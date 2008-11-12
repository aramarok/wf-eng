
package wf.exceptions;

public class XflowException extends Exception {

  
  public XflowException (String msg) { super (msg); }

  public XflowException(Throwable cause) {
    super(cause);
  }

  public XflowException(String message, Throwable cause) {
    super(message, cause);
  }
}
