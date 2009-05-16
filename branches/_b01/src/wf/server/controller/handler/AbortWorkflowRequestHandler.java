package wf.server.controller.handler;

import wf.jms.model.AbortWorkflowRequest;
import wf.jms.model.AbortWorkflowResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

public class AbortWorkflowRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		AbortWorkflowRequest req = (AbortWorkflowRequest) r;
		if (log.isDebugEnabled()) {
			log.debug("Servicing AbortWorkflow request. Workflow Id = "
					+ req.workflowId);
		}
		int response = 0;
		String message = "OK";

		try {
			WorkflowProcessor.getInstance().abortWorkflow(req.workflowId,
					req.user.getName());
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new AbortWorkflowResponse(response, message);
	}

}
