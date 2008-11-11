package xflow.server;

import xflow.server.controller.WorkflowEngine;

/**
 * User: kosta
 * Date: Jul 11, 2004
 * Time: 7:04:42 PM
 */
public class TestHelper {

  private static  WorkflowEngine workflowEngine;

  public static synchronized WorkflowEngine getWorkflowEngine() {
    if( workflowEngine == null ){
      workflowEngine = new WorkflowEngine();
    }
    return workflowEngine;
  }

}
