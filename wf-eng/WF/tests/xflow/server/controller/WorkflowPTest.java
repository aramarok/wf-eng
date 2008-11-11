package xflow.server.controller;

import junit.framework.TestCase;
import xflow.common.XflowException;
import xflow.util.Persistence;

import java.util.Iterator;
import java.util.List;


/**
 * User: kosta
 * Date: Jun 21, 2004
 * Time: 10:17:48 PM
 */
public class WorkflowPTest extends TestCase{

  public void testSaveDB() throws XflowException {
    Persistence.getWorkflowP().saveNewWorkflow( 8888, "wf","testCase", -1);
  }

  public void testGetAllWorkflows() throws Exception {
    List map = Persistence.getWorkflowP().getAllWorkflows();
    printList( map );
  }

  private void printList(List l) {

    for (Iterator j = l.iterator(); j.hasNext();) {

      System.out.println("entry = " + j.next() );
    }

  }




}
