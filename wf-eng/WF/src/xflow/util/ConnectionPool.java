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

public class ConnectionPool {

    private Hashtable connections;
    private int increment = 3;
    private int initialConnections = 3;
    private String dbURL, user, password;

    public ConnectionPool (String driver, 
                           String user, 
                           String password, 
                           String dbURL)
                throws SQLException {

        this.dbURL = dbURL;
        this.user  = user;
        this.password = password;
        
        connections = new Hashtable();
        
        System.out.println ("JDBC Driver: " + driver);
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println ("DB URL: " + dbURL);
        // Put our pool connections in hash table
        for (int i = 0; i < initialConnections; i++) {
           
           connections.put (DriverManager.getConnection (dbURL,
                            user, password), Boolean.FALSE);
        }
    }
    
    public Connection getConnection () throws SQLException {

        Connection con = null;
        Enumeration cons = connections.keys();

        synchronized (connections) {
            while (cons.hasMoreElements()) {
                con = (Connection)cons.nextElement();

                Boolean b = (Boolean)connections.get(con);
                if (b == Boolean.FALSE) {
                    try {
                        con.setAutoCommit(false);
                    } catch (SQLException e) {
                        con = DriverManager.getConnection(dbURL, user,
                                                          password);
                    }

                    // Update hash table to show this one is taken
                    connections.put(con, Boolean.TRUE);

                    return con;
                }
            }
        }


        // If we got here, there are no more free connections, go get some more
        for (int i = 0; i < increment; i++) {
           try {
               Connection c = DriverManager.getConnection(dbURL, user, password);
               connections.put (c, Boolean.FALSE);
           } catch (Exception e) {
               System.err.println ("getConnection FAILED");
           }

        }

        // Recurse to get one of the new connections
        return getConnection();
    }                       

    public void returnConnection (Connection returned) {
        Connection con;
        Enumeration cons = connections.keys();
        while (cons.hasMoreElements()) {
            con = (Connection)cons.nextElement();
            if (con == returned) {
                connections.put(con, Boolean.FALSE);
                break;
            }
        }
    }
}



