package xflow.server.case_condition;

import xflow.server.controller.AbstractServerTestCase;
import xflow.protocol.GetNextWorkItemResponse;

import java.io.IOException;

/**
 * User: kosta
 * Date: Jul 20, 2004
 * Time: 9:02:35 PM
 */
public class CaseConditionTest extends AbstractServerTestCase{

  public static final String MODEL_NAME = "case_condition";


  public CaseConditionTest() {
  }

  public CaseConditionTest(String s) {
    super(s);
  }


  public void testClearDB(){
    assertTrue( "There is already deployed model with the same name",!  doesModelExist( MODEL_NAME ) );
  }

  public void testDeployWF() throws IOException {
    deployWF( MODEL_NAME, "tests/xflow/server/case_condition/case_condition.xf.xml" );
  }



  public void testRunWF_25() throws IOException {
    startWF( MODEL_NAME, new CaseConditionPayload( 25 ) );
    assertProc2_4Unemployed();
    GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, "proc1");
    Integer wfID = response1.workItem.getWorkflowId();
    completeWorkItem(  MODEL_NAME, "proc1", response1.workItem );
    assertTrue( "Workflow is finished", isWorkFlowActive( wfID ) );
    assertTrue( "Proc2 has no work",   processHasWorkItems( MODEL_NAME, "proc2" ) );
    assertTrue( "Proc3 has    work", ! processHasWorkItems( MODEL_NAME, "proc3" ) );
    assertTrue( "Proc4 has    work", ! processHasWorkItems( MODEL_NAME, "proc4" ) );
    finishViaProcess( "proc2");
  }

  public void testRunWF_50() throws IOException {
    startWF( MODEL_NAME, new CaseConditionPayload( 50 ) );
    assertProc2_4Unemployed();
    GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, "proc1");
    Integer wfID = response1.workItem.getWorkflowId();
    completeWorkItem(  MODEL_NAME, "proc1", response1.workItem );
    assertTrue( "Workflow is finished", isWorkFlowActive( wfID ) );
    assertTrue( "Proc2 has    work", ! processHasWorkItems( MODEL_NAME, "proc2" ) );
    assertTrue( "Proc3 has no work",   processHasWorkItems( MODEL_NAME, "proc3" ) );
    assertTrue( "Proc4 has    work", ! processHasWorkItems( MODEL_NAME, "proc4" ) );
    finishViaProcess( "proc3");
  }

  public void testRunWF_10() throws IOException {
    startWF( MODEL_NAME, new CaseConditionPayload( 10 ) );
    assertProc2_4Unemployed();
    GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, "proc1");
    Integer wfID = response1.workItem.getWorkflowId();
    completeWorkItem(  MODEL_NAME, "proc1", response1.workItem );
    assertTrue( "Workflow is finished", isWorkFlowActive( wfID ) );
    assertTrue( "Proc2 has    work", ! processHasWorkItems( MODEL_NAME, "proc2" ) );
    assertTrue( "Proc3 has    work", ! processHasWorkItems( MODEL_NAME, "proc3" ) );
    assertTrue( "Proc4 has no work",   processHasWorkItems( MODEL_NAME, "proc4" ) );
    finishViaProcess( "proc4");
  }

  private void finishViaProcess( String procName) {
    GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, procName);
    Integer wfID = response1.workItem.getWorkflowId();
    completeWorkItem(  MODEL_NAME, procName, response1.workItem );
    assertTrue( "Workflow is not finished", !isWorkFlowActive( wfID ) );
  }

  private void assertProc2_4Unemployed() {
    assertTrue( "Proc2 has work", ! processHasWorkItems( MODEL_NAME, "proc2" ) );
    assertTrue( "Proc3 has work", ! processHasWorkItems( MODEL_NAME, "proc3" ) );
    assertTrue( "Proc4 has work", ! processHasWorkItems( MODEL_NAME, "proc4" ) );
  }

  static public junit.framework.Test suite() {
    junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
    newSuite.addTest( new CaseConditionTest( "testClearDB" ) );
    newSuite.addTest( new CaseConditionTest( "testDeployWF" ) );
    newSuite.addTest( new CaseConditionTest( "testRunWF_25" ) );
    newSuite.addTest( new CaseConditionTest( "testRunWF_50" ) );
    newSuite.addTest( new CaseConditionTest( "testRunWF_10" ) );
    return newSuite;
  };

}
