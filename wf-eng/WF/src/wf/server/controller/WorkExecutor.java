package wf.server.controller;

import com.ibatis.sqlmap.client.SqlMapClient;

import wf.db.Persistence;


public class WorkExecutor {

   public void execute( IBatisWork work ) throws Exception{
      work.execute( Persistence.getThreadSqlMapSession() );
  }

}
