package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.model.ModelWF;

public class GetWorkflowModels {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	List wfModels = WorkflowManager.getWorkflowModels(new Utilizator(
		"utilizator", "password"));
	for (int i = 0; i < wfModels.size(); i++) {
	    ModelWF m = (ModelWF) wfModels.get(i);
	    System.out.println("\n----- Worklflow Model ------");
	    System.out.println("Name:" + m.name);
	    System.out.println("Version: " + m.version);
	    System.out.println("Description: " + m.description);
	}
    }
}
