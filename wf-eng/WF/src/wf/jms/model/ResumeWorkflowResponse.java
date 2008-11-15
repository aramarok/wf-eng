package wf.jms.model;

public class ResumeWorkflowResponse extends Response {

	private static final long serialVersionUID = 1L;

	public ResumeWorkflowResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public ResumeWorkflowResponse(int statusCode) {
		super(statusCode);
	}
}
