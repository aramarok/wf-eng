package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.model.WorkflowState;

public class GetWorkflowState {

	public static void main(String[] args) throws WorkFlowException {

		Integer wfId = new Integer(args[0]);
		Integer workflowId = new Integer(wfId.intValue());
		WorkflowState ws = WorkflowManager.getWorkflowState(workflowId,
				new User("user", "password"));
		System.out.println(ws);
	}

}
