

package wf.jms.model;



public class GetNextWorkItemRequest extends WorkflowProcessRequest {

  public GetNextWorkItemRequest() {
  }

  public GetNextWorkItemRequest( String workflowName, String processName ) {
    this.workflowName = workflowName;
    this.processName = processName;
  }

}
