package wf.client.actions;

import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class AnulareWorkflow {

	public static void main(String[] args) throws ExceptieWF {

		Integer wfId = new Integer(args[0]);
		Integer workFlowId = new Integer(wfId.intValue());
		ManagerWorkflow.anuleazaWorkflow(workFlowId, new Utilizator("utilizator", "password"));
		System.out.println("Success");
	}

}
