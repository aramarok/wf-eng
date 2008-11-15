package wf.jms.model;

public class SuspendWorkflowResponse extends Response {

	private static final long serialVersionUID = 1L;

	public SuspendWorkflowResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public SuspendWorkflowResponse(int statusCode) {
		super(statusCode);
	}
}
