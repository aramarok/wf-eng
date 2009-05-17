package wf.test;

import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;

public class PornireWorkflow {

	public static void main(String[] args) throws ExceptieWF {

		String wfName = null;
		
		if (args.length ==0 ){
			wfName = "case_and";
		} else {
			wfName = args[0];
		}
		
		ItemModel witem = new ItemModel();
		witem.setProperty("PropA", "XXXX");
		witem.setProperty("PropB", "YYYY");
		witem.setProperty("PropC", new Integer(12));
		String xml = "<a><b>100</b></a>";
		witem.setPayloadXML(xml);
		Integer wfId = ManagerWorkflow.pornesteWorkflow(wfName, witem, new Utilizator(
				"utilizator", "password"));
		System.out.println("Started. Workflow ID: " + wfId);
	}

}
