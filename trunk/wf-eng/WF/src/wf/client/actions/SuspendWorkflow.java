package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;

public class SuspendWorkflow {

	public static void main(String[] args) throws WorkFlowException {

		Integer wfId = new Integer(args[0]);
		Integer workflowId = new Integer(wfId.intValue());
		WorkflowManager.suspendWorkflow(workflowId, new User("rtan", "rtan"));
		System.out.println("Success");
	}

}
