package wf.server.controller.handler;

import wf.jms.model.CompleteWorkItemRequest;
import wf.jms.model.CompleteWorkItemResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

public class CompleteWorkItemRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		CompleteWorkItemRequest req = (CompleteWorkItemRequest) r;
		if (log.isDebugEnabled()) {
			log.debug("Servicing CompleteWorkItem request");
		}
		int response = 0;
		String message = "OK";

		try {
			WorkflowProcessor.getInstance().completeWorkItem(req.workflowName,
					req.workflowVersion, req.processName, req.workItem);
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new CompleteWorkItemResponse(response, message);
	}
}
