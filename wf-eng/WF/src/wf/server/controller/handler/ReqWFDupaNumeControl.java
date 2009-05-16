package wf.server.controller.handler;

import java.util.List;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqWFDupaNume;
import wf.jms.model.ResWFDupaNume;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqWFDupaNumeControl implements ControlCereri {

    @SuppressWarnings("unchecked")
    public Raspuns handle(final Cerere cerere) {
	ReqWFDupaNume req = (ReqWFDupaNume) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	List wfUri = null;

	try {
	    wfUri = WorkflowProcessor.getInstance()
		    .getWorkflowsByName(req.name);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResWFDupaNume(raspuns, mesaj, wfUri);
    }
}
