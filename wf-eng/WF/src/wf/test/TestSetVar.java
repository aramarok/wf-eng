


package wf.test;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.XflowException;

public class TestSetVar {

    public static void main (String[] args) throws XflowException {

        String wfId = args[0];
        String name = args[1];
        String value = args[2];
        Integer wfi = new Integer(wfId);
        User user = new User ("rtan", "rtan");

        Integer workflowId = new Integer(wfi.intValue());
        WorkflowManager.setVariable (workflowId, name, value, user);

        ABC abc = new ABC (345, value, 23.23);
        WorkflowManager.setVariable (workflowId, name, abc, user);

        System.out.println (WorkflowManager.getVariable (workflowId, name, user)); 
    }
}
