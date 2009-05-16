package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.StartWorkflowRequest;
import wf.jms.model.StartWorkflowResponse;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

public class StartWorkflowRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		StartWorkflowRequest req = (StartWorkflowRequest) r;
		if (log.isDebugEnabled()) {
			log.debug("Servicing StartWorkflow request."
					+ "\n\tWorkflow name = " + req.workflowName
					+ "\n\tWorkflow version = " + req.version + "\n"
					+ req.workItem);
		}

		int response = 0;
		Integer workflowId = null;
		String initiator = req.user.getName();
		String message = "OK";

		try {
			workflowId = WorkflowProcessor.getInstance().startWorkflow(
					req.workflowName, req.version, req.workItem, initiator);
			response = Response.SUCCESS;
		} catch (Exception e) {
			response = Response.FAILURE;
			e.printStackTrace();
			message = e.getMessage();
		}
		return new StartWorkflowResponse(response, message, workflowId);
	}

}
