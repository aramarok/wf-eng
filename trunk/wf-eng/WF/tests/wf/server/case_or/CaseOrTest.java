package wf.server.case_or;

import java.io.IOException;
import wf.jms.model.ResUrmWI;
import wf.model.ItemModel;
import wf.server.controller.AbstractServerTestCase;

public class CaseOrTest extends AbstractServerTestCase {

    public static final String MODEL_NAME = "case_or";

    public CaseOrTest() {
    }

    public CaseOrTest(final String s) {
	super(s);
    }

    public void testClearDB() {
	assertTrue("There is already deployed model with the same name", !this
		.doesModelExist(MODEL_NAME));
    }

    public void testDeployWF() throws IOException {
	this.deployWF(MODEL_NAME, "tests/wf/server/case_or/case_or.xf.xml");
    }

    public void testStartWF() throws IOException {
	this.startWF(MODEL_NAME);
    }

    public void testRunWF() {
	assertTrue("Proc3 has work", !this.processHasWorkItems(MODEL_NAME,
		"proc3"));
	ResUrmWI response1 = this
		.getNextWorkItemForProcess(MODEL_NAME, "proc1");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "proc1", response1.workItem);
	assertTrue("Workflow is finished", this.isWorkFlowActive(wfID));
	assertTrue("Proc3 has no work", this.processHasWorkItems(MODEL_NAME,
		"proc3"));
    }

    public void testFinalNode() {
	ResUrmWI responseProc3 = this.getNextWorkItemForProcess(MODEL_NAME,
		"proc3");
	ItemModel workItem = responseProc3.workItem;
	this.completeWorkItem(MODEL_NAME, "proc3", workItem);
	assertTrue("Workflow is not finished", !this.isWorkFlowActive(workItem
		.getWorkflowId()));
    }

    static public junit.framework.Test suite() {
	junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
	// newSuite.addTest(new CaseOrTest("testClearDB"));
	// newSuite.addTest(new CaseOrTest("testDeployWF"));
	// newSuite.addTest(new CaseOrTest("testStartWF"));
	// newSuite.addTest(new CaseOrTest("testRunWF"));
	// newSuite.addTest(new CaseOrTest("testFinalNode"));
	return newSuite;
    };

}
