package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqRepornireWF;
import wf.jms.model.ResRepornireWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqRepornireWFControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqRepornireWF req = (ReqRepornireWF) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().resumeWorkflow(req.workflowId);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResRepornireWF(raspuns, mesaj);
    }
}
