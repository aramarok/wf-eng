package wf.client.actions;

import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class GetVariabila {

    public static void main(final String[] args) throws ExceptieWF {

	Integer wfId = new Integer(args[0]);
	String varName = args[1];
	Integer workflowId = new Integer(wfId.intValue());
	Object val = ManagerWorkflow.getVariabla(workflowId, varName,
		new Utilizator("utilizator", "password"));
	System.out.println("Var value: " + val);
    }

}
