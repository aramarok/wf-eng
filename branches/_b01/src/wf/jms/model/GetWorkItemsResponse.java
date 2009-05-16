package wf.jms.model;

import java.util.List;

public class GetWorkItemsResponse extends Response {

	private static final long serialVersionUID = 1L;

	public List workItems;

	public GetWorkItemsResponse(int statusCode, String statusMsg, List v) {
		super(statusCode, statusMsg);
		workItems = v;
	}

	public GetWorkItemsResponse(int statusCode, List v) {
		super(statusCode);
		workItems = v;
	}

}
