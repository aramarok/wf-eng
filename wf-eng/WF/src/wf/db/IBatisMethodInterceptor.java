package wf.db;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class IBatisMethodInterceptor implements MethodInterceptor {

    @SuppressWarnings("unchecked")
    public Object intercept(final Object o, final Method method,
	    final Object[] parameters, final MethodProxy methodProxy)
	    throws Throwable {
	boolean close = true;
	Object res;
	try {
	    if (Persistence.threadSqlMap.get() == null) {
		SqlMapClient ss = Persistence.getSqlMap();
		Persistence.threadSqlMap.set(ss);
		if (Persistence.log.isDebugEnabled()) {
		    Persistence.log.debug("START TRANSACTION-"
			    + method.getName() + " of " + o.getClass());
		}
		ss.startTransaction();
	    } else {
		close = false;
	    }

	    res = methodProxy.invokeSuper(o, parameters);

	    if (close) {
		if (Persistence.threadSqlMap.get() != null) {
		    if (Persistence.log.isDebugEnabled()) {
			Persistence.log.debug("COMMIT TRANSACTION-"
				+ method.getName());
		    }
		    Persistence.getThreadSqlMapSession().commitTransaction();
		    Persistence.getThreadSqlMapSession().endTransaction();
		    Persistence.threadSqlMap.set(null);
		}
	    }
	    return res;
	} catch (Throwable e) {
	    if (Persistence.threadSqlMap.get() != null) {
		Persistence.log.debug("ROLLBACK TRANSACTION-"
			+ method.getName());
		Persistence.getThreadSqlMapSession().endTransaction();
	    }
	    Persistence.threadSqlMap.set(null);
	    throw e;
	} finally {
	}

    }

}
