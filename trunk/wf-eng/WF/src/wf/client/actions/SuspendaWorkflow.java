package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class SuspendaWorkflow {

    public static void main(final String[] args) throws ExceptieWF {

	Integer wfId = new Integer(args[0]);
	Integer workflowId = new Integer(wfId.intValue());
	WorkflowManager.suspendaWorkflow(workflowId, new Utilizator("utilizator",
		"password"));
	System.out.println("Success");
    }

}
