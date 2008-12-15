package wf.server.benzinarie;

import java.io.IOException;

import wf.jms.model.GetNextWorkItemResponse;
import wf.model.WorkItem;
import wf.server.case_and.CaseAndTest;
import wf.server.controller.AbstractServerTestCase;

public class BenzinarieTest extends AbstractServerTestCase{

	public static final String MODEL_NAME = "benzinarie";

	public BenzinarieTest() {
		super();
	}

	public BenzinarieTest(String s) {
		super(s);
	}
	
	public void testClearDB() {
		assertTrue("There is already deployed model with the same name",
				!doesModelExist(MODEL_NAME));
	}

	public void testDeployWF() throws IOException {
		deployWF(MODEL_NAME, "tests/wf/server/benzinarie/benzinarie.xml");
	}

	public void testStartWF() throws IOException {
		startWF(MODEL_NAME);
	}
	
	public void testMasina() {
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
		GetNextWorkItemResponse response1 = getNextWorkItemForProcess(MODEL_NAME, "masina");
		Integer wfID = response1.workItem.getWorkflowId();
		completeWorkItem(MODEL_NAME, "masina", response1.workItem);
		assertTrue("wf terminat", isWorkFlowActive(wfID));
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
	}
	
	public void testAltceva() {
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
		GetNextWorkItemResponse response1 = getNextWorkItemForProcess(MODEL_NAME, "altceva");
		Integer wfID = response1.workItem.getWorkflowId();
		completeWorkItem(MODEL_NAME, "altceva", response1.workItem);
		assertTrue("wf terminat", isWorkFlowActive(wfID));
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
	}
	
	public void testUmplere() {
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
		GetNextWorkItemResponse response1 = getNextWorkItemForProcess(MODEL_NAME, "umplere");
		Integer wfID = response1.workItem.getWorkflowId();
		completeWorkItem(MODEL_NAME, "umplere", response1.workItem);
		assertTrue("wf terminat", isWorkFlowActive(wfID));
		assertTrue("Clientul e spre pleacare", !processHasWorkItems(MODEL_NAME, "plecare"));
	}

	public void testPlecare() {
		assertTrue("Clientul e spre plecare" , processHasWorkItems(MODEL_NAME, "plecare"));
		GetNextWorkItemResponse responseProc3 = getNextWorkItemForProcess(MODEL_NAME, "plecare");
		WorkItem workItem = responseProc3.workItem;
		completeWorkItem(MODEL_NAME, "plecare", workItem);
		assertTrue("wf neterminat", !isWorkFlowActive(workItem.getWorkflowId()));
	}
	
	static public junit.framework.Test suite() {
		junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
		//newSuite.addTest(new BenzinarieTest("testClearDB"));
		//newSuite.addTest(new BenzinarieTest("testDeployWF"));
//		newSuite.addTest(new BenzinarieTest("testStartWF"));
		//newSuite.addTest(new BenzinarieTest("testMasina"));
		//newSuite.addTest(new BenzinarieTest("testAltceva"));
		//newSuite.addTest(new BenzinarieTest("testUmplere"));
		//newSuite.addTest(new BenzinarieTest("testMasina"));
		//newSuite.addTest(new BenzinarieTest("testMasina"));
		newSuite.addTest(new BenzinarieTest("testPlecare"));
		return newSuite;
	};
	
}