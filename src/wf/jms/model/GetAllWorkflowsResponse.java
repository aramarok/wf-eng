package wf.jms.model;

import java.util.List;

public class GetAllWorkflowsResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public List workflows;

	public GetAllWorkflowsResponse(int statusCode, String statusMsg, List v) {
		super(statusCode, statusMsg);
		workflows = v;
	}

	public GetAllWorkflowsResponse(int statusCode, List v) {
		super(statusCode);
		workflows = v;
	}
}
