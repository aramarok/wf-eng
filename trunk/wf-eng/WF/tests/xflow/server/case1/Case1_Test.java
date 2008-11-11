package xflow.server.case1;

import xflow.common.WorkItem;
import xflow.common.WorkflowState;
import xflow.protocol.*;
import xflow.server.controller.AbstractServerTestCase;
import xflow.TestConfig;

import java.io.IOException;
import java.util.List;

/**
 * User: kosta
 * Date: Jun 28, 2004
 * Time: 12:27:08 AM
 */
public class Case1_Test extends AbstractServerTestCase{

  public static final String MODEL_NAME = "case1";


  public Case1_Test() {
    super();
  }

  public Case1_Test(String s) {
    super(s);
  }


  public void testClearDB(){
    assertTrue( "There is already deployed model with the same name",!  doesModelExist( MODEL_NAME ) );
  }

  public void testDeployWF() throws IOException {
    deployWF( MODEL_NAME, "tests/xflow/server/case1/case1.xf.xml" );
  }

  public void testStartWF() throws IOException {
    TestCase1Payload payload = new TestCase1Payload();
    Integer wfID = startWF( MODEL_NAME, payload );
    assertTrue( "Workflow should be active", isWorkFlowActive( wfID ) );
  }

  public void testGetActiveWorkflows(){
    GetActiveWorkflowsRequest req = new GetActiveWorkflowsRequest();
    req.user = TestConfig.getUser();
    GetActiveWorkflowsResponse res = (GetActiveWorkflowsResponse) handleRequest(req);
    List  activeWFs = res.activeWorkflows;
    assertTrue( "There should be at least one active workflow", activeWFs.size() > 0 );
  }

  public void testSetWorkflowVariable(){
    GetActiveWorkflowsRequest req = new GetActiveWorkflowsRequest();
    req.user = TestConfig.getUser();
    GetActiveWorkflowsResponse getActiveWorkflowsResponse = (GetActiveWorkflowsResponse) handleRequest(req);
    WorkflowState wfState = (WorkflowState) getActiveWorkflowsResponse.activeWorkflows.get( 0 );
    Integer wfID = wfState.getId();
    String varName = "testVarName";
    Integer varVal = new Integer( 786 );
    SetVariableRequest setVariableRequest = new SetVariableRequest( wfID, varName, varVal );
    SetVariableResponse res = (SetVariableResponse) handleRequest( setVariableRequest );
    assertResponceOK( res );
    GetVariableRequest getVariableRequest = new GetVariableRequest();
    getVariableRequest.variableName = varName;
    getVariableRequest.workflowId = wfID;
    GetVariableResponse r = (GetVariableResponse) handleRequest( getVariableRequest );
    assertResponceOK( r );
    assertEquals( "variable value is unexpected", varVal, r.variableValue );
  }

  public void testRunWF(){
    assertTrue( "Proc1 has no work",  processHasWorkItems( MODEL_NAME, "proc1" ) );
    GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, "proc1");
    Integer wfID = response1.workItem.getWorkflowId();
    completeWorkItem(  MODEL_NAME, "proc1", response1.workItem );
    assertTrue( "Workflow is finished but should be active", isWorkFlowActive( wfID ) );
    assertTrue( "Proc2 has no work", processHasWorkItems( MODEL_NAME, "proc2" ) );
  }

  public void testFinalNode(){
    assertTrue( "Proc2 has no work", processHasWorkItems( MODEL_NAME, "proc2" ) );
    GetNextWorkItemResponse responseProc3 = getNextWorkItemForProcess(  MODEL_NAME,"proc2" ) ;
    WorkItem workItem = responseProc3.workItem;
    completeWorkItem(  MODEL_NAME, "proc2", workItem );
    assertTrue( "Workflow is not finished", !isWorkFlowActive( workItem.getWorkflowId() ) );
  }


  static public junit.framework.Test suite() {
    junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
    newSuite.addTest( new Case1_Test( "testClearDB" ) );
    newSuite.addTest( new Case1_Test( "testDeployWF" ) );
    newSuite.addTest( new Case1_Test( "testStartWF" ) );
    newSuite.addTest( new Case1_Test( "testGetActiveWorkflows" ) );
    newSuite.addTest( new Case1_Test( "testSetWorkflowVariable" ) );
    newSuite.addTest( new Case1_Test( "testRunWF" ) );
    newSuite.addTest( new Case1_Test( "testFinalNode" ) );
    return newSuite;
  };

}
