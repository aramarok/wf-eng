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
package wf.db;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.log4j.Logger;

import wf.server.controller.DirectedGraphP;
import wf.server.controller.IBatisWork;
import wf.server.controller.InboxP;
import wf.server.controller.ProcessStack;
import wf.server.controller.WaitingP;
import wf.server.controller.WorkExecutor;
import wf.server.controller.WorkItemP;
import wf.server.controller.WorkflowP;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class Persistence {
  public static final String DB_PROPERTIES = "xflow.properties";
  private static Object guard = new Object();
  private static InitialContext iniCtx;
  private static DataSource ds;
  static Logger log = Logger.getLogger(Persistence.class);

  static public void init () {

  }

  /* static private void _init () {


  Connection conn = null;
  Statement s = null;

  // Create all the necessary tables used by xflow
  try {
  // iniCtx = new InitialContext();
  // ds = (DataSource)iniCtx.lookup(XflowConfig.XFLOW_DATASOURCE());
  ds = getSqlMap().getDataSource();
  }catch( Exception e ){
  log.error ("Cannot get  datasource" + XflowConfig.XFLOW_DATASOURCE(), e );
  return;
  }
  try{
  conn = ds.getConnection();
  s = conn.createStatement();
  String dbCreateScriptResource = XflowConfig.DB_CREATE_SCRIPT();
  URL url = Persistence.class.getClassLoader().getResource( dbCreateScriptResource );
  if( url == null ){
  url = Thread.currentThread().getContextClassLoader().getResource( dbCreateScriptResource );
  }
  if( url != null ){
  String buf = readResource( url );
  String[] statements = buf.split( ";" );
  for (int i = 0; i < statements.length; i++) {
  String statement = statements[i];
  log.info( "Executing:" + statement );
  s.execute ( statement );
  }

  log.info ("XFlow Tables initialized");
  }else{
  throw new Exception( "Resource " + dbCreateScriptResource + " not found" );
  }
  } catch (Exception e) {
  log.info (e.getMessage());
  log.info ("updating workflow table");
  try {
  s.execute ("alter table workflow add column status varchar(32)");
  s.execute ("alter table workflow add column parentWorkflowId int");
  s.execute ("alter table inbox add column timeout bit");
  log.info ("workflow table updated");
  } catch (Exception e2) {
  log.info ("workflow table up-to-date");
  }
  } finally {
  try {
  closeAll( null, s, conn );
  } catch (SQLException e) {
  log.error( "", e );
  }
  }
  }*/

  /*private static String readResource(URL url) throws IOException {
  StringBuffer buf = new StringBuffer();
  InputStream is = url.openStream();
  InputStreamReader r = new InputStreamReader( is );
  char[] charBuf = new char[ 2048 ];
  int len = -1;
  while( ( len = r.read( charBuf ) ) != -1 ){
  buf.append( charBuf, 0, len );
  }
  r.close();
  return buf.toString();
  }*/



  /* public static void execute( JDBCWork work ) throws Exception{
  //later will deal with thread local variables to reuse connection and env
  JDBCWork.JDBCEnv env = new JDBCWork.JDBCEnv();
  env.connection = getConnection();
  try{
  work.execute( env );
  }finally{
  closeAll( env.resultSet, env.statement, env.connection );
  }
  }*/

  public static void execute( IBatisWork work ) throws Exception{
    //later will deal with thread local variables to reuse connection and env
    getWorkExecutor().execute(  work );
  }


  private static SqlMapClient sqlMap = null;

  public static SqlMapClient getSqlMap() throws IOException {
    synchronized( guard ){
      if( sqlMap == null ){
        sqlMap = initSQLSqlMap();
      }
      return sqlMap;
    }
  }

  private static  SqlMapClient initSQLSqlMap() throws IOException {
    String resource =  "xflow/server/controller/sqlmap.xml" ;
    Reader reader = Resources.getResourceAsReader (resource);
    SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
    return sqlMap;
  }


  public static Connection getConnection () throws Exception {
    synchronized( guard ){
      if( ds == null){
        ds = getSqlMap().getDataSource();
      }

      if( ds == null ){
        throw new SQLException( "Cannot create Data Source");
      }
      return ds.getConnection();
    }
  }

  public static void closeAll( ResultSet rs, Statement st, Connection c) throws SQLException {
    try{
      if( rs != null ) rs.close();
    }finally{
      try{
        if( st != null ) st.close();
      }finally{
        if( c != null ) c.close();
      }
    }
  }


  private static WorkflowP workflowP = null;
  private static WorkItemP workItemP = null;
  private static InboxP inboxP = null;
  private static DirectedGraphP directedGraphP = null;
  private static WorkExecutor workExecutor = null;
  private static ProcessStack processStack = null;
  private static WaitingP waitingP = null;

  public static WorkExecutor getWorkExecutor() {
    synchronized( guard ){
      if( workExecutor == null ){
        workExecutor = (WorkExecutor) enhanceInstanceOfClass( WorkExecutor.class);
      }
      return workExecutor;
    }

  }

  public static WaitingP getWaitingP() {
    synchronized( guard ){
      if( waitingP == null ){
        waitingP = (WaitingP) enhanceInstanceOfClass( WaitingP.class );
      }
      return waitingP;
    }

  }

  public static DirectedGraphP getDirectGraphP(){
    synchronized( guard ){
      if( directedGraphP == null){
        directedGraphP = (DirectedGraphP) enhanceInstanceOfClass( DirectedGraphP.class);
      }
      return directedGraphP;
    }

  }


  public static WorkflowP getWorkflowP() {
    synchronized( guard ){
      if( workflowP == null ){
        workflowP = (WorkflowP) enhanceInstanceOfClass( WorkflowP.class );
      }
      return workflowP;
    }

  }

  public static WorkItemP getWorkItemP() {
    synchronized( guard ){
      if( workItemP == null ){
        workItemP = (WorkItemP) enhanceInstanceOfClass( WorkItemP.class );
      }
      return workItemP;
    }

  }

  public static InboxP getInboxP() {
    synchronized( guard ){
      if( inboxP == null ){
        inboxP = (InboxP) enhanceInstanceOfClass( InboxP.class );
      }
      return inboxP;
    }

  }

  public static ProcessStack getProcessStack(){
    synchronized( guard ){
      if( processStack == null ){
        processStack = (ProcessStack) enhanceInstanceOfClass(ProcessStack.class );
      }
      return processStack;
    }
  }


  static ThreadLocal threadSqlMap = new ThreadLocal(){
    protected Object initialValue() {
      return null;
    }
  };

  public static SqlMapClient getThreadSqlMapSession(){
    return (SqlMapClient) threadSqlMap.get();
  }


  private static Map enhancers = new Hashtable();
  private static MethodInterceptor ibatisCallback = new IBatisMethodInterceptor();


  public static Object enhanceInstanceOfClass( Class clazz ) {
    Enhancer en = ( Enhancer ) enhancers.get( clazz.getName() );
    if( en == null ){
      if( log.isDebugEnabled() ){
        log.debug( "Create Enhancer for class::" + clazz.getName() );
      }
      en = new Enhancer();
      en.setSuperclass( clazz );
      en.setCallbacks( new MethodInterceptor[]{ ibatisCallback} );
    //  en.setCallbacks( new MethodInterceptor[]{  new IBatisMethodInterceptor()} );
      enhancers.put( clazz.getName(), en );
    }
    return en.create();
  }



}

