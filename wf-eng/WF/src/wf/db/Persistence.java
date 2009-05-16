package wf.db;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.log4j.Logger;
import wf.cfg.Constante;
import wf.server.controller.DirectedGraphP;
import wf.server.controller.IBatisWork;
import wf.server.controller.InboxP;
import wf.server.controller.ProcessStack;
import wf.server.controller.WaitingP;
import wf.server.controller.WorkExecutor;
import wf.server.controller.WorkItemP;
import wf.server.controller.WorkflowP;

public class Persistence {
    public static final String DB_PROPERTIES = Constante.APP_PROPERTIES_FILE_NAME;
    private static DirectedGraphP directedGraphP = null;
    private static DataSource ds;
    private static Map<String, Enhancer> enhancers = new Hashtable<String, Enhancer>();

    private static Object guard = new Object();

    private static MethodInterceptor ibatisCallback = new IBatisMethodInterceptor();

    private static InboxP inboxP = null;

    static Logger log = Logger.getLogger(Persistence.class);

    private static ProcessStack processStack = null;

    private static SqlMapClient sqlMap = null;

    @SuppressWarnings("unchecked")
    static ThreadLocal threadSqlMap = new ThreadLocal() {
	@Override
	protected Object initialValue() {
	    return null;
	}
    };

    private static WaitingP waitingP = null;
    private static WorkExecutor workExecutor = null;
    private static WorkflowP workflowP = null;
    private static WorkItemP workItemP = null;

    public static void closeAll(final ResultSet rs, final Statement st,
	    final Connection c) throws SQLException {
	try {
	    if (rs != null) {
		rs.close();
	    }
	} finally {
	    try {
		if (st != null) {
		    st.close();
		}
	    } finally {
		if (c != null) {
		    c.close();
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public static Object enhanceInstanceOfClass(final Class classType) {
	Enhancer en = enhancers.get(classType.getName());
	if (en == null) {
	    if (log.isDebugEnabled()) {
		log.debug("Create Enhancer for class::" + classType.getName());
	    }
	    en = new Enhancer();
	    en.setSuperclass(classType);
	    en.setCallbacks(new MethodInterceptor[] { ibatisCallback });
	    enhancers.put(classType.getName(), en);
	}
	return en.create();
    }

    public static void execute(final IBatisWork work) throws Exception {
	getWorkExecutor().execute(work);
    }

    public static Connection getConnection() throws Exception {
	synchronized (guard) {
	    if (ds == null) {
		ds = getSqlMap().getDataSource();
	    }

	    if (ds == null) {
		throw new SQLException("Cannot create Data Source");
	    }
	    return ds.getConnection();
	}
    }

    public static DirectedGraphP getDirectGraphP() {
	synchronized (guard) {
	    if (directedGraphP == null) {
		directedGraphP = (DirectedGraphP) enhanceInstanceOfClass(DirectedGraphP.class);
	    }
	    return directedGraphP;
	}

    }

    public static InboxP getInboxP() {
	synchronized (guard) {
	    if (inboxP == null) {
		inboxP = (InboxP) enhanceInstanceOfClass(InboxP.class);
	    }
	    return inboxP;
	}

    }

    public static ProcessStack getProcessStack() {
	synchronized (guard) {
	    if (processStack == null) {
		processStack = (ProcessStack) enhanceInstanceOfClass(ProcessStack.class);
	    }
	    return processStack;
	}
    }

    public static SqlMapClient getSqlMap() throws IOException {
	synchronized (guard) {
	    if (sqlMap == null) {
		sqlMap = initSQLSqlMap();
	    }
	    return sqlMap;
	}
    }

    public static SqlMapClient getThreadSqlMapSession() {
	return (SqlMapClient) threadSqlMap.get();
    }

    public static WaitingP getWaitingP() {
	synchronized (guard) {
	    if (waitingP == null) {
		waitingP = (WaitingP) enhanceInstanceOfClass(WaitingP.class);
	    }
	    return waitingP;
	}

    }

    public static WorkExecutor getWorkExecutor() {
	synchronized (guard) {
	    if (workExecutor == null) {
		workExecutor = (WorkExecutor) enhanceInstanceOfClass(WorkExecutor.class);
	    }
	    return workExecutor;
	}

    }

    public static WorkflowP getWorkflowP() {
	synchronized (guard) {
	    if (workflowP == null) {
		workflowP = (WorkflowP) enhanceInstanceOfClass(WorkflowP.class);
	    }
	    return workflowP;
	}

    }

    public static WorkItemP getWorkItemP() {
	synchronized (guard) {
	    if (workItemP == null) {
		workItemP = (WorkItemP) enhanceInstanceOfClass(WorkItemP.class);
	    }
	    return workItemP;
	}

    }

    static public void init() {
    }

    private static SqlMapClient initSQLSqlMap() throws IOException {
	String resource = Constante.SQL_MAP_LOCATION;
	Reader reader = Resources.getResourceAsReader(resource);
	SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
	return sqlMap;
    }

}
