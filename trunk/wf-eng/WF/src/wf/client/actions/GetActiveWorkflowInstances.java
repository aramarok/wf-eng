package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.model.StareWF;

public class GetActiveWorkflowInstances {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	List activeWFs = WorkflowManager.getActiveWorkflows(new Utilizator(
		"utilizator", "password"));
	for (int i = 0; i < activeWFs.size(); i++) {

	    StareWF ws = (StareWF) activeWFs.get(i);
	    System.out.println("\n----- Worklflow Instance ------");
	    System.out.println(ws);
	}
    }
}
