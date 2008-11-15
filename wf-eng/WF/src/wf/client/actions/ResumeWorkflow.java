package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;

public class ResumeWorkflow {

	public static void main(String[] args) throws WorkFlowException {

		Integer wfId = new Integer(args[0]);
		Integer workflowId = new Integer(wfId.intValue());
		WorkflowManager.resumeWorkflow(workflowId, new User("rtan", "rtan"));
		System.out.println("Success");
	}

}
