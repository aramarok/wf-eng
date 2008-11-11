package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetNextWorkItemResponse;
import xflow.protocol.GetNextWorkItemRequest;
import xflow.common.WorkItem;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 4:12:14 PM
 */
public class GetNextWorkItemRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetNextWorkItem request" );
    }
    GetNextWorkItemRequest req = (GetNextWorkItemRequest) r;    
    int response = 0;
    String message = "OK";
    WorkItem workItem = null;
    try {
      workItem = WorkflowProcessor.getInstance().getNextWorkItem ( req.workflowName, req.processName);
    } catch (Exception e) {
      response = Response.FAILURE;
      e.printStackTrace();
      message = e.getMessage();
    }
    return new GetNextWorkItemResponse(response, message, workItem);
  }
}
