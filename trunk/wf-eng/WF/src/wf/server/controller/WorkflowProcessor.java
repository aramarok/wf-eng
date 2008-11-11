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
import org.jaxen.JaxenException;
import org.xml.sax.SAXException;

import wf.cfg.XflowConfig;
import wf.db.Persistence;
import wf.exceptions.XflowException;
import wf.jms.EventsPublisher;
import wf.jms.JMSPublisher;
import wf.jms.MessageProperty;
import wf.model.DirectedGraph;
import wf.model.Node;
import wf.model.WorkItem;
import wf.model.WorkflowModel;
import wf.model.WorkflowState;
import wf.server.util.PopNode;
import wf.xml.XflowXMLParser;
import xflow.common.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.*;
import java.sql.SQLException;

public class WorkflowProcessor {

  private static Logger log = Logger.getLogger(WorkflowProcessor.class);
  private static Map graphsByGraphId = new HashMap();
  private static Map graphsByNameAndVersion = new HashMap();
  private static List activeWorkflows;
  private static List suspendedWorkflows;
  private static EventsPublisher eventsPublisher = new EventsPublisher();
  private static WorkflowP workflowP = Persistence.getWorkflowP();
  private static DirectedGraphP directedGraphP = Persistence.getDirectGraphP();
  private static InboxP inboxP = Persistence.getInboxP();
  private static WaitingP waitingP = Persistence.getWaitingP();
  private static ProcessStack processStack = Persistence.getProcessStack();

  public static final int MODE_LOCAL = 1;
  public static final int MODE_SERVER = 0;

  private static Object guard = new Object();

  private int mode = MODE_SERVER;



  static WorkflowProcessor instance;


  public void setEventsEnabled( boolean enabled ){
    eventsPublisher.setDoNotPublish( !enabled );
  }


  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public static  WorkflowProcessor getInstance() throws SQLException {
    synchronized( guard ){
      if( instance == null ){
        instance = (WorkflowProcessor) Persistence.enhanceInstanceOfClass( WorkflowProcessor.class );
        instance.init();
      }
      return instance;
    }
  }


  void init() throws SQLException {
    // Load all active workflows
    activeWorkflows = workflowP.getActiveWorkflows();
    // Load all suspended workflows
    suspendedWorkflows = workflowP.getSuspendedWorkflows();
    // Start timeout thread -- disable timeout handling for now
    //new TimeoutDetector().start();

  }

  //
  // Workflow model-oriented services
  //

  public  void deployModel (String xml, String type, String user) throws XflowException {

    DirectedGraph dg = null;

    if ( WorkflowEngine.FLOW_TYPE_XFLOW.equals( type ) ) {
      try {
        dg = XflowXMLParser.parse (xml);
        dg.validate();
      } catch (Exception e) {
        throw new XflowException ("Failed to parse XML : " + e.getMessage(), e);
      }
      try {
        dg.saveDB();
      } catch (Exception e) {
        throw new XflowException ("Failed to save model to database ", e);
      }

      String modelName = dg.getName();
      int graphId = dg.getGraphId();
      //Node startNode = dg.getRootNode();
      log.info ("Saved model: " + modelName);
      log.info ("graphId is: " + graphId);
      graphsByGraphId.put (new Integer(graphId), dg);
      String nameVers = dg.getName() + dg.getVersion();
      graphsByNameAndVersion.put (nameVers, dg);

      try {
        eventsPublisher.publishModelDeployedEvent (modelName, dg.getVersion(), user);
      } catch (Throwable e) {
        log.warn ("Failed to publish event");
      }
    } else {
      throw new XflowException ("Type: " + type + " is not supported.");
    }
  }

  /**
   *
   * @return list of {@link WorkflowModel WorkflowModel}
   * @throws SQLException
   */
  public  List getModels () throws SQLException {
    return workflowP.getModels();
  }

  public  boolean validateProcess (String workflowName, int workflowVersion, String processName)
      throws XflowException, SQLException {

    boolean result = false;
    DirectedGraph dg = getGraphByNameAndVersion (workflowName, workflowVersion);
    Node node = dg.getNode(processName);
    if (node != null) {
      result = true;
    }

    return result;
  }

