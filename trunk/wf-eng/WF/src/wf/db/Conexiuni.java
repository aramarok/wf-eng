package wf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class Conexiuni {

    @SuppressWarnings("unchecked")
    private final Hashtable connections;
    private final String db_url, db_user, db_password;
    private final int increment = 3;
    private final int initialConnections = 3;

    @SuppressWarnings("unchecked")
    public Conexiuni(final String driver, final String user,
	    final String password, final String db_url) throws SQLException {

	this.db_url = db_url;
	this.db_user = user;
	this.db_password = password;

	this.connections = new Hashtable();

	System.out.println("JDBC Driver: " + driver);
	try {
	    Class.forName(driver).newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	System.out.println("DB URL: " + db_url);
	for (int i = 0; i < this.initialConnections; i++) {

	    this.connections.put(DriverManager.getConnection(db_url, user,
		    password), Boolean.FALSE);
	}
    }

    @SuppressWarnings("unchecked")
    public Connection getConnection() throws SQLException {

	Connection con = null;
	Enumeration cons = this.connections.keys();

	synchronized (this.connections) {
	    while (cons.hasMoreElements()) {
		con = (Connection) cons.nextElement();

		Boolean b = (Boolean) this.connections.get(con);
		if (b == Boolean.FALSE) {
		    try {
			con.setAutoCommit(false);
		    } catch (SQLException e) {
			con = DriverManager.getConnection(this.db_url,
				this.db_user, this.db_password);
		    }
		    this.connections.put(con, Boolean.TRUE);

		    return con;
		}
	    }
	}
	for (int i = 0; i < this.increment; i++) {
	    try {
		Connection c = DriverManager.getConnection(this.db_url,
			this.db_user, this.db_password);
		this.connections.put(c, Boolean.FALSE);
	    } catch (Exception e) {
		System.err.println("getConnection FAILED");
	    }

	}
	return this.getConnection();
    }

    @SuppressWarnings("unchecked")
    public void returnConnection(final Connection returned) {
	Connection con;
	Enumeration cons = this.connections.keys();
	while (cons.hasMoreElements()) {
	    con = (Connection) cons.nextElement();
	    if (con == returned) {
		this.connections.put(con, Boolean.FALSE);
		break;
	    }
	}
    }
}