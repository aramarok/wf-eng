package wf.server.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import junit.framework.TestCase;
import wf.TestConfig;
import wf.TestUtilities;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.jms.JMSPublisher;
import wf.jms.SynchQueueMessaging;
import wf.jms.model.CompleteWorkItemRequest;
import wf.jms.model.CompleteWorkItemResponse;
import wf.jms.model.DeployModelRequest;
import wf.jms.model.DeployModelResponse;
import wf.jms.model.GetModelsRequest;
import wf.jms.model.GetModelsResponse;
import wf.jms.model.GetNextWorkItemRequest;
import wf.jms.model.GetNextWorkItemResponse;
import wf.jms.model.GetWorkflowStateRequest;
import wf.jms.model.GetWorkflowStateResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.StartWorkflowRequest;
import wf.jms.model.StartWorkflowResponse;
import wf.model.WorkItem;
import wf.model.WorkflowModel;
import wf.util.Util;

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

	public GetNextWorkItemResponse getNextWorkItemForProcess(String modelName,
			String processName) {
		GetNextWorkItemRequest nextItemRequest1 = new GetNextWorkItemRequest(
				modelName, processName);
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
		deployModelRequest.type = WorkflowEngine.FLOW_TYPE_WF;
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
		GetNextWorkItemResponse response = getNextWorkItemForProcess(modelName,
				processName);
		return response.workItem != null;
	}

	protected CompleteWorkItemResponse completeWorkItem(String modelName,
			String procName, WorkItem workItem) {

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
					throw new WorkFlowException(resp.message);
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
