package wf.server.controller.handler;

import wf.jms.model.GetWorkItemRequest;
import wf.jms.model.GetWorkItemResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.model.WorkItem;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

public class GetWorkItemRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		GetWorkItemRequest req = (GetWorkItemRequest) r;
		if (log.isDebugEnabled()) {
			log.debug("Servicing GetWorkItem request");
		}
		int response = 0;
		String message = "OK";
		WorkItem workItem = null;
		try {
			workItem = WorkflowProcessor.getInstance().getWorkItem(
					req.workItemId, req.processName);
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}

		return new GetWorkItemResponse(response, message, workItem);
	}
}
