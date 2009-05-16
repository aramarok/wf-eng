package wf.server.controller;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import wf.db.Persistence;
import wf.exceptions.WorkFlowException;

public class WorkflowPTest extends TestCase {

	public static void main(String args[]) throws WorkFlowException{
		testSaveDB();
	}
	
	public static void testSaveDB() throws WorkFlowException {
		Persistence.getWorkflowP().saveNewWorkflow(8888, "wf", "testCase", -1);
	}

	public void testGetAllWorkflows() throws Exception {
		List map = Persistence.getWorkflowP().getAllWorkflows();
		printList(map);
	}

	private void printList(List l) {

		for (Iterator j = l.iterator(); j.hasNext();) {

			System.out.println("entry = " + j.next());
		}

	}

}