  public  List getProcessNodes (Integer wfId) throws SQLException, XflowException {

    int gid = workflowP.getGraphId (wfId);
    DirectedGraph dg = getGraphByGraphId (gid);
    List nodes = null;
    if (dg != null) {
      nodes = dg.getNodes(Node.PROCESS);
    } else {
      throw new XflowException ("Can't find graph for workflow id: " + wfId);
    }
    return nodes;
  }

  public  Node getNodeByName (String workflowName, int workflowVersion,
                              String nodeName) throws XflowException, SQLException {
    Node node = null;
    DirectedGraph dg = getGraphByNameAndVersion (workflowName, workflowVersion);
    if (dg != null) {
      node = dg.getNode(nodeName);
    } else {
      throw new XflowException ("Can't find graph for " + workflowName +
          ", version: " + workflowVersion);
    }
    return node;
  }


  //
  // Workflow-oriented services
  //

  public  Integer startWorkflow (String workflowName, int version, WorkItem witem, String initiator)
      throws XflowException, SQLException, JaxenException, IOException, ParserConfigurationException, SAXException {
    Integer workflowId = null;
    DirectedGraph dg = getGraphByNameAndVersion (workflowName, version);

    // Save the work item to db
    log.info ("Saving workitem: " + witem);
    Persistence.getWorkItemP().saveDB(witem);

    int graphId = dg.getGraphId();
    Node startNode = dg.getRootNode();

    log.info ("GraphId: " + graphId);

    workflowId =  Persistence.getWorkflowP().saveNewWorkflow(graphId, workflowName, initiator, -1);

    activeWorkflows.add(workflowId);
    witem.setWorkflowId ( workflowId );

    log.info ("startNode is :" + startNode);
    transitionFromStartNode (graphId, workflowName, version, startNode, witem);

    try {
      int thisVersion = -1;
      if (version == -1) {
        thisVersion = directedGraphP.getLatestVersionNumber (workflowName);
      }
      eventsPublisher.publishWorkflowStartedEvent (workflowName, thisVersion, workflowId,new Integer( -1), initiator, witem);
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }

    return workflowId;
  }

  private  Integer startContaineeWorkflow (String workflowName, int version, WorkItem _witem, String initiator,
                                           Integer parentWorkflowId) throws XflowException, SQLException, JaxenException, IOException, ParserConfigurationException, SAXException {

    Integer workflowId = null;
    DirectedGraph dg = getGraphByNameAndVersion (workflowName, version);

    // First we must clone the work item to be associated with new workflow instance
    WorkItem clonedWItem = _witem.makeCopy();
    // Save the cloned work item to db
    log.info ("Saving cloned workitem: " + clonedWItem);
    Persistence.getWorkItemP().saveDB(clonedWItem);
    log.info ("Cloned workitem: " + clonedWItem);

    int graphId = dg.getGraphId();
    Node startNode = dg.getRootNode();


    workflowId =  workflowP.saveNewWorkflow(graphId, workflowName, initiator, parentWorkflowId.intValue());
    log.info ("Started containee workflow. Workflow Id is: " + workflowId);

    activeWorkflows.add ( workflowId );
    clonedWItem.setWorkflowId ( workflowId );

    log.info ("startNode is :" + startNode);
    transitionFromStartNode (graphId, workflowName, version, startNode, clonedWItem);

    try {
      int thisVersion = -1;
      if (version == -1) {
        thisVersion = directedGraphP.getLatestVersionNumber (workflowName);
      }
      eventsPublisher.publishWorkflowStartedEvent (workflowName, thisVersion, new Integer( graphId),
          parentWorkflowId, initiator, clonedWItem);
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }

    return workflowId;
  }

  public  void abortWorkflow (Integer wfId, String user) throws XflowException, SQLException {

    int graphId = workflowP.getGraphId(wfId );
    DirectedGraph dg = getGraphByGraphId (graphId);

    Integer key = wfId;
    activeWorkflows.remove (key);
    suspendedWorkflows.remove (key);
    workflowP.abortWorkflow (wfId);

    inboxP.removeWorkItems ( wfId);
    waitingP.removeProcesses (wfId);

    try {
      String workflowName = dg.getName();
      int version = dg.getVersion();
      eventsPublisher.publishWorkflowAbortedEvent (workflowName, version, wfId, user);
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }
  }

