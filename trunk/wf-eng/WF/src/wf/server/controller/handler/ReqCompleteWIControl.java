package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqCompleteWI;
import wf.jms.model.ResCompleteWI;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqCompleteWIControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqCompleteWI req = (ReqCompleteWI) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().completeWorkItem(req.workflowName,
		    req.workflowVersion, req.processName, req.workItem);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResCompleteWI(raspuns, mesaj);
    }
}
