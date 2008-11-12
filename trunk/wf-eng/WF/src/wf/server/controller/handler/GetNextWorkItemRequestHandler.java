package wf.server.controller.handler;

import wf.jms.model.GetNextWorkItemRequest;
import wf.jms.model.GetNextWorkItemResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.model.WorkItem;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;


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