  public  void suspendWorkflow (Integer wfId) throws XflowException, SQLException {
    int graphId = workflowP.getGraphId(wfId);
    DirectedGraph dg = getGraphByGraphId (graphId);

    Integer key = wfId;
    if (suspendedWorkflows.contains( key) ) {
      throw new XflowException ("Workflow is already suspended");
    }
    suspendedWorkflows.add (key);
    workflowP.suspendWorkflow (wfId);

    try {
      String workflowName = dg.getName();
      int version = dg.getVersion();
      eventsPublisher.publishWorkflowSuspendedEvent (workflowName, version,
          wfId,"system");
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }
  }

  public  void resumeWorkflow (Integer wfId) throws XflowException, SQLException {

    int graphId = workflowP.getGraphId(wfId );
    DirectedGraph dg = getGraphByGraphId (graphId);

    Integer key = wfId;
    if (!suspendedWorkflows.contains(key) ) {
      throw new XflowException ("Workflow is not currently suspended");
    }
    suspendedWorkflows.remove (key);
    workflowP.resumeWorkflow (wfId);

    try {
      String workflowName = dg.getName();
      int version = dg.getVersion();
      eventsPublisher.publishWorkflowResumedEvent (workflowName, version,
          wfId, "system");
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }
  }

  public  WorkflowState getWorkflowState (Integer wfId) throws XflowException, SQLException {
    return workflowP.getWorkflowState (wfId);
  }

  public  void setVariable (Integer workflowId, String name, Object value) throws XflowException, SQLException {
    int graphId = workflowP.getGraphId(workflowId);
    DirectedGraph dg = getGraphByGraphId (graphId);

    Integer key = workflowId;
    if (!activeWorkflows.contains(key) ) {
      throw new XflowException ("Workflow ID is not active or valid");
    }
    workflowP.setVariable (workflowId, name, value);

    try {
      String workflowName = dg.getName();
      int version = dg.getVersion();
      eventsPublisher.publishVariableUpdatedEvent (workflowName, version, workflowId, name, value);
    } catch (XflowException e) {
      log.warn ("Failed to publish event");
    }
  }

  public  Object getVariable (Integer workflowId, String name) throws  SQLException {
    return workflowP.getVariable (workflowId, name);
  }

  public  List getActiveWorkflows () throws XflowException, SQLException {
    List v = new ArrayList();
    WorkflowState ws = null;
    List wflowIds = workflowP.getActiveWorkflows();
    Iterator itr = wflowIds.iterator();
    while (itr.hasNext()) {
      Integer wfId = (Integer)itr.next();
      ws = getWorkflowState (wfId);
      v.add(ws);
    }
    return v;
  }

  public  List getAllWorkflows () throws XflowException {
    try{
      List v = new ArrayList();
      WorkflowState ws = null;

      List wflowIds = workflowP.getAllWorkflows();
      Iterator itr = wflowIds.iterator();
      while (itr.hasNext()) {
        Integer wfId = (Integer)itr.next();
        ws = getWorkflowState (wfId);
        v.add(ws);
      }
      return v;
    }catch(Exception e ){
      throw new XflowException( e );
    }
  }

  public  List getWorkflowsByName (String name) throws XflowException, SQLException {
    List v = new ArrayList();
    WorkflowState ws = null;
    List wflowIds = workflowP.getWorkflowsByName(name);
    Iterator itr = wflowIds.iterator();
    while (itr.hasNext()) {
      Integer wfId = (Integer)itr.next();
      ws = getWorkflowState (wfId);
      v.add(ws);
    }
    return v;
  }

  //
  // Process-oriented services
  //

