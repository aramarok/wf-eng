package wf.server.controller.handler;

import wf.jms.model.GetWorkItemsRequest;
import wf.jms.model.GetWorkItemsResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

import java.util.List;


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
