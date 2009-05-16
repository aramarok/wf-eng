package wf.jms.model;

public class CompleteWorkItemResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public CompleteWorkItemResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public CompleteWorkItemResponse(int statusCode) {
		super(statusCode);
	}

}
