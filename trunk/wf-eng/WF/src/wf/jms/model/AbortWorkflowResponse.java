package wf.jms.model;

public class AbortWorkflowResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public AbortWorkflowResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public AbortWorkflowResponse(int statusCode) {
		super(statusCode);
	}
}
