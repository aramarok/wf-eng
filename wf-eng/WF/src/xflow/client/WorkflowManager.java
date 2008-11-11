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
package xflow.client;

import xflow.common.Node;
import xflow.common.WorkItem;
import xflow.common.WorkflowState;
import xflow.common.XflowException;
import xflow.messaging.SynchQueueMessaging;
import xflow.protocol.*;
import xflow.security.User;
import xflow.util.Util;

import java.util.List;

/**
 *  WorkflowManager contains all workflow administrative functions.
 *  Clients needing to perform functions such as starting/aborting workflows,
 *  deploying workflows, etc should invoke methods from this class.
 */
public class WorkflowManager {

  public static final String XFLOW  = "XFLOW";
  public static final String BPEL   = "BPEL";

  /**
   *  Deploys a workflow model from an XML document
   *
   *  @param    xml        the XML document
   *  @param    type       the XML document type
   *                       valid type currently supported is: XFLOW.
   *                       BPEL in the near future
   *  @param    user       the user
   *  @exception XflowException
   */
  public static DeployModelResponse deployModel (String xml, String type, User user) throws XflowException {
    DeployModelRequest  req = new DeployModelRequest();
    req.user = user;
    req.xml = xml;
    req.type = type;
    DeployModelResponse resp = (DeployModelResponse)sendRequest (req);
    return resp;
  }

  /**
   *  Starts a workflow.
   *
   *  @param workflowName  the workflow name
   *  @param workItem      the work item
   *  @param user          the initiator of the workflow
   *  @return the workflow ID of the newly created workflow instance
   *  @exception XflowException
   */
  public static Integer startWorkflow (String workflowName,
                                       WorkItem workItem,
                                       User user) throws XflowException {

    StartWorkflowRequest req = new StartWorkflowRequest();
    req.workflowName = workflowName;
    req.workItem = workItem;
    req.user = user;

    StartWorkflowResponse resp = (StartWorkflowResponse)sendRequest (req);
    return resp.workflowId;
  }

  /**
   *  Starts a workflow with a specified version
   *
   *  @param workflowName    the workflow name
   *  @param workflowVersion the workflow version
   *  @param workItem        the work item
   *  @param user            the initiator of the workflow
   *  @return the workflow ID of the newly created workflow instance
   *  @exception XflowException
   */
  public static Integer startWorkflow (String workflowName,
                                       int workflowVersion,
                                       WorkItem workItem,
                                       User user) throws XflowException {

    StartWorkflowRequest req = new StartWorkflowRequest();
    req.workflowName = workflowName;
    req.version = workflowVersion;
    req.workItem = workItem;
    req.user = user;

    StartWorkflowResponse resp = (StartWorkflowResponse)sendRequest (req);
    return resp.workflowId;
  }

  /**
   *  Aborts an active workflow instance
   *
   *  @param workflowId      the workflow instance ID
   *  @param user            the user requesting the abort
   *  @exception XflowException
   */
  public static AbortWorkflowResponse abortWorkflow (Integer workflowId,
                                    User user) throws XflowException {

    AbortWorkflowRequest req = new AbortWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    AbortWorkflowResponse resp = (AbortWorkflowResponse)sendRequest (req);
    return resp;
  }

  /**
   *  Suspends an active workflow instance
   *
   *  @param workflowId      the workflow instance ID
   *  @param user            the user requesting the suspend
   *  @exception XflowException
   */
  public static SuspendWorkflowResponse suspendWorkflow (Integer workflowId,
                                      User user) throws XflowException {

    SuspendWorkflowRequest req = new SuspendWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    SuspendWorkflowResponse resp = (SuspendWorkflowResponse)sendRequest (req);
    return resp;
  }

  /**
   *  Resumes a suspended workflow instance
   *
   *  @param workflowId      the workflow instance ID
   *  @param user            the user requesting the resume
   *  @exception XflowException
   */
  public static ResumeWorkflowResponse resumeWorkflow (Integer workflowId,
                                     User user) throws XflowException {

    ResumeWorkflowRequest req = new ResumeWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    ResumeWorkflowResponse resp = (ResumeWorkflowResponse)sendRequest (req);
    return resp;
  }

  /**
   *  Gets the workflow state
   *
   *  @param workflowId      the workflow instance ID
   *  @param user            the user
   *  @return the workflow state
   *  @exception XflowException
   */
  public static WorkflowState getWorkflowState (Integer workflowId,
                                                User user) throws XflowException {

    GetWorkflowStateRequest req = new GetWorkflowStateRequest();
    req.workflowId = workflowId;
    req.user = user;

    GetWorkflowStateResponse resp = (GetWorkflowStateResponse)sendRequest (req);
    return resp.workflowState;
  }

