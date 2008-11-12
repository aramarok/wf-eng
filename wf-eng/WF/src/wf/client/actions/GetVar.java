


package wf.client.actions;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.XflowException;



public class GetVar {

    public static void main (String[] args) throws XflowException {

        String wfId = args[0];
        String name = args[1];
        Integer wfi = new Integer(wfId);

        Integer workflowId = new Integer(wfi.intValue());
        Object val = WorkflowManager.getVariable (workflowId, name, new User("rtan", "rtan"));
        System.out.println ("Var value: " + val);
    }

}
