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

package wf.server.controller;

import org.apache.log4j.Logger;

import wf.db.Persistence;
import wf.exceptions.XflowException;
import wf.model.WorkflowModel;
import wf.model.WorkflowState;
import wf.server.util.ProcessWithTimeout;
import wf.util.Util;


import java.sql.SQLException;
import java.util.*;


public class WorkflowP {

  private static Logger log = Logger.getLogger(WorkflowP.class);

  public void insertOrtab ( Integer workflowId, int nodeId) throws SQLException {
    Map params = new Hashtable();
    params.put("workflowId",  ( workflowId ));
    params.put("nodeId" , new Integer( nodeId ) );
    Persistence.getThreadSqlMapSession().insert( "insertOrtab",params  );
  }

  public boolean existsOrtab (Integer workflowId, int nodeId) throws SQLException {
    Map params = new Hashtable();
    params.put("workflowId",  ( workflowId ));
    params.put("nodeId" , new Integer( nodeId ) );
    Integer wfId = (Integer) Persistence.getThreadSqlMapSession().queryForObject( "selectOrtab",params  );
    return ( wfId != null );
  }

  /**
   *
   * @param graphId
   * @param workflowName
   * @param initiator
   * @param parentWorkflowId
   * @return ID of the new Workflow
   * @throws XflowException
   */
  public Integer saveNewWorkflow (final int graphId, String workflowName, final String initiator, final int parentWorkflowId)
      throws XflowException {
    Map params = new Hashtable();
    if (parentWorkflowId != -1) {
      params.put( "pWfId", new Integer( parentWorkflowId ) );
    }
    params.put("graphId", new Integer( graphId ));
    params.put("initiator" , initiator);
    Date timeStarted = new Date();
    params.put("timeStarted", timeStarted );
    params.put("timeStartedSql", new java.sql.Date(timeStarted.getTime()));
    Object result = null;
    try {
      result = Persistence.getThreadSqlMapSession().insert( "insertWorkflow",params  );
    } catch (SQLException e) {
      throw new XflowException( e );
    }
    return ((Integer) result );
  }

  public int getGraphId (final Integer workflowId) throws SQLException {
    Object  result = Persistence.getThreadSqlMapSession().queryForObject( "selectWorkflowGid", workflowId  );
    return ((Integer)result).intValue();
  }

  public List getActiveWorkflows () throws  SQLException {
    return  Persistence.getThreadSqlMapSession().queryForList( "activeWorkflowIDs", null  );
  }

  public List getSuspendedWorkflows () throws  SQLException {
    return   Persistence.getThreadSqlMapSession().queryForList( "suspendedWorkflowIDs", null );
  }

  public List getAllWorkflows () throws SQLException {
    return Persistence.getThreadSqlMapSession().queryForList( "allWorkflowIDs", null );
  }

  public  List getWorkflowsByName (final String name) throws SQLException {
    return   Persistence.getThreadSqlMapSession().queryForList( "getWorkflowIDsByName", name  );
  }

  /**
   *
   * @return list of {@link WorkflowModel WorkflowModel}
   * @throws SQLException
   */
  public  List getModels () throws SQLException {
    return  Persistence.getThreadSqlMapSession().queryForList( "getModels",null ) ;
  }

  public  void abortWorkflow (final Integer workflowId) throws SQLException {
    Map params = new Hashtable();
    params.put("workflowId",workflowId );
    params.put("timeEnded", new Date() );
    Persistence.getThreadSqlMapSession().update( "abortWorkflow",params  );
  }

  public  void suspendWorkflow (final Integer workflowId) throws XflowException, SQLException {
    Persistence.getThreadSqlMapSession().update( "suspendWorkflow",workflowId   );
  }

  public void resumeWorkflow (final Integer workflowId) throws SQLException {
    Persistence.getThreadSqlMapSession().update( "resumeWorkflow",workflowId   );
  }