  /**
   *  Sets a variable for a specified workflow instance
   *
   *  @param workflowId      the workflow instance ID
   *  @param variableName    the variable name
   *  @param variableValue   the variable value - must be serializable
   *  @param user            the user
   *  @exception XflowException
   */
  public static SetVariableResponse setVariable (Integer workflowId, String variableName, Object variableValue,
                                  User user)
      throws XflowException {
    SetVariableRequest req = new SetVariableRequest();
    req.workflowId = workflowId;
    req.variableName  = variableName;
    req.variableValue = variableValue;
    req.user = user;

    SetVariableResponse resp = (SetVariableResponse)sendRequest (req);
    return resp;
  }

  /**
   *  Gets a variable for a specified workflow instance
   *
   *  @param workflowId      the workflow instance ID
   *  @param variableName    the variable name
   *  @param user            the user
   *  @return the variable value
   *  @exception XflowException
   */
  public static Object getVariable (Integer workflowId, String variableName,
                                    User user) throws XflowException {

    GetVariableRequest req = new GetVariableRequest();
    req.workflowId = workflowId;
    req.variableName  = variableName;
    req.user = user;
    GetVariableResponse resp = (GetVariableResponse)sendRequest (req);
    return resp.variableValue;
  }

  /**
   *  Gets all active workflow instances
   *
   *  @param user            the user
   *  @return the list of WorkflowState objects of currently active workflow instances
   *  @exception XflowException
   */
  public static List getActiveWorkflows (User user) throws XflowException {

    GetActiveWorkflowsRequest  req = new GetActiveWorkflowsRequest();
    req.user = user;
    GetActiveWorkflowsResponse resp = (GetActiveWorkflowsResponse)sendRequest (req);
    return resp.activeWorkflows;
  }


  /**
   *  Gets all workflow instances
   *
   *  @param user            the user
   *  @return the list of WorkflowState objects of all workflow instances
   *  @exception XflowException
   */
  public static List getAllWorkflows (User user) throws XflowException {

    GetAllWorkflowsRequest  req = new GetAllWorkflowsRequest();
    req.user = user;
    GetAllWorkflowsResponse resp = (GetAllWorkflowsResponse)sendRequest (req);
    return resp.workflows;
  }

  /**
   *  Gets all workflow instances
   *
   *  @param name            the workflow model name
   *  @param user            the user
   *  @return the list of WorkflowState objects of all workflow instances
   *  @exception XflowException
   */
  public static List getAllWorkflowsByName (String name, User user) throws XflowException {

    GetWorkflowsByNameRequest  req = new GetWorkflowsByNameRequest();
    req.user = user;
    req.name = name;
    GetWorkflowsByNameResponse resp = (GetWorkflowsByNameResponse)sendRequest (req);
    return resp.workflows;
  }

  /**
   *  Gets all process nodes participating in  a workflow instance
   *
   *  @param    workflowId      the workflow instance ID
   *  @param    user            the user
   *  @return the list of Node objects
   *  @exception XflowException
   */
  public static List getProcessNodes (Integer workflowId, User user) throws XflowException {
    GetProcessNodesRequest req = new GetProcessNodesRequest();
    req.user = user;
    req.workflowId = workflowId;
    GetProcessNodesResponse resp = (GetProcessNodesResponse)sendRequest(req);
    return resp.nodes;
  }

  /**
   *  Gets all process nodes participating in  a workflow instance
   *
   *  @param    workflowName     the workflow model name
   *  @param    workflowVersion  the workflow version (-1 means get the latest)
   *  @param    nodeName         the node name
   *  @param    user            the user
   *  @return the Node object
   *  @exception XflowException
   */
  public static Node getNodeByName (String workflowName, int workflowVersion,
                                    String nodeName, User user) throws XflowException {
    GetNodeByNameRequest req = new GetNodeByNameRequest();
    req.user = user;
    req.workflowName = workflowName;
    req.version = workflowVersion;
    req.nodeName = nodeName;
    GetNodeByNameResponse resp = (GetNodeByNameResponse)sendRequest(req);
    return resp.node;
  }

  /**
   *  Gets all deployed workflow models
   *
   *  @return the list of WorkflowModel objects
   *  @exception XflowException
   */
  public static List getWorkflowModels (User user) throws XflowException {
    GetModelsRequest req = new GetModelsRequest();
    req.user = user;
    GetModelsResponse resp = (GetModelsResponse)sendRequest(req);
    return resp.models;
  }

  private static Response sendRequest (Request req) throws XflowException {

    req.replyName = Util.generateUniqueStringId();
    try {
      Response resp = SynchQueueMessaging.sendRequest (req);
      if (resp.responseCode != Response.SUCCESS) {
        System.out.println ("FAILURE response from server.");
        throw new XflowException(resp.message);
      }
      return resp;
    } catch (Exception t) {
      throw new XflowException (t );
    }
  }

}
