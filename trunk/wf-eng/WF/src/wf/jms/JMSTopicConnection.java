
package wf.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import wf.cfg.AppConfig;

public class JMSTopicConnection {

  private static InitialContext iniCtx;
  private static TopicConnection conn = null;
  private static boolean initialized = false;

  private static Logger log = Logger.getLogger(JMSTopicConnection.class);

  static {

    try {
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

      Object tmp = iniCtx.lookup(AppConfig.XFLOW_CONNECTION_FACTORY());
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
