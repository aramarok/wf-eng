


package wf.test;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.model.WorkItem;

public class StartWorkflow {

    public static void main (String[] args) throws WorkFlowException {

        String wfName = args[0];
        WorkItem witem = new WorkItem();
        witem.setProperty("PropA", "XXXX");
        witem.setProperty("PropB", "YYYY");
        witem.setProperty("PropC", new Integer(12));
        String xml = "<a><b>100</b></a>";
        witem.setPayloadXML (xml);
        Integer wfId = WorkflowManager.startWorkflow (wfName, witem, new User("rtan", "rtan"));
        System.out.println ("Started. Workflow ID: " + wfId);
    }

}
