package wf.jms.model;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SUCCESS = 0;
	public static final int FAILURE = -1;

	public int responseCode;
	public String message;

	public Response() {
	}

	public Response(int statusCode, String statusMsg) {
		responseCode = statusCode;
		message = statusMsg;
	}

	public Response(int statusCode) {
		responseCode = statusCode;
		message = null;
	}
}
