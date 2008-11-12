
package wf.db;

import java.sql.Connection;
import java.util.Properties;

public class Db {

    public static ConnectionPool connectionPool;
    public static String user     = null;
    public static String password = null;
    public static String driver   = null;
    public static String dbURL    = null;

    public static void init (Properties prop) {

        user = prop.getProperty("DBUSER");
        password = prop.getProperty("DBPASSWORD");
        driver = prop.getProperty("DBDRIVER");
        dbURL  = prop.getProperty("DBURL");
    }

    public static Connection getConnection () {

        if (connectionPool == null) {

            if (user == null) {
                System.err.println ("DBUSER property not defined.");
                return null;
            } 
            if (driver == null) {
                System.err.println ("DBDRIVER property not defined.");
                return null;
            } 
            if (password == null) {
                System.err.println ("DBPASSWORD property not defined.");
                return null;
	    } 
            if (dbURL == null) {
                System.err.println ("DBURL property not defined.");
                return null;
            } 

            try {
                connectionPool = new ConnectionPool(driver, 
                                                    user, 
                                                    password,
                                                    dbURL);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println ("Failed to create Connection Pool");
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

    public static void returnConnection (Connection con) {
        connectionPool.returnConnection (con);
    }

}

