package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.WorkflowState;

public class GetActiveWorkflowInstances {

	public static void main(String[] args) throws Exception {

		List activeWFs = WorkflowManager.getActiveWorkflows(new User("user", "password"));
		for (int i = 0; i < activeWFs.size(); i++) {

			WorkflowState ws = (WorkflowState) activeWFs.get(i);
			System.out.println("\n----- Worklflow Instance ------");
			System.out.println(ws);
		}
	}
}
