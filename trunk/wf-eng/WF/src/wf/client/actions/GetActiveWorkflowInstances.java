


package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.WorkflowState;



public class GetActiveWorkflowInstances {

    public static void main (String[] args) throws Exception {

        List v = WorkflowManager.getActiveWorkflows (new User("foo", "foo"));
        for (int i = 0; i < v.size(); i++) {

            WorkflowState ws = (WorkflowState) v.get(i);
            System.out.println ("\n----- Worklflow Instance ------");
            System.out.println (ws);
        }
    }
}