  public  void completeWorkItem (String workflowName, int workflowVersion,  String processName, WorkItem witem)
      throws XflowException, SQLException, JaxenException, IOException, ParserConfigurationException, SAXException {

    // Begin Validation
    log.info ("In CompleteWorkItem.");
    log.info ("workflowName: " + workflowName);
    log.info ("processName: " + processName);

    log.info (" Validating Work Item: " + witem);


    if (witem == null || witem.getId() == null) {
      throw new XflowException ("Cannot complete work item. Null workitem or its ID");
    }

    Integer wid  = witem.getId();
    Integer wfId = witem.getWorkflowId();
    if (wfId == null) {
      throw new XflowException ("Cannot complete work item. Null workflow Id");
    }

    Integer key = wfId;

    if (! activeWorkflows.contains(key) ) {
      //throw new XflowException ("Cannot complete work item. Workflow instance is not active");
      return; // This is an OK situation - the wf may have ended in the case where there are parallel flows.
    }

    if (suspendedWorkflows.contains(key) ) {
      throw new XflowException ("Cannot complete work item. Workflow instance has been suspended");
    }

    int gid = -1;
    try {
      gid = directedGraphP.getGraphId (workflowName, workflowVersion).intValue();
    } catch (Exception e) {
      throw new XflowException (e);
    }

    if (!inboxP.isWorkItemValid(gid, processName, witem)) {
      throw new XflowException ("Cannot complete work item. Invalid work item state.");
    }


    log.info ("workflow ID: " + wfId);

    // End Validation

    log.info ("Work Item passed validation. Now attempting to complete work item");

    DirectedGraph dg = getGraphByGraphId (gid);
    Node rootNode = dg.getRootNode();
    Node thisNode = rootNode.getNode (processName);

    transitionFrom (gid, workflowName, workflowVersion, thisNode, witem);


  }

  public  List getWorkItems (String wfName, String procName) throws XflowException, SQLException {
    return Persistence.getWorkItemP().getWorkItems(wfName, procName);
  }

  public  WorkItem getNextWorkItem (String wfName, String procName) throws XflowException, SQLException {
    return Persistence.getWorkItemP().getNextWorkItem(wfName, procName);
  }

  public  WorkItem getWorkItem (Integer wid, String procName) throws XflowException, SQLException {
    return Persistence.getWorkItemP().getWorkItem(wid, procName);
  }

  //
  //  Auxiliary Methods
  //

  private  DirectedGraph getGraphByGraphId (int gid) throws XflowException {
    DirectedGraph dg = (DirectedGraph)graphsByGraphId.get(new Integer(gid));
    if (dg == null) {
      try {
        dg = DirectedGraph.loadByGraphId (gid);
        graphsByGraphId.put (new Integer(gid), dg);
        String nameVers = dg.getName() + dg.getVersion();
        graphsByNameAndVersion.put (nameVers, dg);
      } catch (Exception e) {
        throw new XflowException (e);
      }
    }
    return dg;
  }

  private  DirectedGraph getGraphByNameAndVersion (String name, int version) throws XflowException, SQLException {
    DirectedGraph dg = (DirectedGraph)graphsByNameAndVersion.get(name+version);
    if (dg == null) {
      log.info ("Loading workflow: " + name + " " + version);
      dg = directedGraphP.loadDirectedGraph(name, version);

      int gid = dg.getGraphId();
      graphsByGraphId.put (new Integer(gid), dg);
      String nameVers = dg.getName() + dg.getVersion();
      graphsByNameAndVersion.put (nameVers, dg);
    }
    return dg;
  }



  private  void processContainer (int gid, Node containerNode, WorkItem witem) throws XflowException, SQLException, JaxenException, IOException, ParserConfigurationException, SAXException {

    log.info ("in processContainer");

    Integer wfId = witem.getWorkflowId();
    String containee = containerNode.getContainee();
    int containeeVersion = containerNode.getContaineeVersion();

    log.info ("containee name: " + containee);
    log.info ("containee version: " + containeeVersion);

    DirectedGraph dg = getGraphByNameAndVersion(containee, containeeVersion);
    int containeeGid = dg.getGraphId();

    log.info ("Successfully loaded graph");

    int graphId = dg.getGraphId();
    Node startNode = dg.getRootNode();
    Node endNode = dg.getEndNode();

    log.info ("graphId: " + graphId);
    log.info ("startNode: " + endNode);
    log.info ("endNode: " + endNode);

    // If container does not have any destinations -- we spawn a new workflow thread
    if (containerNode.getDestinations().size() == 0) {
      log.info ("Starting containee workflow: " + containee + " version: " + containeeVersion);
      startContaineeWorkflow (containee, containeeVersion, witem, "System", wfId);
    } else {
      // Otherwise, execute containee workflow in the current thread
      // Push to process stack
      processStack.push (wfId, gid, containerNode, endNode);
      // Transition from start node
      transitionFromStartNode (containeeGid, containee, containeeVersion, startNode, witem);
    }
  }

