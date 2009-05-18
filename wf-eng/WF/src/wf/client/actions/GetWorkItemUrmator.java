package wf.client.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import wf.client.ProcesWF;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;

public class GetWorkItemUrmator {

    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {
	String wfName = args[0];
	String procName = args[1];
	new GetWorkItemUrmator(wfName, procName).start();
    }

    private final String procName;
    private final String workflowName;

    private ProcesWF wp;

    public GetWorkItemUrmator(final String wfName, final String pname) {
	this.workflowName = wfName;
	this.procName = pname;
    }

    public void start() throws ExceptieWF {
	this.wp = new ProcesWF(this.workflowName, -1, this.procName,
		null, new Utilizator("utilizator", "password"));
	ItemModel wi = this.wp.getNextWorkItem();
	System.out.println("Work Item: " + wi);

	System.out.print("Complete this work item? [y/n]:");
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    System.in));
	    String s = in.readLine();
	    if ((s != null) && s.equals("y")) {
		this.wp.completeWorkItem(wi);
	    }
	} catch (Exception e) {
	    System.out.println(e);
	}
    }
}
