package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class SetVar {

    public static void main(final String[] args) throws ExceptieWF {

	Integer wfId = new Integer(args[0]);
	String varName = args[1];
	String varValue = args[2];
	Utilizator user = new Utilizator("utilizator", "password");

	Integer workflowId = new Integer(wfId.intValue());
	WorkflowManager.setVariable(workflowId, varName, varValue, user);
	System.out.println("Success");
    }
}
