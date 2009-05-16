package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqValidareProces;
import wf.jms.model.ResValidareProces;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqValidareProcesControl implements ControlCereri {
    public Raspuns handle(final Cerere cerere) {
	ReqValidareProces req = (ReqValidareProces) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	boolean valid = false;

	try {
	    valid = WorkflowProcessor.getInstance().validateProcess(
		    req.workflowName, req.workflowVersion, req.processName);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResValidareProces(raspuns, mesaj, valid);
    }

}
