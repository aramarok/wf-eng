package wf.jms.model;

import java.util.List;

public class GetProcessNodesResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public List nodes = null;

	public GetProcessNodesResponse(int statusCode, String statusMsg, List n) {
		super(statusCode, statusMsg);
		nodes = n;
	}

	public GetProcessNodesResponse(int statusCode) {
		super(statusCode);
	}
}
