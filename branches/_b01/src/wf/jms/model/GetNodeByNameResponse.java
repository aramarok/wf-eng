package wf.jms.model;

import wf.model.Node;

public class GetNodeByNameResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public Node node;

	public GetNodeByNameResponse(int statusCode, String statusMsg, Node n) {
		super(statusCode, statusMsg);
		node = n;
	}

	public GetNodeByNameResponse(int statusCode, Node n) {
		super(statusCode);
		node = n;
	}
}