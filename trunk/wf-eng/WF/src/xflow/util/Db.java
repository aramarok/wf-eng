/*
 * ====================================================================
 *
 * XFLOW - Process Management System
 * Copyright (C) 2003 Rob Tan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "XFlow" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact rcktan@yahoo.com
 * 
 * 4. Products derived from this software may not be called "XFlow", nor
 *    may "XFlow" appear in their name, without prior written permission
 *    from the XFlow Project Management (rcktan@yahoo.com)
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      XFlow Project (http://xflow.sourceforge.net/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://xflow.sourceforge.net/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the XFlow Project and was originally 
 * created by Rob Tan (rcktan@yahoo.com)
 * For more information on the XFlow Project, please see:
 *           <http://xflow.sourceforge.net/>.
 * ====================================================================
 */
package xflow.util;

import java.sql.*;
import java.util.*;

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

