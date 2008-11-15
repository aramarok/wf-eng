package wf.jms.model;

import wf.model.WorkItem;

public class GetWorkItemResponse extends Response {

	private static final long serialVersionUID = 1L;
	
	public WorkItem workItem;

	public GetWorkItemResponse(int statusCode, String statusMsg, WorkItem wi) {
		super(statusCode, statusMsg);
		workItem = wi;
	}

	public GetWorkItemResponse(int statusCode, WorkItem wi) {
		super(statusCode);
		workItem = wi;
	}

}
