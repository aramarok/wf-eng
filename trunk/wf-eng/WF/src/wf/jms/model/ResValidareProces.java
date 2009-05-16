package wf.jms.model;

public class ResValidareProces extends Raspuns {

    private static final long serialVersionUID = 1L;

    public boolean ok;

    public ResValidareProces(final int cod, final boolean b) {
	super(cod);
	this.ok = b;
    }

    public ResValidareProces(final int cod, final String mesaj,
	    final boolean b) {
	super(cod, mesaj);
	this.ok = b;
    }

}
