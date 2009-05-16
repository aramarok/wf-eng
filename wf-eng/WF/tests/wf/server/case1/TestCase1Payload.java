package wf.server.case1;

import java.io.Serializable;

public class TestCase1Payload implements Serializable {

    private static final long serialVersionUID = 1L;

    String descr;
    int id;
    String name;
    double value;

    public String getDescr() {
	return this.descr;
    }

    public int getId() {
	return this.id;
    }

    public String getName() {
	return this.name;
    }

    public double getValue() {
	return this.value;
    }

    public void setDescr(final String descr) {
	this.descr = descr;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public void setValue(final double value) {
	this.value = value;
    }

}
