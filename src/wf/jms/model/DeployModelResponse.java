package wf.jms.model;

public class DeployModelResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public DeployModelResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public DeployModelResponse(int statusCode) {
		super(statusCode);
	}
}
