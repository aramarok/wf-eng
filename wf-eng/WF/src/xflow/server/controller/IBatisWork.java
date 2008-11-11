package xflow.server.controller;

import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * User: kosta
 * Date: Jun 21, 2004
 * Time: 9:55:27 PM
 */
public abstract class IBatisWork {

  protected Object result;

    public Object getResult() {
      return result;
    }


    public abstract void execute( SqlMapClient sqlMap )throws Exception;


}
