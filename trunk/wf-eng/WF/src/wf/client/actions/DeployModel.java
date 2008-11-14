


package wf.client.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;


public class DeployModel {

    public static void main (String[] args) throws Exception {

        String xmlFileName = args[0];

        File f = new File(xmlFileName);
        BufferedReader in = new BufferedReader(new FileReader(xmlFileName));
        String xml = "";
        String str;
        while ((str = in.readLine()) != null) {
            xml += str;
        }

        try {
            WorkflowManager.deployModel (xml, WorkflowManager.XFLOW, new User("foo", "foo"));
            System.out.println ("Success");
        } catch (WorkFlowException xe) {
            System.out.println (xe);
        } 
    }
}
