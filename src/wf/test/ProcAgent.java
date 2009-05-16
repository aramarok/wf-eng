package wf.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import wf.client.InboxMessageListener;
import wf.client.WorkflowProcess;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.jms.JMSTopicConnection;
import wf.model.WorkItem;

public class ProcAgent implements InboxMessageListener {

	private String workflowName;
	private String procName;
	private WorkflowProcess wp;

	public ProcAgent(String wfName, String pname) {
		workflowName = wfName;
		procName = pname;
	}

	public void onMessage(WorkItem witem) {
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
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
	}

	public void start() throws WorkFlowException {
		wp = new WorkflowProcess(workflowName, -1, procName, this, new User(
				"user", "password"));
	}

	public static void main(String[] args) throws WorkFlowException,
			JMSException {
		String wfName = args[0];
		String procName = args[1];
		JMSTopicConnection.initialize();
		new ProcAgent(wfName, procName).start();
	}
}
