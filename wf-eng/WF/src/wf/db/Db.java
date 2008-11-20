package wf.db;

import java.sql.Connection;
import java.util.Properties;

import wf.client.WFConstants;

public class Db {

	public static ConnectionPool connectionPool;
	public static String db_userName = null;
	public static String db_password = null;
	public static String db_driver = null;
	public static String db_url = null;

	public static void init(Properties prop) {
		db_userName = prop.getProperty(WFConstants.DB_USERNAME_FIELD);
		db_password = prop.getProperty(WFConstants.DB_PASSWORD_FIELD);
		db_driver = prop.getProperty(WFConstants.DB_DRIVER_FIELD);
		db_url = prop.getProperty(WFConstants.DB_URL_FIELD);
	}

	public static Connection getConnection() {
		if (connectionPool == null) {

			if (db_userName == null) {
				System.err.println("DB_USERNAME property not defined.");
				return null;
			}
			if (db_driver == null) {
				System.err.println("DB_DRIVER property not defined.");
				return null;
			}
			if (db_password == null) {
				System.err.println("DB_PASSWORD property not defined.");
				return null;
			}
			if (db_url == null) {
				System.err.println("DB_URL property not defined.");
				return null;
			}

			try {
				connectionPool = new ConnectionPool(db_driver, db_userName, db_password, db_url);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to create Connection Pool");
			}
		}

		try {
			Connection con = connectionPool.getConnection();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void returnConnection(Connection con) {
		connectionPool.returnConnection(con);
	}

}