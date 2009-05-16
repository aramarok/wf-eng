package wf.server;

import wf.server.controller.WorkflowEngine;

public class TestHelper {

    private static WorkflowEngine workflowEngine;

    public static synchronized WorkflowEngine getWorkflowEngine() {
	if (workflowEngine == null) {
	    workflowEngine = new WorkflowEngine();
	}
	return workflowEngine;
    }

}
