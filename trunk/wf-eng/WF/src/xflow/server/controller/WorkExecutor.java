package xflow.server.controller;

import com.ibatis.sqlmap.client.SqlMapClient;
import xflow.util.Persistence;

/**
 * User: kosta
 * Date: Jul 11, 2004
 * Time: 7:46:04 PM
 */
public class WorkExecutor {

   public void execute( IBatisWork work ) throws Exception{
    //later will deal with thread local variables to reuse connection and env
      work.execute( Persistence.getThreadSqlMapSession() );
  }

}
