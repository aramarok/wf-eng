package wf.server.controller.handler;

import wf.jms.model.GetAllWorkflowsResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;
import java.util.List;

public class GetAllWorkflowsRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		if (log.isDebugEnabled()) {
			log.debug("Servicing GetAllWorkflows request");
		}
		int response = 0;
		String message = "OK";
		List workflows = null;
		try {
			workflows = WorkflowProcessor.getInstance().getAllWorkflows();
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new GetAllWorkflowsResponse(response, message, workflows);
	}
}
