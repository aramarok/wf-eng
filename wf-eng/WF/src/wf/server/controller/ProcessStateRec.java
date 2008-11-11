package wf.server.controller;


import wf.model.ProcessState;

import java.lang.Integer;

import java.util.Date;

/**
 * User: kosta
 * Date: Jun 22, 2004
 * Time: 9:30:28 PM
 */
public class ProcessStateRec {

  public int workflowId;
  public String processName;
  public Date timeStarted;
  public int  workItemId;



  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public Date getTimeStarted() {
    return timeStarted;
  }

  public void setTimeStarted(Date timeStarted) {
    this.timeStarted = timeStarted;
  }

  public int getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(int workflowId) {
    this.workflowId = workflowId;
  }

  public int getWorkItemId() {
    return workItemId;
  }

  public void setWorkItemId(int workItemId) {
    this.workItemId = workItemId;
  }

  public ProcessState makeProcessState() {
    ProcessState res = new ProcessState();
    res.setProcessName( processName );
    res.setTimeStarted( timeStarted );
    res.setWorkflowId( new Integer( workflowId ));
    res.setWorkItemId( new Integer( workItemId ));
    return res;
  }
}
