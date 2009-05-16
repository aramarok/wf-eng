package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqSuspendareWF;
import wf.jms.model.ResSuspendareWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqSuspendareWFControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqSuspendareWF req = (ReqSuspendareWF) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().suspendWorkflow(req.workflowId);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResSuspendareWF(raspuns, mesaj);
    }
}
