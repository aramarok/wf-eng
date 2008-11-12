


package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.XflowException;


public class AbortWorkflow {

    public static void main (String[] args) throws XflowException {

        String wfId = args[0];
        Integer wfi = new Integer(wfId);
        Integer workflowId = new Integer(wfi.intValue());
        WorkflowManager.abortWorkflow (workflowId, new User("rtan", "rtan"));
        System.out.println ("Success");
    }

}
