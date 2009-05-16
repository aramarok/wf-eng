package wf.server.case1;

import java.io.IOException;
import java.util.List;
import wf.TestConfig;
import wf.jms.model.ReqSetareVariabila;
import wf.jms.model.ReqVariabila;
import wf.jms.model.ReqWFActive;
import wf.jms.model.ResSetareVariabila;
import wf.jms.model.ResUrmWI;
import wf.jms.model.ResVariabila;
import wf.jms.model.ResWFActive;
import wf.model.ItemModel;
import wf.model.StareWF;
import wf.server.controller.AbstractServerTestCase;

public class Case1_Test extends AbstractServerTestCase {

    public static final String MODEL_NAME = "case1";

    static public junit.framework.Test suite() {
	junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
	newSuite.addTest(new Case1_Test("testClearDB"));
	newSuite.addTest(new Case1_Test("testDeployWF"));
	newSuite.addTest(new Case1_Test("testStartWF"));
	newSuite.addTest(new Case1_Test("testGetActiveWorkflows"));
	newSuite.addTest(new Case1_Test("testSetWorkflowVariable"));
	newSuite.addTest(new Case1_Test("testRunWF"));
	newSuite.addTest(new Case1_Test("testFinalNode"));
	return newSuite;
    }

    public Case1_Test() {
	super();
    }

    public Case1_Test(final String s) {
	super(s);
    }

    public void testClearDB() {
	assertTrue("There is already deployed model with the same name", !this
		.doesModelExist(MODEL_NAME));
    }

    public void testDeployWF() throws IOException {
	this.deployWF(MODEL_NAME, "tests/wf/server/case1/case1.xf.xml");
    }

    public void testFinalNode() {
	assertTrue("Proc2 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc2"));
	ResUrmWI responseProc3 = this.getNextWorkItemForProcess(MODEL_NAME,
		"proc2");
	ItemModel workItem = responseProc3.workItem;
	this.completeWorkItem(MODEL_NAME, "proc2", workItem);
	assertTrue("Workflow is not finished", !this.isWorkFlowActive(workItem
		.getWorkflowId()));
    }

    @SuppressWarnings("unchecked")
    public void testGetActiveWorkflows() {
	ReqWFActive req = new ReqWFActive();
	req.utilizator = TestConfig.getUser();
	ResWFActive res = (ResWFActive) this.handleRequest(req);
	List activeWFs = res.activeWorkflows;
	assertTrue("There should be at least one active workflow", activeWFs
		.size() > 0);
    }

    public void testRunWF() {
	assertTrue("Proc1 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc1"));
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished but should be active", this
		.isWorkFlowActive(wfID));
	assertTrue("Proc2 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc2"));
    }

    public void testSetWorkflowVariable() {
	ReqWFActive req = new ReqWFActive();
	req.utilizator = TestConfig.getUser();
	ResWFActive getActiveWorkflowsResponse = (ResWFActive) this
		.handleRequest(req);
	StareWF wfState = (StareWF) getActiveWorkflowsResponse.activeWorkflows
		.get(0);
	Integer wfID = wfState.getId();
	String varName = "testVarName";
	Integer varVal = new Integer(786);
	ReqSetareVariabila setVariableRequest = new ReqSetareVariabila(wfID,
		varName, varVal);
	ResSetareVariabila res = (ResSetareVariabila) this
		.handleRequest(setVariableRequest);
	this.assertResponceOK(res);
	ReqVariabila getVariableRequest = new ReqVariabila();
	getVariableRequest.variableName = varName;
	getVariableRequest.workflowId = wfID;
	ResVariabila r = (ResVariabila) this.handleRequest(getVariableRequest);
	this.assertResponceOK(r);
	assertEquals("variable value is unexpected", varVal, r.variableValue);
    }

    public void testStartWF() throws IOException {
	TestCase1Payload payload = new TestCase1Payload();
	Integer wfID = this.startWF(MODEL_NAME, payload);
	assertTrue("Workflow should be active", this.isWorkFlowActive(wfID));
    };

}
