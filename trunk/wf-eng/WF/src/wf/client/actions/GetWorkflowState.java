package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.model.WorkflowState;

public class GetWorkflowState {

	public static void main(String[] args) throws WorkFlowException {

		String wfId = args[0];
		Integer wfi = new Integer(wfId);

		Integer workflowId = new Integer(wfi.intValue());
		WorkflowState ws = WorkflowManager.getWorkflowState(workflowId,
				new User("rtan", "rtan"));
		System.out.println(ws);
	}

}
