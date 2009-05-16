package wf.server.controller;

import wf.db.Persistence;
import wf.jms.model.*;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class RequestHandlerFactory {

	private static Logger log = Logger.getLogger(RequestHandlerFactory.class);

	Map registeredHandlers = new HashMap();

	public RequestHandlerFactory() {

	}

	private void register(Class aClass, Class handler) {
		registeredHandlers.put(aClass, Persistence
				.enhanceInstanceOfClass(handler));
	}

	public RequestHandler getHandlerFor(Request r) {
		RequestHandler rh = (RequestHandler) registeredHandlers.get(r
				.getClass());
		if (rh != null)
			return rh;
		Class requestClass = r.getClass();
		String className = requestClass.getName();
		if (log.isDebugEnabled()) {
			log.debug("Try to instantiate handler for " + className);
		}
		String baseName = className.substring(className.lastIndexOf('.') + 1);
		String handlerName = "wf.server.controller.handler." + baseName
				+ "Handler";
		try {
			Class handlerClass = getClass().getClassLoader().loadClass(
					handlerName);
			register(requestClass, handlerClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		rh = (RequestHandler) registeredHandlers.get(r.getClass());
		if (rh == null) {
			log.info("Handler for " + r.getClass().getName()
					+ " should be defined at this point!!");
		}
		return rh;
	}

	public static void main(String[] args) {
		RequestHandlerFactory f = new RequestHandlerFactory();
		StartWorkflowRequest req = new StartWorkflowRequest();
		RequestHandler rh = f.getHandlerFor(req);
		System.out.println("rh = " + rh);
	}

}
