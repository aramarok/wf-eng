
package wf.jms.model;

import java.util.List;

import wf.model.WorkflowState;

public class GetActiveWorkflowsResponse extends Response {
  
  public List activeWorkflows;

  public GetActiveWorkflowsResponse (int statusCode, String statusMsg, List v) {
    super (statusCode, statusMsg);
    activeWorkflows = v;
  }

  public GetActiveWorkflowsResponse (int statusCode, List v) {
    super (statusCode);
    activeWorkflows = v;
  }
}
