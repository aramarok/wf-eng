package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.WorkflowModel;

public class GetWorkflowModels {

	public static void main(String[] args) throws Exception {

		List wfModels = WorkflowManager.getWorkflowModels(new User("foo", "foo"));
		for (int i = 0; i < wfModels.size(); i++) {
			WorkflowModel m = (WorkflowModel) wfModels.get(i);
			System.out.println("\n----- Worklflow Model ------");
			System.out.println("Name:" + m.name);
			System.out.println("Version: " + m.version);
			System.out.println("Description: " + m.description);
		}
	}
}
