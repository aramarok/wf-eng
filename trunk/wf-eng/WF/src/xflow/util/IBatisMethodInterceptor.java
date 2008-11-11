package xflow.util;

import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;

import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 9:00:05 PM
 */
public class IBatisMethodInterceptor  implements MethodInterceptor{

  int c = 0;

 private void inc(){
    c++;
    System.out.println("inc c = " + c);
  }

  private void dec(){
    c--;
    System.out.println("dec c = " + c);
  }

  public Object intercept( Object o, Method method, Object[] parameters, MethodProxy methodProxy ) throws Throwable {
    boolean close = true;
    Object res;
    try {
      // inc();
      if(  Persistence.threadSqlMap.get() == null ){
        SqlMapClient ss = Persistence.getSqlMap();
        Persistence.threadSqlMap.set( ss );
        if( Persistence.log.isDebugEnabled() ){
          Persistence.log.debug("START TRANSACTION-" + method.getName() + " of " + o.getClass() );
        }
        ss.startTransaction();
      }else{
        close = false;
      }

      res = methodProxy.invokeSuper( o, parameters );

      if( close ){
        if( Persistence.threadSqlMap.get() != null ){
          if( Persistence.log.isDebugEnabled() ){
            Persistence.log.debug("COMMIT TRANSACTION-" + method.getName());
          }
          Persistence.getThreadSqlMapSession().commitTransaction();
          //Persistence.getThreadSqlMapSession().flushDataCache();
          Persistence.getThreadSqlMapSession().endTransaction();
          // getThreadSqlMapSession().close();
          Persistence.threadSqlMap.set( null );
        }
      }
      return res;
    } catch (Throwable e) {
      if( Persistence.threadSqlMap.get() != null ){
        Persistence.log.debug("ROLLBACK TRANSACTION-" + method.getName());
        //Persistence.getThreadSqlMapSession().flushDataCache();
        Persistence.getThreadSqlMapSession().endTransaction();
        //  getThreadSqlMapSession().close();
      }
      Persistence.threadSqlMap.set( null );
      throw e;
    } finally{
      //  dec();
    }

  }

}


