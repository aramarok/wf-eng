package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.CompleteWorkItemResponse;
import xflow.protocol.CompleteWorkItemRequest;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 4:56:20 PM
 */
public class CompleteWorkItemRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    CompleteWorkItemRequest req = (CompleteWorkItemRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing CompleteWorkItem request");
    }
    int response = 0;
    String message = "OK";

    try {
      WorkflowProcessor.getInstance().completeWorkItem (req.workflowName, req.workflowVersion, req.processName, req.workItem);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE;
      e.printStackTrace();
      message = e.getMessage();
    }
    return new CompleteWorkItemResponse(response, message);
  }
}