  public  WorkflowState getWorkflowState (Integer workflowId) throws XflowException, SQLException {

    WorkflowState state = (WorkflowState) Persistence.getThreadSqlMapSession().queryForObject( "getWorkflowState", workflowId );
    if ( state== null ) return null;
    if (state.isActive )  state.timeEnded = null;
    if (state.state == null || state.state.equals("")) {
      if (state.isActive) {
        state.state = "RUNNING";
      } else {
        state.state = "COMPLETED";
      }
    }

    // Load the active processes
    List procStateRecords = Persistence.getThreadSqlMapSession().queryForList( "selectProcessStateRecords", workflowId );
    for (Iterator j = procStateRecords.iterator(); j.hasNext();) {
      ProcessStateRec stateRec = (ProcessStateRec) j.next();
      state.activeProcesses.add ( stateRec.makeProcessState() );
    }

    // Load the workflow variables
    List wfVars = Persistence.getThreadSqlMapSession().queryForList( "selectWorkwlowVariables", workflowId );
    for (Iterator j = wfVars.iterator(); j.hasNext();) {
      WorkflowVariable wfVar = (WorkflowVariable) j.next();
      state.variables.put (wfVar.getName(), Util.objFromXML( wfVar.getValue() ));
    }
    return state;
  }

  public  void setVariable (Integer workflowId, String name, Object value) throws  SQLException {
    if( log.isDebugEnabled() ){
      log.debug( "Hex Encoding: " + value);
    }
    String valueStr = Util.objToXML(value);
    if( log.isDebugEnabled() ){
      log.debug ("String to be stored: " + valueStr);
    }
    Map params = new Hashtable();
    params.put( "workflowId",  workflowId );
    params.put( "name", name );
    params.put( "varVal", valueStr );
    Persistence.getThreadSqlMapSession().delete( "deleteWorkflowVar", params );
    Persistence.getThreadSqlMapSession().insert( "insertWorkflowVar", params );
  }

  public  Object getVariable (final Integer workflowId, final String name) throws SQLException {
    Map params = new Hashtable();
    params.put("workflowId", workflowId );
    params.put("name", name );
    Object result = Persistence.getThreadSqlMapSession().queryForObject( "getVariable",params  );
    if( result == null) return null;
    return Util.objFromXML((String ) result);
  }

  public  void setCompleted (final Integer workflowId) throws SQLException {
    Map params = new Hashtable();
    params.put("workflowId", (workflowId));
    params.put("timeEnded", new Date() );
    Persistence.getThreadSqlMapSession().update( "setCompleted",params  );
  }

  public boolean isCompleted (final int workflowId) throws SQLException {
    Object res = Persistence.getThreadSqlMapSession().queryForObject( "isCompleted", new Integer(workflowId )  );
    return (  res != null );
  }

  public  List getProcessesWithTimeouts() throws XflowException, SQLException {
    List v = new ArrayList();
    String pName = null;
    Integer    pId;
    int    pToutMinutes = -1;
    String pThdl = null;

    List nodesWithTimeout = Persistence.getThreadSqlMapSession().queryForList( "selectNodesWithTimeout", null );
    for (Iterator j = nodesWithTimeout.iterator(); j.hasNext();) {
      HashMap map = (HashMap) j.next();
      pName = (String) map.get("name");
      pId   = ((Integer)map.get("nid"));
      String vstr = (String) map.get("value");
      Object value = Util.objFromXML(vstr);
      Integer iValue = (Integer)value;
      pToutMinutes = iValue.intValue();

      pThdl = null;
      vstr = (String) Persistence.getThreadSqlMapSession().queryForObject( "selectTimeoutHandler", pId );
      if( vstr != null ){
        value = Util.objFromXML(vstr);
        pThdl = (String)value;
      }

      List workflowIDs = Persistence.getThreadSqlMapSession().queryForList( "selectWorkflowIdByNodeId", pId );
      for (Iterator k = workflowIDs.iterator(); k.hasNext();) {
        Integer wf_id = (Integer) k.next();
        int wfId = wf_id.intValue();
        ProcessWithTimeout pto = new ProcessWithTimeout();
        pto.workflowId = wfId;
        pto.processName = pName;
        pto.timeoutMinutes = pToutMinutes;
        pto.timeoutHandler = pThdl;
        v.add (pto);
      }

    }

    return v;
  }
}
