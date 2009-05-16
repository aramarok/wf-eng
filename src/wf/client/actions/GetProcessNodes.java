package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.Node;

public class GetProcessNodes {

	public static void main(String[] args) throws Exception {

		Integer wfId = new Integer(args[0]);
		Integer workflowId = new Integer(wfId.intValue());
		List v = WorkflowManager.getProcessNodes(workflowId, new User("user",
				"password"));
		for (int i = 0; i < v.size(); i++) {
			Node node = (Node) v.get(i);
			System.out.println(node.getName());
		}
	}
}
