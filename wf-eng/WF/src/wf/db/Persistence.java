package wf.db;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.log4j.Logger;

import wf.server.controller.DirectedGraphP;
import wf.server.controller.IBatisWork;
import wf.server.controller.InboxP;
import wf.server.controller.ProcessStack;
import wf.server.controller.WaitingP;
import wf.server.controller.WorkExecutor;
import wf.server.controller.WorkItemP;
import wf.server.controller.WorkflowP;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class Persistence {
	public static final String DB_PROPERTIES = "app.properties";
	private static Object guard = new Object();
	private static InitialContext iniCtx;
	private static DataSource ds;
	static Logger log = Logger.getLogger(Persistence.class);

	static public void init() {

	}

	public static void execute(IBatisWork work) throws Exception {
		getWorkExecutor().execute(work);
	}

	private static SqlMapClient sqlMap = null;

	public static SqlMapClient getSqlMap() throws IOException {
		synchronized (guard) {
			if (sqlMap == null) {
				sqlMap = initSQLSqlMap();
			}
			return sqlMap;
		}
	}

	private static SqlMapClient initSQLSqlMap() throws IOException {
		String resource = "wf/server/controller/sqlmap.xml";
		Reader reader = Resources.getResourceAsReader(resource);
		SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		return sqlMap;
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

	public static void closeAll(ResultSet rs, Statement st, Connection c)
			throws SQLException {
		try {
			if (rs != null)
				rs.close();
		} finally {
			try {
				if (st != null)
					st.close();
			} finally {
				if (c != null)
					c.close();
			}
		}
	}

	private static WorkflowP workflowP = null;
	private static WorkItemP workItemP = null;
	private static InboxP inboxP = null;
	private static DirectedGraphP directedGraphP = null;
	private static WorkExecutor workExecutor = null;
	private static ProcessStack processStack = null;
	private static WaitingP waitingP = null;

	public static WorkExecutor getWorkExecutor() {
		synchronized (guard) {
			if (workExecutor == null) {
				workExecutor = (WorkExecutor) enhanceInstanceOfClass(WorkExecutor.class);
			}
			return workExecutor;
		}

	}

	public static WaitingP getWaitingP() {
		synchronized (guard) {
			if (waitingP == null) {
				waitingP = (WaitingP) enhanceInstanceOfClass(WaitingP.class);
			}
			return waitingP;
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

	static ThreadLocal threadSqlMap = new ThreadLocal() {
		protected Object initialValue() {
			return null;
		}
	};

	public static SqlMapClient getThreadSqlMapSession() {
		return (SqlMapClient) threadSqlMap.get();
	}

	private static Map enhancers = new Hashtable();
	private static MethodInterceptor ibatisCallback = new IBatisMethodInterceptor();

	public static Object enhanceInstanceOfClass(Class clazz) {
		Enhancer en = (Enhancer) enhancers.get(clazz.getName());
		if (en == null) {
			if (log.isDebugEnabled()) {
				log.debug("Create Enhancer for class::" + clazz.getName());
			}
			en = new Enhancer();
			en.setSuperclass(clazz);
			en.setCallbacks(new MethodInterceptor[] { ibatisCallback });
			enhancers.put(clazz.getName(), en);
		}
		return en.create();
	}

}
