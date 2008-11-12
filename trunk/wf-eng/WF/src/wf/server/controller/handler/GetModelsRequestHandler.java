package wf.server.controller.handler;

import wf.jms.model.GetModelsResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

import java.util.List;


public class GetModelsRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetModels request");
    }
    int response = 0;
    String message = "OK";
    List models = null;
    try {
      models = WorkflowProcessor.getInstance().getModels();
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetModelsResponse(response, message, models);
  }
}
