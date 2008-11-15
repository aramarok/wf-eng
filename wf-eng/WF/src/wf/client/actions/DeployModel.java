package wf.client.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;

public class DeployModel {

	public static void main(String[] args) throws Exception {

		File xmlInputFile = new File(args[0]);
		BufferedReader in = new BufferedReader(new FileReader(xmlInputFile));
		String xml = "";
		String str;
		while ((str = in.readLine()) != null) {
			xml += str;
		}

		try {
			WorkflowManager.deployModel(xml, WorkflowManager.WF, new User(
					"foo", "foo"));
			System.out.println("Success");
		} catch (WorkFlowException xe) {
			System.out.println(xe);
		}
	}
}
