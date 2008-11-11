package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.StartWorkflowResponse;
import xflow.protocol.StartWorkflowRequest;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 3:24:18 PM
 */
public class StartWorkflowRequestHandler implements RequestHandler{
  
  public Response handle(Request r) {
    StartWorkflowRequest req = (StartWorkflowRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing StartWorkflow request." +
          "\n\tWorkflow name = " + req. workflowName +
          "\n\tWorkflow version = " + req.version +
          "\n" + req.workItem);
    }

    int response = 0;
    Integer workflowId = null;
    String initiator = req.user.getName();
    String message = "OK";

    try {
      workflowId = WorkflowProcessor.getInstance().startWorkflow (req.workflowName, req.version, req.workItem, initiator);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new StartWorkflowResponse(response, message, workflowId);
  }


}
