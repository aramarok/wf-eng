package wf.jms.model;

public class ValidateProcessResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public boolean ok;

	public ValidateProcessResponse(int statusCode, String statusMsg, boolean b) {
		super(statusCode, statusMsg);
		ok = b;
	}

	public ValidateProcessResponse(int statusCode, boolean b) {
		super(statusCode);
		ok = b;
	}

}
