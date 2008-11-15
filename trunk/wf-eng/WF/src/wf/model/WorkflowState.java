package wf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WorkflowState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Integer id;
	public String workflowName;
	public int version;
	public boolean isActive;
	public String state;
	public String initiator;
	public Date timeStarted;
	public Date timeEnded;
	public Map variables;
	public List activeProcesses;

	public WorkflowState() {
		variables = new HashMap();
		activeProcesses = new ArrayList();
	}

	public void setWorkflowId(Integer wfid) {
		id = wfid;
	}

	public Integer getWorkflowId() {
		return id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public void setWorkflowName(String name) {
		workflowName = name;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setState(String s) {
		state = s;
	}

	public String getState() {
		return state;
	}

	public void setInitiator(String s) {
		initiator = s;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setIsActive(boolean b) {
		isActive = b;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setTimeStarted(Date ts) {
		timeStarted = ts;
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeEnded(Date ts) {
		timeEnded = ts;
	}

	public Date getTimeEnded() {
		return timeEnded;
	}

	public void setVariables(Map v) {
		variables = v;
	}

	public Map getVariables() {
		return variables;
	}

	public void setActiveProcesses(List v) {
		activeProcesses = v;
	}

	public List getActiveProcesses() {
		return activeProcesses;
	}

	public String toString() {
		String result = "";
		result += "Workflow ID: " + id + "\n";
		result += "Workflow Name: " + workflowName + "\n";
		result += "Workflow Version: " + version + "\n";
		result += "IsActive: " + isActive + "\n";
		result += "State: " + state + "\n";
		result += "Initiator: " + initiator + "\n";
		result += "Time Started: " + timeStarted + "\n";
		result += "Time Ended: " + timeEnded + "\n";

		result += "Variables: \n";
		Iterator itr = variables.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Object val = variables.get(key);
			result += " Key: " + key;
			result += " Value: " + val + "\n";
		}

		result += "Processes: \n";
		for (int i = 0; i < activeProcesses.size(); i++) {
			ProcessState ps = (ProcessState) activeProcesses.get(i);
			result += ps.toString();
		}

		return result;
	}
}
