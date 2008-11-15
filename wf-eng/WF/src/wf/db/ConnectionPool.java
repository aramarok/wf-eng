package wf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class ConnectionPool {

	private Hashtable connections;
	private int increment = 3;
	private int initialConnections = 3;
	private String dbURL, user, password;

	public ConnectionPool(String driver, String user, String password,
			String dbURL) throws SQLException {

		this.dbURL = dbURL;
		this.user = user;
		this.password = password;

		connections = new Hashtable();

		System.out.println("JDBC Driver: " + driver);
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("DB URL: " + dbURL);
		for (int i = 0; i < initialConnections; i++) {

			connections.put(DriverManager.getConnection(dbURL, user, password),
					Boolean.FALSE);
		}
	}

	public Connection getConnection() throws SQLException {

		Connection con = null;
		Enumeration cons = connections.keys();

		synchronized (connections) {
			while (cons.hasMoreElements()) {
				con = (Connection) cons.nextElement();

				Boolean b = (Boolean) connections.get(con);
				if (b == Boolean.FALSE) {
					try {
						con.setAutoCommit(false);
					} catch (SQLException e) {
						con = DriverManager
								.getConnection(dbURL, user, password);
					}
					connections.put(con, Boolean.TRUE);

					return con;
				}
			}
		}
		for (int i = 0; i < increment; i++) {
			try {
				Connection c = DriverManager.getConnection(dbURL, user,
						password);
				connections.put(c, Boolean.FALSE);
			} catch (Exception e) {
				System.err.println("getConnection FAILED");
			}

		}
		return getConnection();
	}

	public void returnConnection(Connection returned) {
		Connection con;
		Enumeration cons = connections.keys();
		while (cons.hasMoreElements()) {
			con = (Connection) cons.nextElement();
			if (con == returned) {
				connections.put(con, Boolean.FALSE);
				break;
			}
		}
	}
}
