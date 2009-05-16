package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.WorkflowState;

public class GetAllWorkflowInstances {

	public static void main(String[] args) throws Exception {

		String wfName = "";
		List v;
		if (args.length > 0) {
			wfName = args[0];
			v = WorkflowManager.getAllWorkflowsByName(wfName, new User("user",
					"password"));
		} else {
			v = WorkflowManager.getAllWorkflows(new User("user", "password"));
		}

		for (int i = 0; i < v.size(); i++) {

			WorkflowState ws = (WorkflowState) v.get(i);
			System.out.println("\n----- Worklflow Instance ------");
			System.out.println(ws);
		}
	}
}
