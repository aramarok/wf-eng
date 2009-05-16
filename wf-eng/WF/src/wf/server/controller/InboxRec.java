package wf.server.controller;

import java.util.Date;

public class InboxRec {

    int gid;
    String procName;
    boolean timeout;
    Date timeStarted;
    int workflowId;
    String workflowName;
    Integer workitemId;

    public int getGid() {
	return this.gid;
    }

    public String getProcName() {
	return this.procName;
    }

    public Date getTimeStarted() {
	return this.timeStarted;
    }

    public int getWorkflowId() {
	return this.workflowId;
    }

    public String getWorkflowName() {
	return this.workflowName;
    }

    public Integer getWorkitemId() {
	return this.workitemId;
    }

    public boolean isTimeout() {
	return this.timeout;
    }

    public void setGid(final int gid) {
	this.gid = gid;
    }

    public void setProcName(final String procName) {
	this.procName = procName;
    }

    public void setTimeout(final boolean timeout) {
	this.timeout = timeout;
    }

    public void setTimeStarted(final Date timeStarted) {
	this.timeStarted = timeStarted;
    }

    public void setWorkflowId(final int workflowId) {
	this.workflowId = workflowId;
    }

    public void setWorkflowName(final String workflowName) {
	this.workflowName = workflowName;
    }

    public void setWorkitemId(final Integer workitemId) {
	this.workitemId = workitemId;
    }

}
