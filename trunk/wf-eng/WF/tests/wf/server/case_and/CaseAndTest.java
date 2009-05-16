package wf.server.case_and;

import java.io.IOException;
import wf.jms.model.ResUrmWI;
import wf.model.ItemModel;
import wf.server.controller.AbstractServerTestCase;

public class CaseAndTest extends AbstractServerTestCase {

    public static final String MODEL_NAME = "case_and";

    static public junit.framework.Test suite() {
	junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
	// newSuite.addTest(new CaseAndTest("testClearDB"));
	// newSuite.addTest(new CaseAndTest("testDeployWF"));
	newSuite.addTest(new CaseAndTest("testStartWF"));
	// newSuite.addTest(new CaseAndTest("testRunWF"));
	// newSuite.addTest(new CaseAndTest("testFinalNode"));
	return newSuite;
    }

    public CaseAndTest() {
	super();
    }

    public CaseAndTest(final String s) {
	super(s);
    }

    public void testClearDB() {
	assertTrue("There is already deployed model with the same name", !this
		.doesModelExist(MODEL_NAME));
    }

    public void testDeployWF() throws IOException {
	this.deployWF(MODEL_NAME, "tests/wf/server/case_and/case_and.xf.xml");
    }

    public void testFinalNode() {
	assertTrue("Proc3 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	ResUrmWI responseProc3 = this.getNextWorkItemForProcess(MODEL_NAME,
		"proc3");
	ItemModel workItem = responseProc3.workItem;
	this.completeWorkItem(MODEL_NAME, "proc3", workItem);
	assertTrue("Workflow is not finished", !this.isWorkFlowActive(workItem
		.getWorkflowId()));
    }

    public void testRunWF() {
	assertTrue("Proc3 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc3 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));

	ResUrmWI response2 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc2");
	wfID = response2.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc2", response2.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc3 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc3"));
    }

    public void testStartWF() throws IOException {
	this.startWF(MODEL_NAME);
    };

}
