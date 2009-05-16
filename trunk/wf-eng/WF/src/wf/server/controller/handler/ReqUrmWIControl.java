package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqUrmWI;
import wf.jms.model.ResUrmWI;
import wf.model.ItemModel;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqUrmWIControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {

	ReqUrmWI req = (ReqUrmWI) cerere;
	int raspuns = 0;
	String mesaj = "OK";
	ItemModel item = null;

	try {
	    item = WorkflowProcessor.getInstance().getNextWorkItem(
		    req.workflowName, req.processName);
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResUrmWI(raspuns, mesaj, item);
    }
}