  private  boolean evaluateRule (WorkItem witem, String rule) throws XflowException, JaxenException, IOException, ParserConfigurationException, SAXException {

    boolean result = true;
    if (rule != null && !rule.equals("") && !rule.equals("always")) {
      log.info ("Evaluating rule: " + rule);
      if (RuleEngine.evaluate(witem, rule) == false) {
        result = false;
      }
      log.info ("Rule: " + rule + " evaluated to: " + result);
    }
    return result;
  }

  private  void transitionFrom (int gid, String workflowName, int workflowVersion,
                                Node fromNode, WorkItem witem) throws XflowException, SQLException, JaxenException, IOException, ParserConfigurationException, SAXException {


    List destv = fromNode.getDestinations();
    String processName = fromNode.getName();
    Integer workflowId = witem.getWorkflowId();

    log.info ("Transitioning from: " + fromNode.getNodeId() + " " + fromNode.getName());
    log.info ("From node has: " + destv.size() + " destinations");

    // Place workitem in destination nodes inbox
    for (int i = 0; i < destv.size(); i++) {
      wf.model.Destination dest = (wf.model.Destination)destv.get(i);

      log.debug ("Processing destination " + i);

      // Evaluate rule
      if (!evaluateRule (witem, dest.rule)) {
        log.info ("This destination's rule evaluated to false. Not going there");
        continue;
      }

      Node node = dest.node;
      String nodeType = node.getNodeType();

      log.debug ("This destination node is: " + node.getNodeId() + " " + node.getName());
      log.debug ("This destination node type is: " + node.getNodeType());

      // End nodes don't have inboxes -- so just remove from previous inbox and mark workflow
      // as completed.
      if (nodeType.equals(Node.END)) {

        log.info ("Processing END node");
        inboxP.removeWorkItem(gid, processName, witem);

        // Unwind if necessary
        PopNode popNode = processStack.pop (workflowId.intValue(), node);

        // Mark workflow ended only if we didn't unwind.
        if (popNode == null) {
          int thisVersion = -1;
          if (workflowVersion == -1) {
            thisVersion = directedGraphP.getLatestVersionNumber (workflowName);
          }

          Integer key = workflowId;
          activeWorkflows.remove (key);
          workflowP.setCompleted (workflowId);

          try {
            // Publish workflow completed event
            eventsPublisher.publishWorkflowCompletedEvent (workflowName, thisVersion,  workflowId, "system");
          } catch (XflowException e) {
            log.warn ("Failed to publish event");
          }
        } else {
          log.info ("Transitioning to unwoundNode's destinations.");

          // Transition to popped node's destination(s)
          int cGid = popNode.gid;
          int cNodeId = popNode.nodeId;
          log.info ("cGid = " + cGid + " cNodeId = " + cNodeId);
          DirectedGraph dg = getGraphByGraphId (cGid);
          log.info ("Got graph");
          Node cNode = dg.getNode(cNodeId);
          log.info ("cNode = " + cNode);
          String cWorkflowName = dg.getName();
          int cVersion = dg.getVersion();
          log.info ("cWorkflowName = " + cWorkflowName + " Version = " + cVersion);
          transitionFrom (cGid, cWorkflowName, cVersion, cNode, witem);
        }
        continue;
      }

      String nextProcessName = node.getName();
      String nextProcessType = node.getNodeType();

      // Handle Or node
      if (nextProcessType.equals(Node.OR)) {

        log.info ("Processing OR node");
        // Has there been a prior transition to this OR node? If yes,
        // don't do anything.
        if (orTransitionHasOccurred(workflowId, node.getNodeId())) {
          continue;
        }

        // Record the Or transition
        recordOrTransition (workflowId, node.getNodeId());

        List orDest = node.getDestinations();
        Node orDestNode = ((wf.model.Destination) orDest.get(0)).node;
        nextProcessName = orDestNode.getName();
        nextProcessType = orDestNode.getNodeType();
        if (nextProcessType.equals(Node.PROCESS)) {
          log.info ("Transitioning to: " + nextProcessName);
          transitionTo (gid, workflowName, workflowId, workflowVersion, processName, nextProcessName, witem);
        }else{
          throw new XflowException( "Next node is not process!!");
        }
        // Handle And node
      } else if (nextProcessType.equals(Node.AND)) {
        log.info ("Processing AND node");
        int destNodeId = node.getNodeId();
        int fromNodeId = fromNode.getNodeId();
        waitingP.addProcess (workflowId, destNodeId, fromNodeId);
        inboxP.removeWorkItem (gid, processName, witem);

        List fromNodes = node.getFromNodes();
        if (waitingP.allProcessesArrived(fromNodes, workflowId, destNodeId)) {
          log.info ("Waiting on AND node completed.");
          waitingP.removeProcesses(workflowId);
          transitionFrom (gid, workflowName, workflowVersion, node, witem);
        }
      } else if (nextProcessType.equals(Node.PROCESS)) {
        log.info ("Transitioning to: " + nextProcessName);
        transitionTo (gid, workflowName, workflowId, workflowVersion, processName, nextProcessName, witem);
      } else if (nextProcessType.equals(Node.CONTAINER)) {
        // This is a container for another workflow
        log.info ("Processing CONTAINER node");
        processContainer(gid, node, witem);
      }
    }
  }

