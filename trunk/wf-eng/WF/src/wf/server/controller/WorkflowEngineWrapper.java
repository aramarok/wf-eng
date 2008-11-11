package wf.server.controller;

import wf.jms.model.*;

/**
 * Silly wrapper that makes API more clear and simplifies SOAP connectivity
 *
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 3:33:39 PM
 */
public class WorkflowEngineWrapper {

  WorkflowEngine workflowEngine;

  public WorkflowEngineWrapper() {
  }

  public WorkflowEngineWrapper(WorkflowEngine workflowEngine) {
    this.workflowEngine = workflowEngine;
  }

  public WorkflowEngine getWorkflowEngine() {
    return workflowEngine;
  }

  public void setWorkflowEngine(WorkflowEngine workflowEngine) {
    this.workflowEngine = workflowEngine;
  }

  public  AbortWorkflowResponse handleAbortWorkflowRequest( AbortWorkflowRequest request ){
    return (AbortWorkflowResponse) workflowEngine.handle( request );
  }


  public  GetProcessNodesResponse handleGetProcessNodesRequest( GetProcessNodesRequest request ){
    return (GetProcessNodesResponse) workflowEngine.handle( request );
  }


  public  ResumeWorkflowResponse handleResumeWorkflowRequest( ResumeWorkflowRequest request ){
    return (ResumeWorkflowResponse) workflowEngine.handle( request );
  }


  public  CompleteWorkItemResponse handleCompleteWorkItemRequest( CompleteWorkItemRequest request ){
    return (CompleteWorkItemResponse) workflowEngine.handle( request );
  }


  public  GetVariableResponse handleGetVariableRequest( GetVariableRequest request ){
    return (GetVariableResponse) workflowEngine.handle( request );
  }


  public  GetNextWorkItemResponse handleGetNextWorkItemRequest( GetNextWorkItemRequest request ){
    return (GetNextWorkItemResponse) workflowEngine.handle( request );
  }


  public  GetWorkflowStateResponse handleGetWorkflowStateRequest( GetWorkflowStateRequest request ){
    return (GetWorkflowStateResponse) workflowEngine.handle( request );
  }


  public  SuspendWorkflowResponse handleSuspendWorkflowRequest( SuspendWorkflowRequest request ){
    return (SuspendWorkflowResponse) workflowEngine.handle( request );
  }


  public  GetWorkflowsByNameResponse handleGetWorkflowsByNameRequest( GetWorkflowsByNameRequest request ){
    return (GetWorkflowsByNameResponse) workflowEngine.handle( request );
  }


  public  GetActiveWorkflowsResponse handleGetActiveWorkflowsRequest( GetActiveWorkflowsRequest request ){
    return (GetActiveWorkflowsResponse) workflowEngine.handle( request );
  }


  public  GetAllWorkflowsResponse handleGetAllWorkflowsRequest( GetAllWorkflowsRequest request ){
    return (GetAllWorkflowsResponse) workflowEngine.handle( request );
  }


  public  DeployModelResponse handleDeployModelRequest( DeployModelRequest request ){
    return (DeployModelResponse) workflowEngine.handle( request );
  }


  public  GetNodeByNameResponse handleGetNodeByNameRequest( GetNodeByNameRequest request ){
    return (GetNodeByNameResponse) workflowEngine.handle( request );
  }


  public  StartWorkflowResponse handleStartWorkflowRequest( StartWorkflowRequest request ){
    return (StartWorkflowResponse) workflowEngine.handle( request );
  }


  public  SetVariableResponse handleSetVariableRequest( SetVariableRequest request ){
    return (SetVariableResponse) workflowEngine.handle( request );
  }


  public  GetWorkItemResponse handleGetWorkItemRequest( GetWorkItemRequest request ){
    return (GetWorkItemResponse) workflowEngine.handle( request );
  }


  public  ValidateProcessResponse handleValidateProcessRequest( ValidateProcessRequest request ){
    return (ValidateProcessResponse) workflowEngine.handle( request );
  }


  public  GetModelsResponse handleGetModelsRequest( GetModelsRequest request ){
    return (GetModelsResponse) workflowEngine.handle( request );
  }


  public  GetWorkItemsResponse handleGetWorkItemsRequest( GetWorkItemsRequest request ){
    return (GetWorkItemsResponse) workflowEngine.handle( request );
  }


}
