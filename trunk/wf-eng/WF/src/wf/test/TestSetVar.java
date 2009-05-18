package wf.test;

import wf.client.ManagerWorkflow;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;

public class TestSetVar {

	public static void main(String[] args) throws ExceptieWF {

		String wfId = args[0];
		String name = args[1];
		String value = args[2];
		Integer wfi = new Integer(wfId);
		Utilizator user = new Utilizator("utilizator", "password");

		Integer workflowId = new Integer(wfi.intValue());
		ManagerWorkflow.setVariabla(workflowId, name, value, user);

		ABC abc = new ABC(345, value, 23.23);
		ManagerWorkflow.setVariabla(workflowId, name, abc, user);

		System.out.println(ManagerWorkflow.getVariabla(workflowId, name, user));
	}
}
