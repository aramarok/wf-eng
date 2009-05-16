package wf.model;

import java.io.Serializable;
import java.util.Date;

public class StareProces implements Serializable {

    private static final long serialVersionUID = 1L;

    public String processName;
    public Date timeStarted;
    public Integer workflowId;
    public Integer workItemId;

    public StareProces() {
    }

    public String getProcessName() {
	return this.processName;
    }

    public Date getTimeStarted() {
	return this.timeStarted;
    }

    public Integer getWorkflowId() {
	return this.workflowId;
    }

    public Integer getWorkItemId() {
	return this.workItemId;
    }

    public void setProcessName(final String n) {
	this.processName = n;
    }

    public void setTimeStarted(final Date d) {
	this.timeStarted = d;
    }

    public void setWorkflowId(final Integer id) {
	this.workflowId = id;
    }

    public void setWorkItemId(final Integer id) {
	this.workItemId = id;
    }

    @Override
    public String toString() {
	String result = "";
	result += "  Workflow ID: " + this.workflowId + "\n";
	result += "  Process Name: " + this.processName + "\n";
	result += "  Time Started: " + this.timeStarted + "\n";
	result += "  Work Item ID: " + this.workItemId + "\n";
	return result;
    }
}