  private  void transitionFromStartNode (int gid, String workflowName, int workflowVersion,
                                         Node startNode, WorkItem witem)
      throws XflowException, JaxenException, IOException, ParserConfigurationException, SAXException {

    // Get the destinations from start node
    List destv = startNode.getDestinations();

    // Place workitem in destination nodes inbox
    for (int i = 0; i < destv.size(); i++) {
      wf.model.Destination dest = (wf.model.Destination)destv.get(i);

      // Evaluate rule
      if (!evaluateRule (witem, dest.rule)) {
        continue;
      }

      Node node = dest.node;
      String nodeType = node.getNodeType();

      // End nodes don't have inboxes -- so ignore
      if (nodeType.equals(Node.END)) {
        continue;
      }
      String procName = node.getName();
      log.info ("Adding workitem to inbox for proc: " + procName);

      // Add the work item to the process's inbox
      inboxP.addWorkItem (gid, workflowName, procName, witem);

      // Send notification to destination
      log.info ("Transition From Start Node");
      log.info ("Sending inbox notification:");
      log.info ("   workflowName: " + workflowName);
      log.info ("   procName: " + procName);
      log.info ("   witem: " + witem.getId());
      sendInboxNotification (workflowName, procName, witem);
    }
  }

  private  void transitionTo (int gid, String workflowName, Integer workflowId,
                              int workflowVersion, String processName,
                              String nextProcessName, WorkItem witem)
      throws XflowException {

    inboxP.removeWorkItem (gid, processName, witem);
    inboxP.addWorkItem (gid, workflowName, nextProcessName, witem);

    log.info ("TransitionTo");
    log.info ("Sending inbox notification:");
    log.info ("   workflowName: " + workflowName);
    log.info ("   procName: " + nextProcessName);
    log.info ("   witem: " + witem.getId());

    // Send notification to destination
    sendInboxNotification (workflowName, nextProcessName, witem);

    try {
      int thisVersion = -1;
      if (workflowVersion == -1) {
        thisVersion = directedGraphP.getLatestVersionNumber (workflowName);
      }
      eventsPublisher.publishNodeTransitionEvent (workflowName, thisVersion, workflowId, processName, nextProcessName,
          witem);
    } catch (Exception e) {
      log.warn ("Failed to publish event", e);
    }


  }

  private  void sendInboxNotification (String workflowName, String procName, WorkItem witem) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject (witem);
      s.flush();
      byte[] barr = out.toByteArray();

      List props = new ArrayList();
      MessageProperty mp = new MessageProperty();
      mp.name = "ProcessName";
      mp.value = workflowName + procName;
      props.add (mp);
      JMSPublisher.send (XflowConfig.XFLOW_TOPIC(), barr, props);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private  boolean orTransitionHasOccurred (Integer workflowId, int nodeId) throws SQLException {
    return workflowP.existsOrtab (workflowId, nodeId);
  }

  private  void recordOrTransition (Integer workflowId, int nodeId) throws SQLException {
    workflowP.insertOrtab (workflowId, nodeId);
  }
}

