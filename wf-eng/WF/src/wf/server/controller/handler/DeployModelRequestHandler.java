package wf.server.controller.handler;

import wf.jms.model.DeployModelRequest;
import wf.jms.model.DeployModelResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;


/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 1:20:13 PM
 */
public class DeployModelRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    DeployModelRequest req = (DeployModelRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing DeployModel request:" + req.type + req.xml );
    }

    int response = 0;
    String msg = "OK";
    try {
      WorkflowProcessor.getInstance().deployModel (req.xml, req.type, req.user.getName());
      response = Response.SUCCESS;
    } catch (Exception e) {
      e.printStackTrace();
      response = Response.FAILURE;
      msg = e.getMessage();
    }

    return new DeployModelResponse(response, msg);

  }

}
