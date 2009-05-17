package wf.client.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class IncarcaModel {

	public static void main(String[] args) throws Exception {

		File xmlInputFile = new File(
				"C:/Documents and Settings/utilizator/workspace1/WF/src/wf/test/testRule.xml");
		BufferedReader in = new BufferedReader(new FileReader(xmlInputFile));
		String xml = "";
		String str;
		while ((str = in.readLine()) != null) {
			xml += str;
		}

		try {
			WorkflowManager.incarcaModel(xml, WorkflowManager.WF, new Utilizator(
					"utilizator", "password"));
			System.out.println("Success");
		} catch (ExceptieWF xe) {
			System.out.println(xe);
		}
	}
}
