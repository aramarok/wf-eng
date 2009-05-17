package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class AnuleazaWorkflow {

	public static void main(String[] args) throws ExceptieWF {

		Integer wfId = new Integer(args[0]);
		Integer workFlowId = new Integer(wfId.intValue());
		WorkflowManager.anuleazaWorkflow(workFlowId, new Utilizator("utilizator", "password"));
		System.out.println("Success");
	}

}
