package wf.client.actions;

import java.util.List;
import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.model.StareWF;

public class GetToateInstanteleWorkflow {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	String wfName = "";
	List v;
	if (args.length > 0) {
	    wfName = args[0];
	    v = ManagerWorkflow.getToateInstanteleWorkflowDupaNume(wfName, new Utilizator(
		    "utilizator", "password"));
	} else {
	    v = ManagerWorkflow.getToateInstanteleWorkflow(new Utilizator("utilizator",
		    "password"));
	}

	for (int i = 0; i < v.size(); i++) {

	    StareWF ws = (StareWF) v.get(i);
	    System.out.println("\n----- Worklflow Instance ------");
	    System.out.println(ws);
	}
    }
}
