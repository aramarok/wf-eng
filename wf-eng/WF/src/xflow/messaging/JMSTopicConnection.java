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
package xflow.messaging;

import java.util.*;
import java.io.*;
import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.Logger;

import xflow.common.*;

public class JMSTopicConnection {

  private static InitialContext iniCtx;
  private static TopicConnection conn = null;
  private static boolean initialized = false;

  private static Logger log = Logger.getLogger(JMSTopicConnection.class);

  static {

    try {
      // Tomcat doesn't seem to find jndi.properties -- so we might have to load manually
      boolean jndiFileFound = true;
      File f = new File("jndi.properties");
      try {
        FileReader fr = new FileReader(f);
      } catch (FileNotFoundException e) {
        jndiFileFound = false;
      }

      if (jndiFileFound) {
        System.out.println ("Loading from jndi.properties file");
        Properties props = new Properties ();
        props.load (new FileInputStream("jndi.properties"));
        String namingFactory = (String)props.get("java.naming.factory.initial");
        String providerUrl = (String)props.get("java.naming.provider.url");
        String factoryUrlPkg = (String)props.get("java.naming.factory.url.pkgs");
        Hashtable env = new Hashtable();
        env.put ("java.naming.factory.initial", namingFactory);
        env.put ("java.naming.provider.url", providerUrl);
        env.put ("java.naming.factory.url.pkgs", factoryUrlPkg);
        iniCtx = new InitialContext(env);
      } else {
        iniCtx = new InitialContext();
      }

      Object tmp = iniCtx.lookup(XflowConfig.XFLOW_CONNECTION_FACTORY());
      TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
      conn = tcf.createTopicConnection();
    } catch (Exception e) {
      if( log.isDebugEnabled() ){
        log.debug( "JMSTopicConnection.static.init", e  );
      }
    }
  }

  public static void initialize() throws JMSException {

    if (initialized) {
      return;
    }
    initialized = true;
    JMSShutdownHook shook = new JMSShutdownHook();
    Runtime.getRuntime().addShutdownHook (shook);
    start();
  }

  public static TopicConnection getConnection () {
    return conn;
  }

  public static InitialContext getInitialContext() {
    return iniCtx;
  }

  public static void start() throws JMSException {
    log.info ("Starting connection");
    conn.start();
  }

  public static void stop() throws JMSException {
    log.info ("Stopping connection");
    conn.stop();
  }

  public static void close() throws JMSException {
    log.info ("Closing connection");
    conn.close();
  }
}
