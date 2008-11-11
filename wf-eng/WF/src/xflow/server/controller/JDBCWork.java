package xflow.server.controller;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * User: kosta
 * Date: Jun 20, 2004
 * Time: 10:51:19 PM
 */
public abstract class JDBCWork {

  protected Object result;

  public Object getResult() {
    return result;
  }


  public abstract void execute( JDBCEnv env )throws Exception;


  public static class JDBCEnv{
    public Connection connection;
    public Statement statement;
    public ResultSet resultSet;
  }
}
