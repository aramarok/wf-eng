
package wf.exceptions;

public class WorkFlowException extends Exception {

  
  public WorkFlowException (String msg) { super (msg); }

  public WorkFlowException(Throwable cause) {
    super(cause);
  }

  public WorkFlowException(String message, Throwable cause) {
    super(message, cause);
  }
}
