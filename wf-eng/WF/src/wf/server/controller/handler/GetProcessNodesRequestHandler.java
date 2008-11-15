package wf.server.controller.handler;

import wf.jms.model.GetProcessNodesRequest;
import wf.jms.model.GetProcessNodesResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;
import java.util.List;

public class GetProcessNodesRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		GetProcessNodesRequest req = (GetProcessNodesRequest) r;

		if (log.isDebugEnabled()) {
			log.debug("Servicing GetProcessNodes request.\n\t"
					+ "Workflow Id = " + req.workflowId);
		}
		int response = 0;
		String message = "OK";
		List nodes = null;

		try {
			nodes = WorkflowProcessor.getInstance().getProcessNodes(
					req.workflowId);
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new GetProcessNodesResponse(response, message, nodes);
	}
}
