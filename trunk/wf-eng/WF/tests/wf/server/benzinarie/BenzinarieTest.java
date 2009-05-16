package wf.server.benzinarie;

import java.io.IOException;
import wf.jms.model.ResUrmWI;
import wf.model.ItemModel;
import wf.server.controller.AbstractServerTestCase;

public class BenzinarieTest extends AbstractServerTestCase {

    public static final String MODEL_NAME = "benzinarie";

    static public junit.framework.Test suite() {
	junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
	newSuite.addTest(new BenzinarieTest("testClearDB"));
	newSuite.addTest(new BenzinarieTest("testDeployWF"));
	newSuite.addTest(new BenzinarieTest("testStartWF"));
	// newSuite.addTest(new BenzinarieTest("testMasina"));
	// newSuite.addTest(new BenzinarieTest("testAltceva"));
	// newSuite.addTest(new BenzinarieTest("testUmplere"));
	// newSuite.addTest(new BenzinarieTest("testMasina"));
	// newSuite.addTest(new BenzinarieTest("testPlecare"));
	return newSuite;
    }

    public BenzinarieTest() {
	super();
    }

    public BenzinarieTest(final String s) {
	super(s);
    }

    public void testAltceva() {
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
	ResUrmWI response1 = this.getNextWorkItemForProcess(MODEL_NAME,
		"altceva");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "altceva", response1.workItem);
	assertTrue("wf terminat", this.isWorkFlowActive(wfID));
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
    }

    public void testClearDB() {
	assertTrue("There is already deployed model with the same name", !this
		.doesModelExist(MODEL_NAME));
    }

    public void testDeployWF() throws IOException {
	this.deployWF(MODEL_NAME, "tests/wf/server/benzinarie/benzinarie.xml");
    }

    public void testMasina() {
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
	ResUrmWI response1 = this.getNextWorkItemForProcess(MODEL_NAME,
		"masina");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "masina", response1.workItem);
	assertTrue("wf terminat", this.isWorkFlowActive(wfID));
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
    }

    public void testPlecare() {
	assertTrue("Clientul e spre plecare", this.processHasWorkItems(
		MODEL_NAME, "plecare"));
	ResUrmWI responseProc3 = this.getNextWorkItemForProcess(MODEL_NAME,
		"plecare");
	ItemModel workItem = responseProc3.workItem;
	this.completeWorkItem(MODEL_NAME, "plecare", workItem);
	assertTrue("wf neterminat", !this.isWorkFlowActive(workItem
		.getWorkflowId()));
    }

    public void testStartWF() throws IOException {
	this.startWF(MODEL_NAME);
    }

    public void testUmplere() {
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
	ResUrmWI response1 = this.getNextWorkItemForProcess(MODEL_NAME,
		"umplere");
	Integer wfID = response1.workItem.getWorkflowId();
	this.completeWorkItem(MODEL_NAME, "umplere", response1.workItem);
	assertTrue("wf terminat", this.isWorkFlowActive(wfID));
	assertTrue("Clientul e spre pleacare", !this.processHasWorkItems(
		MODEL_NAME, "plecare"));
    };

}