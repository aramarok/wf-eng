
package wf.cfg;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.net.URL;
import java.io.InputStream;

public class XflowConfig {

  public static final String XFLOW_CONFIG = "xflow.properties";

  private static Logger log = Logger.getLogger(XflowConfig.class);

  private static XflowConfig _xXflowConfig;

  private Properties config = new Properties();

  private XflowConfig(){
    try{
      URL url = this.getClass().getClassLoader().getResource( XFLOW_CONFIG );
      if( url == null ){
        url = Thread.currentThread().getContextClassLoader().getResource( XFLOW_CONFIG );
      }
      if( url != null ){
        InputStream inputStream = url.openStream();
        config.load( inputStream );
        inputStream.close();
      }
    } catch( Exception e){
      e.printStackTrace();
    }

  }

  public synchronized static XflowConfig getInstance(){
    if( _xXflowConfig == null ){
      _xXflowConfig = new XflowConfig();
    }
    return _xXflowConfig;
  }


  public String  get(String key,String def) {
    String v = (String) config.get( key );
    if( v == null ) {
      log.info( "key " + key + " was not found in " + XFLOW_CONFIG + ". Will use default value [" + def +"]" );
      put( key, def );
      return def;
    }
    return v;
  }

  public int getInt(String key, int i) {
    String v = (String) config.get( key );
    if( v == null ) {
      log.info( "key " + key + " was not found in " + XFLOW_CONFIG + ". Will use default value [" + i +"]" );
      putInt( key, i );
      return i;
    }
    return Integer.parseInt( v );
  }

  public void put( String key, String val ){
    config.put( key, val );
  }

  public void putInt(String key, int i) {
    config.put( key, String.valueOf( i ) );
  }
  public static final	String XFLOW_CONNECTION_FACTORY(){
    return getInstance().get( "XFLOW_CONNECTION_FACTORY",  "ConnectionFactory" );
  }

  public static final String XFLOW_TOPIC(){
    return getInstance().get( "XFLOW_TOPIC", "topic/inboxTopic");
  }
  public static final String XFLOW_EVENT_TOPIC(){
    return getInstance().get( "XFLOW_EVENT_TOPIC", "topic/XFLOW.EVENT");
  }
  public static final String XFLOW_QUEUE(){
    return getInstance().get( "XFLOW_QUEUE", "queue/xflowReplyQueue");
  }
  public static final	String WORKFLOWENGINE_QUEUE(){
    return getInstance().get("WORKFLOWENGINE_QUEUE", "queue/WorkflowEngineQueue");
  }
  public static final	String XFLOW_DATASOURCE(){
    return getInstance().get("XFLOW_DATASOURCE", "java:/XflowDS");
  }

  public static String DB_CREATE_SCRIPT() {
    return getInstance().get( "CREATEDB-SQL","conf/create_db.sql");
  }
  
}
