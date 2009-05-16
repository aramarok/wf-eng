package wf.jms.model;

public class SetVariableResponse extends Response {

	private static final long serialVersionUID = 1L;

	public SetVariableResponse(int statusCode, String statusMsg) {
		super(statusCode, statusMsg);
	}

	public SetVariableResponse(int statusCode) {
		super(statusCode);
	}

}
