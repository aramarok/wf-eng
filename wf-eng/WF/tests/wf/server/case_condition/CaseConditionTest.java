package wf.server.case_condition;

import java.io.IOException;
import wf.jms.model.ResUrmWI;
import wf.server.controller.AbstractServerTestCase;

public class CaseConditionTest extends AbstractServerTestCase {

    public static final String MODEL_NAME = "case_condition";

    static public junit.framework.Test suite() {
	junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
	newSuite.addTest(new CaseConditionTest("testClearDB"));
	newSuite.addTest(new CaseConditionTest("testDeployWF"));
	newSuite.addTest(new CaseConditionTest("testRunWF_25"));
	newSuite.addTest(new CaseConditionTest("testRunWF_50"));
	newSuite.addTest(new CaseConditionTest("testRunWF_10"));
	return newSuite;
    }

    public CaseConditionTest() {
    }

    public CaseConditionTest(final String s) {
	super(s);
    }

    private void assertProc2_4Unemployed() {
	assertTrue("Proc2 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc2"));
	assertTrue("Proc3 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	assertTrue("Proc4 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc4"));
    }

    private void finishViaProcess(final String procName) {
	ResUrmWI response1 = this.getNextWorkItemForProcess(MODEL_NAME,
		procName);
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, procName, response1.workItem);
	assertTrue("Workflow is not finished", !this.isWorkFlowActive(wfID));
    }

    public void testClearDB() {
	assertTrue("There is already deployed model with the same name", !this
		.doesModelExist(MODEL_NAME));
    }

    public void testDeployWF() throws IOException {
	this.deployWF(MODEL_NAME,
		"tests/wf/server/case_condition/case_condition.xf.xml");
    }

    public void testRunWF_10() throws IOException {
	this.startWF(MODEL_NAME, new CaseConditionPayload(10));
	this.assertProc2_4Unemployed();
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc2 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc2"));
	assertTrue("Proc3 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	assertTrue("Proc4 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc4"));
	this.finishViaProcess("proc4");
    }

    public void testRunWF_25() throws IOException {
	this.startWF(MODEL_NAME, new CaseConditionPayload(25));
	this.assertProc2_4Unemployed();
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc2 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc2"));
	assertTrue("Proc3 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	assertTrue("Proc4 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc4"));
	this.finishViaProcess("proc2");
    }

    public void testRunWF_50() throws IOException {
	this.startWF(MODEL_NAME, new CaseConditionPayload(50));
	this.assertProc2_4Unemployed();
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc2 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc2"));
	assertTrue("Proc3 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	assertTrue("Proc4 has    work", !this.processHasWorkItems(MODEL_NAME,
		"proc4"));
	this.finishViaProcess("proc3");
    };

}
