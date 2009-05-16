package wf.jms.model;

import java.util.List;

public class GetModelsResponse extends Response {

	private static final long serialVersionUID = 1L;

	public List models;

	public GetModelsResponse(int statusCode, String statusMsg, List v) {
		super(statusCode, statusMsg);
		models = v;
	}

	public GetModelsResponse(int statusCode, List v) {
		super(statusCode);
		models = v;
	}
}
