package wf.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import wf.client.InboxMessageListener;
import wf.client.ProcesWF;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.jms.ConexiuneTopicJMS;
import wf.model.ItemModel;

public class ProcAgent implements InboxMessageListener {

	private String workflowName;
	private String procName;
	private ProcesWF wp;

	public ProcAgent(String wfName, String pname) {
		workflowName = wfName;
		procName = pname;
	}

	public void onMessage(ItemModel witem) {
		System.out.println("Got a work item: " + witem);
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Enter value of PropA: ");
			String s = stdin.readLine();
			witem.setProperty("PropA", s);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Completing work item");
		try {
			wp.completeWorkItem(witem);
		} catch (ExceptieWF e) {
			e.printStackTrace();
		}
	}

	public void start() throws ExceptieWF {
		wp = new ProcesWF(workflowName, -1, procName, this, new Utilizator(
				"utilizator", "password"));
	}

	public static void main(String[] args) throws ExceptieWF,
			JMSException {
		String wfName = args[0];
		String procName = args[1];
		ConexiuneTopicJMS.initialize();
		new ProcAgent(wfName, procName).start();
	}
}
