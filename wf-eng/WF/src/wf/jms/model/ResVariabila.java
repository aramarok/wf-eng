package wf.jms.model;

public class ResVariabila extends Raspuns {

    private static final long serialVersionUID = 1L;

    public Object variableValue;

    public ResVariabila(final int cod, final Object obiect) {
	super(cod);
	this.variableValue = obiect;
    }

    public ResVariabila(final int cod, final String mesaj,
	    final Object obiect) {
	super(cod, mesaj);
	this.variableValue = obiect;
    }
}
