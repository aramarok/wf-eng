package wf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StareWF implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List activeProcesses;
    public Integer id;
    public String initiator;
    public boolean isActive;
    public String state;
    public Date timeEnded;
    public Date timeStarted;
    @SuppressWarnings("unchecked")
    public Map variables;
    public int version;
    public String workflowName;

    @SuppressWarnings("unchecked")
    public StareWF() {
	this.variables = new HashMap();
	this.activeProcesses = new ArrayList();
    }

    @SuppressWarnings("unchecked")
    public List getActiveProcesses() {
	return this.activeProcesses;
    }

    public Integer getId() {
	return this.id;
    }

    public String getInitiator() {
	return this.initiator;
    }

    public boolean getIsActive() {
	return this.isActive;
    }

    public String getState() {
	return this.state;
    }

    public Date getTimeEnded() {
	return this.timeEnded;
    }

    public Date getTimeStarted() {
	return this.timeStarted;
    }

    @SuppressWarnings("unchecked")
    public Map getVariables() {
	return this.variables;
    }

    public int getVersion() {
	return this.version;
    }

    public Integer getWorkflowId() {
	return this.id;
    }

    public String getWorkflowName() {
	return this.workflowName;
    }

    public boolean isActive() {
	return this.isActive;
    }

    public void setActive(final boolean active) {
	this.isActive = active;
    }

    @SuppressWarnings("unchecked")
    public void setActiveProcesses(final List v) {
	this.activeProcesses = v;
    }

    public void setId(final Integer id) {
	this.id = id;
    }

    public void setInitiator(final String s) {
	this.initiator = s;
    }

    public void setIsActive(final boolean b) {
	this.isActive = b;
    }

    public void setState(final String s) {
	this.state = s;
    }

    public void setTimeEnded(final Date ts) {
	this.timeEnded = ts;
    }

    public void setTimeStarted(final Date ts) {
	this.timeStarted = ts;
    }

    @SuppressWarnings("unchecked")
    public void setVariables(final Map v) {
	this.variables = v;
    }

    public void setVersion(final int version) {
	this.version = version;
    }

    public void setWorkflowId(final Integer wfid) {
	this.id = wfid;
    }

    public void setWorkflowName(final String name) {
	this.workflowName = name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
	String result = "";
	result += "Workflow ID: " + this.id + "\n";
	result += "Workflow Name: " + this.workflowName + "\n";
	result += "Workflow Version: " + this.version + "\n";
	result += "IsActive: " + this.isActive + "\n";
	result += "State: " + this.state + "\n";
	result += "Initiator: " + this.initiator + "\n";
	result += "Time Started: " + this.timeStarted + "\n";
	result += "Time Ended: " + this.timeEnded + "\n";

	result += "Variables: \n";
	Iterator itr = this.variables.keySet().iterator();
	while (itr.hasNext()) {
	    String key = (String) itr.next();
	    Object val = this.variables.get(key);
	    result += " Key: " + key;
	    result += " Value: " + val + "\n";
	}

	result += "Processes: ";
	if (this.activeProcesses != null) {
	    result += this.activeProcesses.size() + "\n";
	    for (int i = 0; i < this.activeProcesses.size(); i++) {
		StareProces ps = (StareProces) this.activeProcesses.get(i);
		result += ps.toString();
	    }
	} else {
	    result += "0\n";
	}

	return result;
    }
}
