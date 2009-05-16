package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.SuspendWorkflowRequest;
import wf.jms.model.SuspendWorkflowResponse;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

public class SuspendWorkflowRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		SuspendWorkflowRequest req = (SuspendWorkflowRequest) r;
		if (log.isDebugEnabled()) {
			log.debug("Servicing SuspendWorkflow request."
					+ "\n\tWorkflow Id = " + req.workflowId);
		}
		int response = 0;
		String message = "OK";
		try {
			WorkflowProcessor.getInstance().suspendWorkflow(req.workflowId);
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new SuspendWorkflowResponse(response, message);
	}
}
