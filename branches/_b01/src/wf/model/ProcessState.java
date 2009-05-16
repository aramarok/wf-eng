package wf.model;

import java.io.Serializable;
import java.util.Date;

public class ProcessState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Integer workflowId;
	public String processName;
	public Date timeStarted;
	public Integer workItemId;

	public ProcessState() {
	}

	public void setWorkflowId(Integer id) {
		workflowId = id;
	}

	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setProcessName(String n) {
		processName = n;
	}

	public String getProcessName() {
		return processName;
	}

	public void setTimeStarted(Date d) {
		timeStarted = d;
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setWorkItemId(Integer id) {
		workItemId = id;
	}

	public Integer getWorkItemId() {
		return workItemId;
	}

	public String toString() {
		String result = "";
		result += "  Workflow ID: " + workflowId + "\n";
		result += "  Process Name: " + processName + "\n";
		result += "  Time Started: " + timeStarted + "\n";
		result += "  Work Item ID: " + workItemId + "\n";
		return result;
	}
}
