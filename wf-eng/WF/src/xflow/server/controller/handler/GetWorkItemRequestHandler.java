package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetWorkItemResponse;
import xflow.protocol.GetWorkItemRequest;
import xflow.common.WorkItem;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:29:54 PM
 */
public class GetWorkItemRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetWorkItemRequest req = (GetWorkItemRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetWorkItem request");
    }
    int response = 0;
    String message = "OK";
    WorkItem workItem = null;
    try {
      workItem = WorkflowProcessor.getInstance().getWorkItem ( req.workItemId, req.processName);
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }

    return new GetWorkItemResponse(response, message, workItem);
  }
}
