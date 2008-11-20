package wf.client.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import wf.client.WorkflowProcess;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.model.WorkItem;

public class GetNextWorkItem {

	private String workflowName;
	private String procName;
	private WorkflowProcess wp;

	public GetNextWorkItem(String wfName, String pname) {
		workflowName = wfName;
		procName = pname;
	}

	public void start() throws WorkFlowException {
		wp = new WorkflowProcess(workflowName, -1, procName, null, new User(
				"user", "password"));
		WorkItem wi = wp.getNextWorkItem();
		System.out.println("Work Item: " + wi);

		System.out.print("Complete this work item? [y/n]:");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String s = in.readLine();
			if (s != null && s.equals("y")) {
				wp.completeWorkItem(wi);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) throws WorkFlowException,
			JMSException {
		String wfName = args[0];
		String procName = args[1];
		new GetNextWorkItem(wfName, procName).start();
	}
}
