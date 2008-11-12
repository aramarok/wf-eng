package wf.server.controller.handler;

import wf.jms.model.GetNodeByNameRequest;
import wf.jms.model.GetNodeByNameResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.model.Node;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;


public class GetNodeByNameRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetNodeByNameRequest req = (GetNodeByNameRequest) r;
    if( log.isDebugEnabled() ){
      log.debug ("Servicing GetNodeByName request");
    }
    int response = 0;
    String message = "OK";
    Node node = null;
    try {
      node = WorkflowProcessor.getInstance().getNodeByName( req.workflowName, req.version, req.nodeName);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetNodeByNameResponse(response, message, node);
  }
}
