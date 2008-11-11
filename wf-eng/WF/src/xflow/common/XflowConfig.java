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
package xflow.common;

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

  // JBOSS
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

  // Weblogic 8.1
  /*
  public static final	String XFLOW_CONNECTION_FACTORY= "QCF";
  public static final String XFLOW_TOPIC = "testTopic";
  public static final String XFLOW_QUEUE = "testQueue";
  public static final	String WORKFLOWENGINE_QUEUE= "WorkflowEngineQueue";
  public static final	String XFLOW_DATASOURCE= "DefaultDS";
  */
}
