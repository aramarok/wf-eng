package wf.exceptions;

public class ExceptieWF extends Exception {

    private static final long serialVersionUID = 1L;

    public ExceptieWF(String msg) {
	super(msg);
    }

    public ExceptieWF(Throwable cause) {
	super(cause);
    }

    public ExceptieWF(String message, Throwable cause) {
	super(message, cause);
    }
}
