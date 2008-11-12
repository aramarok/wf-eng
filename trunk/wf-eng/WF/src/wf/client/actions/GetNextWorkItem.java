


package wf.client.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.JMSException;

import wf.client.WorkflowProcess;
import wf.client.auth.User;
import wf.exceptions.XflowException;
import wf.model.WorkItem;


public class GetNextWorkItem {

    private String workflowName;
    private String procName;
    private WorkflowProcess wp;

    public GetNextWorkItem (String wfName, String pname) {
        workflowName = wfName;
        procName = pname;
    }

    public void start() throws XflowException {
        wp = new WorkflowProcess (workflowName, -1, procName, null, new User("rtan","rtan"));
        WorkItem wi = wp.getNextWorkItem();
        System.out.println ("Work Item: " + wi);

        System.out.print ("Complete this work item? [y/n]:");
        try {
           BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
           String s = in.readLine();
           if (s != null && s.equals("y")) {
               wp.completeWorkItem (wi);
	   } 
	} catch (Exception e) {         
	    System.out.println (e);
        }
    }

    public static void main (String[] args) throws XflowException, JMSException {
        String wfName = args[0];
        String procName = args[1];
        new GetNextWorkItem (wfName, procName).start();
    }
}
