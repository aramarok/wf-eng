package wf.client.actions;

import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.model.StareWF;

public class GetStareWorkflow {

    public static void main(final String[] args) throws ExceptieWF {

	Integer wfId = new Integer(args[0]);
	Integer workflowId = new Integer(wfId.intValue());
	StareWF ws = ManagerWorkflow.getStareWorkflow(workflowId,
		new Utilizator("utilizator", "password"));
	System.out.println(ws);
    }

}
