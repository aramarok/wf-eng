package wf.jms.model;

public class GetVariableResponse extends Response {

	private static final long serialVersionUID = 1L;

	public Object variableValue;

	public GetVariableResponse(int statusCode, String statusMsg, Object vv) {
		super(statusCode, statusMsg);
		variableValue = vv;
	}

	public GetVariableResponse(int statusCode, Object vv) {
		super(statusCode);
		variableValue = vv;
	}
}