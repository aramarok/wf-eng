

package wf.server.controller;

import org.apache.log4j.Logger;

import wf.db.Persistence;
import wf.exceptions.WorkFlowException;
import wf.model.Node;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingP {

  private static Logger log = Logger.getLogger(WaitingP.class);

  public  void addProcess (Integer workflowId, int destNodeId, int fromNodeId) throws WorkFlowException, SQLException {

    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    params.put( "destNodeId", new Integer( destNodeId) );
    params.put( "fromNodeId", new Integer( fromNodeId) );
    Persistence.getThreadSqlMapSession().insert( "insertWaitingRecord", params );
  }

  public  void removeProcesses (List fromNodes, Integer workflowId) throws WorkFlowException, SQLException {
    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    for (int i = 0; i < fromNodes.size(); i++) {
       Node node = (Node) fromNodes.get(i);
      params.put( "fromNodeId", new Integer( node.getNodeId() ) );
      Persistence.getThreadSqlMapSession().delete( "deleteProcFromWaiting", params );
    }

  }

  public  void removeProcesses (Integer wfId) throws WorkFlowException, SQLException {
    Persistence.getThreadSqlMapSession().delete( "removeProcessesForWorkflow", wfId );

  }

  public  boolean allProcessesArrived (List fromNodes, Integer workflowId, int destNodeId)
      throws WorkFlowException, SQLException {
    int count = fromNodes.size();
    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    params.put( "destNodeId", new Integer( destNodeId ) );
    List nodeIDs = new ArrayList();
    for (int i = 0; i < count; i++) {
      Node node = (Node) fromNodes.get(i);
      nodeIDs.add( node.getNodeIdAsInteger() );
    }
    params.put( "fromNodeIDs", nodeIDs );
    int rcount = 0;
    Integer cnt = (Integer) Persistence.getThreadSqlMapSession().queryForObject( "countArrived", params );
    if( cnt != null ) rcount = cnt.intValue();
    log.info ("Nodes arrived count: " + rcount + " fromNodes count = " + count);
    return rcount == count;
  }


}
