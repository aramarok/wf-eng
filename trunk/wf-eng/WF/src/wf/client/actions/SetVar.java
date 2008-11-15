package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;

public class SetVar {

	public static void main(String[] args) throws WorkFlowException {

		Integer wfId = new Integer(args[0]);
		String varName = args[1];
		String varValue = args[2];
		User user = new User("rtan", "rtan");

		Integer workflowId = new Integer(wfId.intValue());
		WorkflowManager.setVariable(workflowId, varName, varValue, user);
		System.out.println("Success");
	}
}
