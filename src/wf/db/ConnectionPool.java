package wf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class ConnectionPool {

	@SuppressWarnings("unchecked")
	private Hashtable connections;
	private int increment = 3;
	private int initialConnections = 3;
	private String db_url, db_user, db_password;

	@SuppressWarnings("unchecked")
	public ConnectionPool(String driver, String user, String password,
			String db_url) throws SQLException {

		this.db_url = db_url;
		this.db_user = user;
		this.db_password = password;

		connections = new Hashtable();

		System.out.println("JDBC Driver: " + driver);
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("DB URL: " + db_url);
		for (int i = 0; i < initialConnections; i++) {

			connections.put(
					DriverManager.getConnection(db_url, user, password),
					Boolean.FALSE);
		}
	}

	@SuppressWarnings("unchecked")
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
						con = DriverManager.getConnection(db_url, db_user,
								db_password);
					}
					connections.put(con, Boolean.TRUE);

					return con;
				}
			}
		}
		for (int i = 0; i < increment; i++) {
			try {
				Connection c = DriverManager.getConnection(db_url, db_user,
						db_password);
				connections.put(c, Boolean.FALSE);
			} catch (Exception e) {
				System.err.println("getConnection FAILED");
			}

		}
		return getConnection();
	}

	@SuppressWarnings("unchecked")
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