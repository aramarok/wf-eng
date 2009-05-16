package wf.client.actions;

import java.util.List;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.model.Destinatie;
import wf.model.Nod;

public class GetNodeByNodeName {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {

	String workFlowName = args[0];
	String sVersion = args[1];
	int version = new Integer(sVersion).intValue();
	String nodeName = args[2];

	Nod node = WorkflowManager.getNodeByName(workFlowName, version,
		nodeName, new Utilizator("utilizator", "password"));
	node.print();
	System.out.println("");
	System.out.println("Nodes which transition into this node: ");
	List fromNodes = node.getFromNodes();
	for (int i = 0; i < fromNodes.size(); i++) {
	    Nod fnode = (Nod) fromNodes.get(i);
	    fnode.print();
	}
	System.out.println("");
	System.out.println("Nodes which this node transitions to: ");
	List destinations = node.getDestinations();
	for (int i = 0; i < destinations.size(); i++) {
	    Destinatie d = (Destinatie) destinations.get(i);
	    d.node.print();
	    if (d.rule != null) {
		System.out.println("rule: " + d.rule);
	    }
	}
    }
}
