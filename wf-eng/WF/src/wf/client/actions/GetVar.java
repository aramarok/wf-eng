package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;

public class GetVar {

	public static void main(String[] args) throws WorkFlowException {

		Integer wfId = new Integer(args[0]);
		String varName = args[1];
		Integer workflowId = new Integer(wfId.intValue());
		Object val = WorkflowManager.getVariable(workflowId, varName, new User(
				"user", "password"));
		System.out.println("Var value: " + val);
	}

}
