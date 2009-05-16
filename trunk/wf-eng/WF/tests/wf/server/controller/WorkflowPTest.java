package wf.server.controller;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;

public class WorkflowPTest extends TestCase {

    public static void main(final String args[]) throws ExceptieWF {
	testSaveDB();
    }

    public static void testSaveDB() throws ExceptieWF {
	Persistence.getWorkflowP().saveNewWorkflow(8888, "wf", "testCase", -1);
    }

    @SuppressWarnings("unchecked")
    private void printList(final List l) {
	for (Iterator j = l.iterator(); j.hasNext();) {
	    System.out.println("entry = " + j.next());
	}
    }

    public void testGetAllWorkflows() throws Exception {
	List map = Persistence.getWorkflowP().getAllWorkflows();
	this.printList(map);
    }

}
