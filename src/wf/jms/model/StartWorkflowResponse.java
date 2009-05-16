package wf.jms.model;

public class StartWorkflowResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public Integer workflowId;

	public StartWorkflowResponse(int statusCode, String statusMsg,
			Integer workflowId) {
		super(statusCode, statusMsg);
		this.workflowId = workflowId;
	}

	public StartWorkflowResponse(int statusCode, int id) {
		super(statusCode);
		workflowId = new Integer(id);
	}
}
