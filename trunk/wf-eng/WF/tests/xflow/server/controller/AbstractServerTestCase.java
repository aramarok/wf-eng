package xflow.server.controller;

import junit.framework.TestCase;
import xflow.common.WorkflowModel;
import xflow.common.WorkItem;
import xflow.common.XflowException;
import xflow.messaging.JMSPublisher;
import xflow.messaging.SynchQueueMessaging;
import xflow.protocol.*;
import xflow.TestConfig;
import xflow.TestUtilities;
import xflow.security.User;
import xflow.util.Util;

import java.util.Iterator;
import java.util.Date;
import java.io.IOException;
import java.sql.SQLException;

/**
 * User: kosta
 * Date: Jul 11, 2004
 * Time: 7:07:57 PM
 */
public class AbstractServerTestCase extends TestCase {

  static Object guard = new Object();
  private static WorkflowEngine _workflowEngine;


  public AbstractServerTestCase() {

  }

  public AbstractServerTestCase(String s) {
    super(s);
  }

  public void startWF(String modelName) throws IOException {
    startWF(modelName, null);
  }

  public Integer startWF(String modelName, Object payload) throws IOException {
    WorkItem wi = new WorkItem();
    if (payload == null) {
      wi.setPayloadTXT(modelName + "- payload - " + new Date());
    } else {
      wi.setPayloadAsJavaObject(payload);
    }
    StartWorkflowRequest startWFReq = new StartWorkflowRequest();
    startWFReq.user = TestConfig.getUser();
    startWFReq.workflowName = modelName;
    startWFReq.workItem = wi;
    StartWorkflowResponse startWorkflowResponse = (StartWorkflowResponse) handleRequest(startWFReq);
    assertResponceOK(startWorkflowResponse);
    return startWorkflowResponse.workflowId;
  }

  public GetNextWorkItemResponse getNextWorkItemForProcess(String modelName, String processName) {
    GetNextWorkItemRequest nextItemRequest1 = new GetNextWorkItemRequest(modelName, processName);
    GetNextWorkItemResponse response1 = (GetNextWorkItemResponse) handleRequest(nextItemRequest1);
    assertResponceOK(response1);
    return response1;
  }

  public boolean isWorkFlowActive(Integer wfID) {
    GetWorkflowStateRequest getWorkflowStateRequest = new GetWorkflowStateRequest();
    getWorkflowStateRequest.workflowId = wfID;
    GetWorkflowStateResponse wfStateResp = (GetWorkflowStateResponse) handleRequest(getWorkflowStateRequest);
    assertResponceOK(wfStateResp);
    return wfStateResp.workflowState.isActive();
  }


  public void deployWF(String modelName, String resource) throws IOException {
    DeployModelRequest deployModelRequest = new DeployModelRequest();
    deployModelRequest.user = TestConfig.getUser();
    deployModelRequest.type = WorkflowEngine.FLOW_TYPE_XFLOW;
    deployModelRequest.xml = TestUtilities.readFileContent(resource);
    DeployModelResponse deployModelResponse = (DeployModelResponse) handleRequest(deployModelRequest);
    assertResponceOK(deployModelResponse);
    boolean hasModel = doesModelExist(modelName);
    assertTrue("No deployed WF model with name" + modelName, hasModel);
  }


  public void assertResponceOK(Response r) {
    assertEquals("SUCCESS code", Response.SUCCESS, r.responseCode);
  }

  public boolean doesModelExist(String modelName) {
    boolean hasModel = false;
    GetModelsRequest getModelsRequest = new GetModelsRequest();
    GetModelsResponse response = (GetModelsResponse) handleRequest(getModelsRequest);
    for (Iterator j = response.models.iterator(); j.hasNext();) {
      WorkflowModel model = (WorkflowModel) j.next();
      if (modelName.equals(model.getName())) {
        hasModel = true;
        break;
      }
    }
    return hasModel;
  }

  private static WorkflowEngine getWorkflowEngine() {
    synchronized (guard) {
      if (_workflowEngine == null) {
        _workflowEngine = new WorkflowEngine();
        try {
          WorkflowProcessor.getInstance().setEventsEnabled(false);
        } catch (SQLException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
        JMSPublisher.setSendingEnabled(false);
      }
      return _workflowEngine;
    }

  }

  public boolean processHasWorkItems(String modelName, String processName) {
    GetNextWorkItemResponse response = getNextWorkItemForProcess(modelName, processName);
    return response.workItem != null;
  }

  protected CompleteWorkItemResponse completeWorkItem(String modelName, String procName, WorkItem workItem) {

    CompleteWorkItemRequest completeWorkItemRequest = new CompleteWorkItemRequest();
    completeWorkItemRequest.workflowName = modelName;
    completeWorkItemRequest.processName = procName;
    completeWorkItemRequest.workItem = workItem;
    CompleteWorkItemResponse completeWorkItemResponse = (CompleteWorkItemResponse) handleRequest(completeWorkItemRequest);
    assertResponceOK(completeWorkItemResponse);
    return completeWorkItemResponse;
  }


  protected Response handleRequest(Request req) {
    if ("yes".equalsIgnoreCase(System.getProperty("remote"))) {
      try {
        req.replyName = Util.generateUniqueStringId();
        req.user = new User("kosta", "kosta");
        Response resp = SynchQueueMessaging.sendRequest(req);
        if (resp.responseCode != Response.SUCCESS) {
          System.out.println("FAILURE response from server.");
          throw new XflowException(resp.message);
        }
        return resp;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      return getWorkflowEngine().handle(req);
    }

  }
}
