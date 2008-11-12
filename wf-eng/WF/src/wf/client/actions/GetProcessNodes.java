


package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.Node;



public class GetProcessNodes {

    public static void main (String[] args) throws Exception {

        String wfId = args[0];
        Integer wfi = new Integer(wfId);
        Integer workflowId = new Integer(wfi.intValue());

        List v = WorkflowManager.getProcessNodes ( workflowId, new User("foo", "foo"));
        for (int i = 0; i < v.size(); i++) {
            Node node = (Node)v.get(i);
            System.out.println (node.getName());
        }
    }
}
