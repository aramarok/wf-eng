


package wf.client.actions;

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.Destination;
import wf.model.Node;


public class GetNodeByNodeName {

    public static void main (String[] args) throws Exception {

        String workflowName = args[0];
        String versionStr = args[1];
        int version = new Integer(versionStr).intValue();
        String nodeName = args[2];

        Node node = WorkflowManager.getNodeByName (workflowName, version, nodeName, 
                                                  new User("foo", "foo"));
        node.print();
        System.out.println("");
        System.out.println ("Nodes which transition into this node: ");
        List fromNodes = node.getFromNodes();
        for (int i = 0; i < fromNodes.size(); i++) {
            Node fnode = (Node) fromNodes.get(i);
            fnode.print();
        }
        System.out.println ("");
        System.out.println ("Nodes which this node transitions to: ");
        List destinations = node.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination)destinations.get(i);
            d.node.print();
            if (d.rule != null) {
		System.out.println ("rule: " + d.rule);
	    }
        }
    }
}
