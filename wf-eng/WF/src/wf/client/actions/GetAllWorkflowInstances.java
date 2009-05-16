package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.model.StareWF;

public class GetAllWorkflowInstances {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	String wfName = "";
	List v;
	if (args.length > 0) {
	    wfName = args[0];
	    v = WorkflowManager.getAllWorkflowsByName(wfName, new Utilizator(
		    "utilizator", "password"));
	} else {
	    v = WorkflowManager.getAllWorkflows(new Utilizator("utilizator",
		    "password"));
	}

	for (int i = 0; i < v.size(); i++) {

	    StareWF ws = (StareWF) v.get(i);
	    System.out.println("\n----- Worklflow Instance ------");
	    System.out.println(ws);
	}
    }
}
