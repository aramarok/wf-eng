package wf.server.case_and;

import java.io.IOException;

import wf.jms.model.GetNextWorkItemResponse;
import wf.model.WorkItem;
import wf.server.controller.AbstractServerTestCase;


public class CaseAndTest  extends AbstractServerTestCase{

  public static final String MODEL_NAME = "case_and";

  public CaseAndTest() {
    super();
  }

  public CaseAndTest(String s) {
    super(s);
  }


  public void testClearDB(){
    assertTrue( "There is already deployed model with the same name",!  doesModelExist( MODEL_NAME ) );
  }

  public void testDeployWF() throws IOException {
    deployWF( MODEL_NAME, "tests/xflow/server/case_and/case_and.xf.xml" );
  }

  public void testStartWF() throws IOException {
     startWF( MODEL_NAME );
   }

   public void testRunWF(){
     assertTrue( "Proc3 has work", ! processHasWorkItems( MODEL_NAME, "proc3" ) );
     GetNextWorkItemResponse response1 = getNextWorkItemForProcess( MODEL_NAME, "proc1");
     Integer wfID = response1.workItem.getWorkflowId();
     completeWorkItem(  MODEL_NAME, "proc1", response1.workItem );
     assertTrue( "Workflow is finished", isWorkFlowActive( wfID ) );
     assertTrue( "Proc3 has work",! processHasWorkItems( MODEL_NAME, "proc3" ) );

     GetNextWorkItemResponse response2 = getNextWorkItemForProcess( MODEL_NAME, "proc2");
     wfID = response2.workItem.getWorkflowId();
     completeWorkItem(  MODEL_NAME, "proc2", response2.workItem );
     assertTrue( "Workflow is finished", isWorkFlowActive( wfID ) );
     assertTrue( "Proc3 has no work", processHasWorkItems( MODEL_NAME, "proc3" ) );
   }


   public void testFinalNode(){
     assertTrue( "Proc3 has no work", processHasWorkItems( MODEL_NAME, "proc3" ) );
     GetNextWorkItemResponse responseProc3 = getNextWorkItemForProcess(  MODEL_NAME,"proc3" ) ;
     WorkItem workItem = responseProc3.workItem;
     completeWorkItem(  MODEL_NAME, "proc3", workItem );
     assertTrue( "Workflow is not finished", !isWorkFlowActive( workItem.getWorkflowId() ) );
   }


  static public junit.framework.Test suite() {
    junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
    newSuite.addTest( new CaseAndTest( "testClearDB" ) );
    newSuite.addTest( new CaseAndTest( "testDeployWF" ) );
    newSuite.addTest( new CaseAndTest( "testStartWF" ) );
    newSuite.addTest( new CaseAndTest( "testRunWF" ) );
    newSuite.addTest( new CaseAndTest( "testFinalNode" ) );
    return newSuite;
  };

}
