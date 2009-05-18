package wf.client;

import java.util.List;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.jms.SynchQueueMessaging;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqAbortWF;
import wf.jms.model.ReqDeployModel;
import wf.jms.model.ReqModeleDisponibile;
import wf.jms.model.ReqNodDupaNume;
import wf.jms.model.ReqNoduriProces;
import wf.jms.model.ReqRepornireWF;
import wf.jms.model.ReqSetareVariabila;
import wf.jms.model.ReqStareWF;
import wf.jms.model.ReqStartWF;
import wf.jms.model.ReqSuspendareWF;
import wf.jms.model.ReqToateWF;
import wf.jms.model.ReqVariabila;
import wf.jms.model.ReqWFActive;
import wf.jms.model.ReqWFDupaNume;
import wf.jms.model.ResAbortWF;
import wf.jms.model.ResDeployModel;
import wf.jms.model.ResModeleDisponibile;
import wf.jms.model.ResNodDupaNume;
import wf.jms.model.ResNoduriProces;
import wf.jms.model.ResRepornireWF;
import wf.jms.model.ResSetareVariabila;
import wf.jms.model.ResStareWF;
import wf.jms.model.ResStartWF;
import wf.jms.model.ResSuspendareWF;
import wf.jms.model.ResToateWF;
import wf.jms.model.ResVariabila;
import wf.jms.model.ResWFActive;
import wf.jms.model.ResWFDupaNume;
import wf.model.ItemModel;
import wf.model.Nod;
import wf.model.StareWF;
import wf.util.Util;

public class ManagerWorkflow {

    public static final String BPEL = "BPEL";
    public static final String WF = "WF";

    public static ResAbortWF anuleazaWorkflow(final Integer workflowId,
	    final Utilizator user) throws ExceptieWF {

	ReqAbortWF req = new ReqAbortWF();
	req.workflowId = workflowId;
	req.utilizator = user;

	ResAbortWF resp = (ResAbortWF) trimiteCerere(req);
	return resp;
    }

    public static ResDeployModel incarcaModel(final String xml,
	    final String type, final Utilizator user) throws ExceptieWF {
	ReqDeployModel req = new ReqDeployModel();
	req.utilizator = user;
	req.xml = xml;
	req.type = type;
	ResDeployModel resp = (ResDeployModel) trimiteCerere(req);
	return resp;
    }

    @SuppressWarnings("unchecked")
    public static List getInstanteActiveWorkflow(final Utilizator user)
	    throws ExceptieWF {

	ReqWFActive req = new ReqWFActive();
	req.utilizator = user;
	ResWFActive resp = (ResWFActive) trimiteCerere(req);
	return resp.activeWorkflows;
    }

    @SuppressWarnings("unchecked")
    public static List getToateInstanteleWorkflow(final Utilizator user) throws ExceptieWF {

	ReqToateWF req = new ReqToateWF();
	req.utilizator = user;
	ResToateWF resp = (ResToateWF) trimiteCerere(req);
	return resp.workflows;
    }

    @SuppressWarnings("unchecked")
    public static List getToateInstanteleWorkflowDupaNume(final String name,
	    final Utilizator user) throws ExceptieWF {

	ReqWFDupaNume req = new ReqWFDupaNume();
	req.utilizator = user;
	req.name = name;
	ResWFDupaNume resp = (ResWFDupaNume) trimiteCerere(req);
	return resp.workflows;
    }

    public static Nod getNodDupaNume(final String workflowName,
	    final int workflowVersion, final String nodeName,
	    final Utilizator user) throws ExceptieWF {
	ReqNodDupaNume req = new ReqNodDupaNume();
	req.utilizator = user;
	req.workflowName = workflowName;
	req.version = workflowVersion;
	req.nodeName = nodeName;
	ResNodDupaNume resp = (ResNodDupaNume) trimiteCerere(req);
	return resp.node;
    }

