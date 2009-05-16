package wf.server.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class JDBCWork {

    public static class JDBCEnv {
	public Connection connection;
	public ResultSet resultSet;
	public Statement statement;
    }

    protected Object result;

    public abstract void execute(JDBCEnv env) throws Exception;

    public Object getResult() {
	return this.result;
    }
}
