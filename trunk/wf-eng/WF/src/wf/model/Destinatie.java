package wf.model;

import java.io.Serializable;

public class Destinatie implements Serializable {

    private static final long serialVersionUID = 1L;

    public Nod node;
    public String rule;

    public Destinatie(final Nod n, final String r) {
	this.node = n;
	this.rule = r;
    }

    public Nod getNode() {
	return this.node;
    }

    public String getRule() {
	return this.rule;
    }

    public void setNode(final Nod n) {
	this.node = n;
    }

    public void setRule(final String rule) {
	this.rule = rule;
    }
}
