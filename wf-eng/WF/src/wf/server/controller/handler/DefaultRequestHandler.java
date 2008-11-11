package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 7:59:26 PM
 */
public class DefaultRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    return r.service();
  }
}
