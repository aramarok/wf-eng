package wf.jms.model;

public class ResDeployModel extends Raspuns {

    private static final long serialVersionUID = 1L;

    public ResDeployModel(final int statusCode) {
	super(statusCode);
    }

    public ResDeployModel(final int cod, final String mesaj) {
	super(cod, mesaj);
    }
}
