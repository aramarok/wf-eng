

package wf.jms.model;


public class SetVariableRequest extends Request {

  public Integer workflowId;
  public String variableName;
  public Object variableValue;

  public SetVariableRequest() {
  }

  public SetVariableRequest(Integer workflowId, String variableName, Object variableValue) {
    this.workflowId = workflowId;
    this.variableName = variableName;
    this.variableValue = variableValue;
  }


}

