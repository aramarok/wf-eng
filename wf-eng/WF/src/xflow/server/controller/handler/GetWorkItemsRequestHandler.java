package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetWorkItemsResponse;
import xflow.protocol.GetWorkItemsRequest;

import java.util.List;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:53:28 PM
 */
public class GetWorkItemsRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetWorkItemsRequest req = (GetWorkItemsRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetWorkItems request");
    }
    int response = 0;
    String message = "OK";

    List workItems = null;
    try{
      WorkflowProcessor.getInstance().getWorkItems ( req.workflowName, req.processName);
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetWorkItemsResponse(response, message, workItems);
  }
}
