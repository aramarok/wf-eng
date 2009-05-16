package wf.server.controller;

public class WorkflowVariable {
    String name;
    String value;
    int workflowid;

    public String getName() {
	return this.name;
    }

    public String getValue() {
	return this.value;
    }

    public int getWorkflowid() {
	return this.workflowid;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public void setValue(final String value) {
	this.value = value;
    }

    public void setWorkflowid(final int workflowid) {
	this.workflowid = workflowid;
    }
}
