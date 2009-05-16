package wf.server.controller;

import wf.jms.model.ReqAbortWF;
import wf.jms.model.ReqCompleteWI;
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
import wf.jms.model.ReqUrmWI;
import wf.jms.model.ReqValidareProces;
import wf.jms.model.ReqVariabila;
import wf.jms.model.ReqWFActive;
import wf.jms.model.ReqWFDupaNume;
import wf.jms.model.ReqWI;
import wf.jms.model.ReqWIs;
import wf.jms.model.ResAbortWF;
import wf.jms.model.ResCompleteWI;
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
import wf.jms.model.ResUrmWI;
import wf.jms.model.ResValidareProces;
import wf.jms.model.ResVariabila;
import wf.jms.model.ResWFActive;
import wf.jms.model.ResWFDupaNume;
import wf.jms.model.ResWI;
import wf.jms.model.ResWIs;

public class WorkflowEngineWrapper {

    WorkflowEngine workflowEngine;

    public WorkflowEngineWrapper() {
    }

    public WorkflowEngineWrapper(final WorkflowEngine workflowEngine) {
	this.workflowEngine = workflowEngine;
    }

    public WorkflowEngine getWorkflowEngine() {
	return this.workflowEngine;
    }

    public ResAbortWF handleAbortWorkflowRequest(final ReqAbortWF request) {
	return (ResAbortWF) this.workflowEngine.handle(request);
    }

    public ResCompleteWI handleCompleteWorkItemRequest(
	    final ReqCompleteWI request) {
	return (ResCompleteWI) this.workflowEngine.handle(request);
    }

    public ResDeployModel handleDeployModelRequest(final ReqDeployModel request) {
	return (ResDeployModel) this.workflowEngine.handle(request);
    }

    public ResWFActive handleGetActiveWorkflowsRequest(final ReqWFActive request) {
	return (ResWFActive) this.workflowEngine.handle(request);
    }

    public ResToateWF handleGetAllWorkflowsRequest(final ReqToateWF request) {
	return (ResToateWF) this.workflowEngine.handle(request);
    }

    public ResModeleDisponibile handleGetModelsRequest(
	    final ReqModeleDisponibile request) {
	return (ResModeleDisponibile) this.workflowEngine.handle(request);
    }

    public ResUrmWI handleGetNextWorkItemRequest(final ReqUrmWI request) {
	return (ResUrmWI) this.workflowEngine.handle(request);
    }

    public ResNodDupaNume handleGetNodeByNameRequest(
	    final ReqNodDupaNume request) {
	return (ResNodDupaNume) this.workflowEngine.handle(request);
    }

    public ResNoduriProces handleGetProcessNodesRequest(
	    final ReqNoduriProces request) {
	return (ResNoduriProces) this.workflowEngine.handle(request);
    }

    public ResVariabila handleGetVariableRequest(final ReqVariabila request) {
	return (ResVariabila) this.workflowEngine.handle(request);
    }

    public ResWFDupaNume handleGetWorkflowsByNameRequest(
	    final ReqWFDupaNume request) {
	return (ResWFDupaNume) this.workflowEngine.handle(request);
    }

    public ResStareWF handleGetWorkflowStateRequest(final ReqStareWF request) {
	return (ResStareWF) this.workflowEngine.handle(request);
    }

    public ResWI handleGetWorkItemRequest(final ReqWI request) {
	return (ResWI) this.workflowEngine.handle(request);
    }

    public ResWIs handleGetWorkItemsRequest(final ReqWIs request) {
	return (ResWIs) this.workflowEngine.handle(request);
    }

    public ResRepornireWF handleResumeWorkflowRequest(
	    final ReqRepornireWF request) {
	return (ResRepornireWF) this.workflowEngine.handle(request);
    }

    public ResSetareVariabila handleSetVariableRequest(
	    final ReqSetareVariabila request) {
	return (ResSetareVariabila) this.workflowEngine.handle(request);
    }

    public ResStartWF handleStartWorkflowRequest(final ReqStartWF request) {
	return (ResStartWF) this.workflowEngine.handle(request);
    }

    public ResSuspendareWF handleSuspendWorkflowRequest(
	    final ReqSuspendareWF request) {
	return (ResSuspendareWF) this.workflowEngine.handle(request);
    }

    public ResValidareProces handleValidateProcessRequest(
	    final ReqValidareProces request) {
	return (ResValidareProces) this.workflowEngine.handle(request);
    }

    public void setWorkflowEngine(final WorkflowEngine workflowEngine) {
	this.workflowEngine = workflowEngine;
    }

}
