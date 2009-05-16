package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.model.StareWF;

public class GetWorkflowState {

    public static void main(final String[] args) throws ExceptieWF {

	Integer wfId = new Integer(args[0]);
	Integer workflowId = new Integer(wfId.intValue());
	StareWF ws = WorkflowManager.getWorkflowState(workflowId,
		new Utilizator("utilizator", "password"));
	System.out.println(ws);
    }

}
