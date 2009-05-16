package wf.model;

import java.io.Serializable;

public class ModelWF implements Serializable {

    private static final long serialVersionUID = 1L;

    public String description;
    public String name;
    public int version;

    public ModelWF() {
    }

    public String getDescription() {
	return this.description;
    }

    public String getName() {
	return this.name;
    }

    public int getVersion() {
	return this.version;
    }

    public int getWorkflowVersion() {
	return this.version;
    }

    public void setDescription(final String n) {
	this.description = n;
    }

    public void setName(final String n) {
	this.name = n;
    }

    public void setVersion(final int version) {
	this.version = version;
    }

    public void setWorkflowVersion(final int v) {
	this.version = v;
    }

}
