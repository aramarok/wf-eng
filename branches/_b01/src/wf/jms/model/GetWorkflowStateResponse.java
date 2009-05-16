package wf.jms.model;

import wf.model.WorkflowState;

public class GetWorkflowStateResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public WorkflowState workflowState;

	public GetWorkflowStateResponse(int statusCode, String statusMsg,
			WorkflowState s) {
		super(statusCode, statusMsg);
		workflowState = s;
	}

	public GetWorkflowStateResponse(int statusCode, WorkflowState s) {
		super(statusCode);
		workflowState = s;
	}
}
