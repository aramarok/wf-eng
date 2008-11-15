package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.WorkflowState;

public class GetAllWorkflowInstances {

	public static void main(String[] args) throws Exception {

		String name = "";
		List v;
		if (args.length > 0) {
			name = args[0];
			v = WorkflowManager.getAllWorkflowsByName(name, new User("foo",
					"foo"));
		} else {
			v = WorkflowManager.getAllWorkflows(new User("foo", "foo"));
		}

		for (int i = 0; i < v.size(); i++) {

			WorkflowState ws = (WorkflowState) v.get(i);
			System.out.println("\n----- Worklflow Instance ------");
			System.out.println(ws);
		}
	}
}
