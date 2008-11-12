
package wf.client;

import java.util.List;

import wf.client.auth.User;
import wf.exceptions.XflowException;
import wf.jms.SynchQueueMessaging;
import wf.jms.model.AbortWorkflowRequest;
import wf.jms.model.AbortWorkflowResponse;
import wf.jms.model.DeployModelRequest;
import wf.jms.model.DeployModelResponse;
import wf.jms.model.GetActiveWorkflowsRequest;
import wf.jms.model.GetActiveWorkflowsResponse;
import wf.jms.model.GetAllWorkflowsRequest;
import wf.jms.model.GetAllWorkflowsResponse;
import wf.jms.model.GetModelsRequest;
import wf.jms.model.GetModelsResponse;
import wf.jms.model.GetNodeByNameRequest;
import wf.jms.model.GetNodeByNameResponse;
import wf.jms.model.GetProcessNodesRequest;
import wf.jms.model.GetProcessNodesResponse;
import wf.jms.model.GetVariableRequest;
import wf.jms.model.GetVariableResponse;
import wf.jms.model.GetWorkflowStateRequest;
import wf.jms.model.GetWorkflowStateResponse;
import wf.jms.model.GetWorkflowsByNameRequest;
import wf.jms.model.GetWorkflowsByNameResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.ResumeWorkflowRequest;
import wf.jms.model.ResumeWorkflowResponse;
import wf.jms.model.SetVariableRequest;
import wf.jms.model.SetVariableResponse;
import wf.jms.model.StartWorkflowRequest;
import wf.jms.model.StartWorkflowResponse;
import wf.jms.model.SuspendWorkflowRequest;
import wf.jms.model.SuspendWorkflowResponse;
import wf.model.Node;
import wf.model.WorkItem;
import wf.model.WorkflowState;
import wf.util.Util;


public class WorkflowManager {

  public static final String XFLOW  = "XFLOW";
  public static final String BPEL   = "BPEL";

  
  public static DeployModelResponse deployModel (String xml, String type, User user) throws XflowException {
    DeployModelRequest  req = new DeployModelRequest();
    req.user = user;
    req.xml = xml;
    req.type = type;
    DeployModelResponse resp = (DeployModelResponse)sendRequest (req);
    return resp;
  }

  
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

  
  public static AbortWorkflowResponse abortWorkflow (Integer workflowId,
                                    User user) throws XflowException {

    AbortWorkflowRequest req = new AbortWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    AbortWorkflowResponse resp = (AbortWorkflowResponse)sendRequest (req);
    return resp;
  }

  
  public static SuspendWorkflowResponse suspendWorkflow (Integer workflowId,
                                      User user) throws XflowException {

    SuspendWorkflowRequest req = new SuspendWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    SuspendWorkflowResponse resp = (SuspendWorkflowResponse)sendRequest (req);
    return resp;
  }

  
  public static ResumeWorkflowResponse resumeWorkflow (Integer workflowId,
                                     User user) throws XflowException {

    ResumeWorkflowRequest req = new ResumeWorkflowRequest();
    req.workflowId = workflowId;
    req.user = user;

    ResumeWorkflowResponse resp = (ResumeWorkflowResponse)sendRequest (req);
    return resp;
  }

  
  public static WorkflowState getWorkflowState (Integer workflowId,
                                                User user) throws XflowException {

    GetWorkflowStateRequest req = new GetWorkflowStateRequest();
    req.workflowId = workflowId;
    req.user = user;

    GetWorkflowStateResponse resp = (GetWorkflowStateResponse)sendRequest (req);
    return resp.workflowState;
  }

  
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

  
  public static Object getVariable (Integer workflowId, String variableName,
                                    User user) throws XflowException {

    GetVariableRequest req = new GetVariableRequest();
    req.workflowId = workflowId;
    req.variableName  = variableName;
    req.user = user;
    GetVariableResponse resp = (GetVariableResponse)sendRequest (req);
    return resp.variableValue;
  }

  
  public static List getActiveWorkflows (User user) throws XflowException {

    GetActiveWorkflowsRequest  req = new GetActiveWorkflowsRequest();
    req.user = user;
    GetActiveWorkflowsResponse resp = (GetActiveWorkflowsResponse)sendRequest (req);
    return resp.activeWorkflows;
  }


  
  public static List getAllWorkflows (User user) throws XflowException {

    GetAllWorkflowsRequest  req = new GetAllWorkflowsRequest();
    req.user = user;
    GetAllWorkflowsResponse resp = (GetAllWorkflowsResponse)sendRequest (req);
    return resp.workflows;
  }

  
  public static List getAllWorkflowsByName (String name, User user) throws XflowException {

    GetWorkflowsByNameRequest  req = new GetWorkflowsByNameRequest();
    req.user = user;
    req.name = name;
    GetWorkflowsByNameResponse resp = (GetWorkflowsByNameResponse)sendRequest (req);
    return resp.workflows;
  }

  
  public static List getProcessNodes (Integer workflowId, User user) throws XflowException {
    GetProcessNodesRequest req = new GetProcessNodesRequest();
    req.user = user;
    req.workflowId = workflowId;
    GetProcessNodesResponse resp = (GetProcessNodesResponse)sendRequest(req);
    return resp.nodes;
  }

  
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