    @SuppressWarnings("unchecked")
    public static List getNoduriProces(final Integer workflowId,
	    final Utilizator user) throws ExceptieWF {
	ReqNoduriProces req = new ReqNoduriProces();
	req.utilizator = user;
	req.workflowId = workflowId;
	ResNoduriProces resp = (ResNoduriProces) trimiteCerere(req);
	return resp.nodes;
    }

    public static Object getVariabla(final Integer workflowId,
	    final String variableName, final Utilizator user) throws ExceptieWF {

	ReqVariabila req = new ReqVariabila();
	req.workflowId = workflowId;
	req.variableName = variableName;
	req.utilizator = user;
	ResVariabila resp = (ResVariabila) trimiteCerere(req);
	return resp.variableValue;
    }

    @SuppressWarnings("unchecked")
    public static List getModeleWorkflow(final Utilizator user)
	    throws ExceptieWF {
	ReqModeleDisponibile req = new ReqModeleDisponibile();
	req.utilizator = user;
	ResModeleDisponibile resp = (ResModeleDisponibile) trimiteCerere(req);
	return resp.models;
    }

    public static StareWF getStareWorkflow(final Integer workflowId,
	    final Utilizator user) throws ExceptieWF {

	ReqStareWF req = new ReqStareWF();
	req.workflowId = workflowId;
	req.utilizator = user;

	ResStareWF resp = (ResStareWF) trimiteCerere(req);
	return resp.workflowState;
    }

    public static ResRepornireWF continuaWorkflow(final Integer workflowId,
	    final Utilizator user) throws ExceptieWF {

	ReqRepornireWF req = new ReqRepornireWF();
	req.workflowId = workflowId;
	req.utilizator = user;

	ResRepornireWF resp = (ResRepornireWF) trimiteCerere(req);
	return resp;
    }

    private static Raspuns trimiteCerere(final Cerere req) throws ExceptieWF {

	req.numeRaspuns = Util.generateUniqueStringId();
	try {
	    Raspuns resp = SynchQueueMessaging.sendRequest(req);
	    if (resp.codRaspuns != Raspuns.SUCCES) {
		System.out.println("EROARE response from server.");
		throw new ExceptieWF(resp.mesaj);
	    }
	    return resp;
	} catch (Exception t) {
	    throw new ExceptieWF(t);
	}
    }

    public static ResSetareVariabila setVariabla(final Integer workflowId,
	    final String variableName, final Object variableValue,
	    final Utilizator user) throws ExceptieWF {
	ReqSetareVariabila req = new ReqSetareVariabila();
	req.workflowId = workflowId;
	req.variableName = variableName;
	req.variableValue = variableValue;
	req.utilizator = user;

	ResSetareVariabila resp = (ResSetareVariabila) trimiteCerere(req);
	return resp;
    }

    public static Integer startWorkflow(final String workflowName,
	    final int workflowVersion, final ItemModel workItem,
	    final Utilizator user) throws ExceptieWF {

	ReqStartWF req = new ReqStartWF();
	req.workflowName = workflowName;
	req.version = workflowVersion;
	req.workItem = workItem;
	req.utilizator = user;

	ResStartWF resp = (ResStartWF) trimiteCerere(req);
	return resp.workflowId;
    }

    public static Integer startWorkflow(final String workflowName,
	    final ItemModel workItem, final Utilizator user) throws ExceptieWF {

	ReqStartWF req = new ReqStartWF();
	req.workflowName = workflowName;
	req.workItem = workItem;
	req.utilizator = user;

	ResStartWF resp = (ResStartWF) trimiteCerere(req);
	return resp.workflowId;
    }

    public static ResSuspendareWF suspendaWorkflow(final Integer workflowId,
	    final Utilizator user) throws ExceptieWF {

	ReqSuspendareWF req = new ReqSuspendareWF();
	req.workflowId = workflowId;
	req.utilizator = user;

	ResSuspendareWF resp = (ResSuspendareWF) trimiteCerere(req);
	return resp;
    }

}
