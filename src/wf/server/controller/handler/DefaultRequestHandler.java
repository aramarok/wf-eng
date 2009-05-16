package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;

public class DefaultRequestHandler implements RequestHandler {

	public Response handle(Request r) {
		return r.service();
	}
}
