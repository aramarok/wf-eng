package wf.server.controller;

public class WorkflowVariable {
	String name;
	String value;
	int workflowid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWorkflowid() {
		return workflowid;
	}

	public void setWorkflowid(int workflowid) {
		this.workflowid = workflowid;
	}
}
