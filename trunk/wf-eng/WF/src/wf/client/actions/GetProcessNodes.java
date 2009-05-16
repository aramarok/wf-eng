package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.model.Nod;

public class GetProcessNodes {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	Integer wfId = new Integer(args[0]);
	Integer workflowId = new Integer(wfId.intValue());
	List v = WorkflowManager.getProcessNodes(workflowId, new Utilizator(
		"utilizator", "password"));
	for (int i = 0; i < v.size(); i++) {
	    Nod node = (Nod) v.get(i);
	    System.out.println(node.getName());
	}
    }
}
