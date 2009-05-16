package wf.server.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import junit.framework.TestCase;
import wf.TestConfig;
import wf.TestUtilities;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.jms.PublisherJMS;
import wf.jms.Mesagerie;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqCompleteWI;
import wf.jms.model.ReqDeployModel;
import wf.jms.model.ReqModeleDisponibile;
import wf.jms.model.ReqStareWF;
import wf.jms.model.ReqStartWF;
import wf.jms.model.ReqUrmWI;
import wf.jms.model.ResCompleteWI;
import wf.jms.model.ResDeployModel;
import wf.jms.model.ResModeleDisponibile;
import wf.jms.model.ResStareWF;
import wf.jms.model.ResStartWF;
import wf.jms.model.ResUrmWI;
import wf.model.ItemModel;
import wf.model.ModelWF;
import wf.util.Util;

public class AbstractServerTestCase extends TestCase {

    static Object guard = new Object();
    private static WorkflowEngine _workflowEngine;

    public AbstractServerTestCase() {

    }

    public AbstractServerTestCase(final String s) {
	super(s);
    }

    public void startWF(final String modelName) throws IOException {
	this.startWF(modelName, null);
    }

    public Integer startWF(final String modelName, final Object payload)
	    throws IOException {
	ItemModel wi = new ItemModel();
	if (payload == null) {
	    wi.setPayloadTXT(modelName + "- payload - " + new Date());
	} else {
	    wi.setPayloadAsJavaObject(payload);
	}
	ReqStartWF startWFReq = new ReqStartWF();
	startWFReq.utilizator = TestConfig.getUser();
	startWFReq.workflowName = modelName;
	startWFReq.workItem = wi;
	ResStartWF startWorkflowResponse = (ResStartWF) this
		.handleRequest(startWFReq);
	this.assertResponceOK(startWorkflowResponse);
	return startWorkflowResponse.workflowId;
    }

    public ResUrmWI getNextWorkItemForProcess(final String modelName,
	    final String processName) {
	ReqUrmWI nextItemRequest1 = new ReqUrmWI(modelName, processName);
	ResUrmWI response1 = (ResUrmWI) this.handleRequest(nextItemRequest1);
	this.assertResponceOK(response1);
	return response1;
    }

    public boolean isWorkFlowActive(final Integer wfID) {
	ReqStareWF getWorkflowStateRequest = new ReqStareWF();
	getWorkflowStateRequest.workflowId = wfID;
	ResStareWF wfStateResp = (ResStareWF) this
		.handleRequest(getWorkflowStateRequest);
	this.assertResponceOK(wfStateResp);
	return wfStateResp.workflowState.isActive();
    }

    public void deployWF(final String modelName, final String resource)
	    throws IOException {
	ReqDeployModel deployModelRequest = new ReqDeployModel();
	deployModelRequest.utilizator = TestConfig.getUser();
	deployModelRequest.type = WorkflowEngine.FLOW_TYPE_WF;
	deployModelRequest.xml = TestUtilities.readFileContent(resource);
	ResDeployModel deployModelResponse = (ResDeployModel) this
		.handleRequest(deployModelRequest);
	this.assertResponceOK(deployModelResponse);
	boolean hasModel = this.doesModelExist(modelName);
	assertTrue("No deployed WF model with name" + modelName, hasModel);
    }

    public void assertResponceOK(final Raspuns r) {
	assertEquals("SUCCES code", Raspuns.SUCCES, r.codRaspuns);
    }

    @SuppressWarnings("unchecked")
    public boolean doesModelExist(final String modelName) {
	boolean hasModel = false;
	ReqModeleDisponibile getModelsRequest = new ReqModeleDisponibile();
	ResModeleDisponibile response = (ResModeleDisponibile) this
		.handleRequest(getModelsRequest);
	for (Iterator j = response.models.iterator(); j.hasNext();) {
	    ModelWF model = (ModelWF) j.next();
	    if (modelName.equals(model.getName())) {
		hasModel = true;
		break;
	    }
	}
	return hasModel;
    }

    @SuppressWarnings("unused")
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
		PublisherJMS.setSendingEnabled(false);
	    }
	    return _workflowEngine;
	}

    }

    public boolean processHasWorkItems(final String modelName,
	    final String processName) {
	ResUrmWI response = this.getNextWorkItemForProcess(modelName,
		processName);
	return response.workItem != null;
    }

    protected ResCompleteWI completeWorkItem(final String modelName,
	    final String procName, final ItemModel workItem) {

	ReqCompleteWI completeWorkItemRequest = new ReqCompleteWI();
	completeWorkItemRequest.workflowName = modelName;
	completeWorkItemRequest.processName = procName;
	completeWorkItemRequest.workItem = workItem;
	ResCompleteWI completeWorkItemResponse = (ResCompleteWI) this
		.handleRequest(completeWorkItemRequest);
	this.assertResponceOK(completeWorkItemResponse);
	return completeWorkItemResponse;
    }

    protected Raspuns handleRequest(final Cerere req) {
	// if ("yes".equalsIgnoreCase(System.getProperty("remote"))) {
	try {
	    req.numeRaspuns = Util.generateUniqueStringId();
	    req.utilizator = new Utilizator("kosta", "kosta");
	    Raspuns resp = Mesagerie.sendRequest(req);
	    if (resp.codRaspuns != Raspuns.SUCCES) {
		System.out.println("EROARE response from server.");
		throw new ExceptieWF(resp.mesaj);
	    }
	    return resp;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	// } else {
	// return getWorkflowEngine().handle(req);
	// }

    }
}
