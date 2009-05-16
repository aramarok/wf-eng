package wf.webservice;

import java.util.ArrayList;
import java.util.List;

import wf.client.WorkflowManager;
import wf.client.WorkflowProcess;
import wf.client.auth.User;
import wf.exceptions.WorkFlowException;
import wf.model.WorkItem;
import wf.model.WorkflowState;

public class WfService {

	public Integer startWorkflow(String workflowName, int version,
			WorkItem witem, User user) {
		Integer wfId = null;
		try {
			System.out.println("**** workflowName: " + workflowName);
			System.out.println("**** version: " + version);
			System.out.println("**** workitem: " + witem);
			System.out.println("**** user: " + user);
			if (version == -1) {
				wfId = WorkflowManager.startWorkflow(workflowName, witem, user);
			} else {
				wfId = WorkflowManager.startWorkflow(workflowName, version,
						witem, user);
			}
			System.out.println("Workflow Started");
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}

		return wfId;
	}

	public void abortWorkflow(int workflowId, User user) {

		System.out.println("Aborting workflow");
		try {
			WorkflowManager.abortWorkflow(new Integer(workflowId), user);
			System.out.println("Workflow Aborted");
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
	}

	public WorkflowState getWorkflowState(int workflowId, User user) {
		try {
			return WorkflowManager.getWorkflowState(new Integer(workflowId),
					user);
		} catch (WorkFlowException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setVariable(int workflowId, String variableName,
			Object variableValue, User user) {
		try {
			WorkflowManager.setVariable(new Integer(workflowId), variableName,
					variableValue, user);
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
	}

	public Object getVariable(int workflowId, String variableName, User user) {
		try {
			return WorkflowManager.getVariable(new Integer(workflowId),
					variableName, user);
		} catch (WorkFlowException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	public List getActiveWorkflows(User user) {
		try {
			return WorkflowManager.getActiveWorkflows(user);
		} catch (WorkFlowException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void deployModel(String xml, String type, User user) {
		try {
			WorkflowManager.deployModel(xml, type, user);
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
	}

	public List getWorkItems(String wfName, String processName, User user) {
		List v = null;
		try {
			WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName,
					null, user);
			v = wfp.getWorkItems();
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
		return v;
	}

	public WorkItem getNextWorkItem(String wfName, String processName, User user) {
		WorkItem wi = null;
		try {
			WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName,
					null, user);
			wi = wfp.getNextWorkItem();
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
		return wi;
	}

	public WorkItem getWorkItem(String wfName, String processName, int id,
			User user) {
		WorkItem wi = null;
		try {
			WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName,
					null, user);
			wi = wfp.getWorkItem(new Integer(id));
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
		return wi;
	}

	public void completeWorkItem(String wfName, String processName,
			WorkItem witem, User user) {
		try {
			WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName,
					null, user);
			wfp.completeWorkItem(witem);
		} catch (WorkFlowException e) {
			e.printStackTrace();
		}
	}

	public String xxx() {
		return "XXX";
	}

	public List yyy() {
		List v = new ArrayList();
		v.add("aaa");
		v.add("bbb");
		return v;
	}

}
