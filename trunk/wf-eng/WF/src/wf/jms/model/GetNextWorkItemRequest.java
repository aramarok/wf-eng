package wf.jms.model;

public class GetNextWorkItemRequest extends WorkflowProcessRequest {

	private static final long serialVersionUID = 1L;

	public GetNextWorkItemRequest() {
	}

	public GetNextWorkItemRequest(String workflowName, String processName) {
		this.workflowName = workflowName;
		this.processName = processName;
	}

}
