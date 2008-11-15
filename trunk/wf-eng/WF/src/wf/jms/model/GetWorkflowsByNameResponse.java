package wf.jms.model;

import java.util.List;

public class GetWorkflowsByNameResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public List workflows;

	public GetWorkflowsByNameResponse(int statusCode, String statusMsg, List v) {
		super(statusCode, statusMsg);
		workflows = v;
	}

	public GetWorkflowsByNameResponse(int statusCode, List v) {
		super(statusCode);
		workflows = v;
	}
}