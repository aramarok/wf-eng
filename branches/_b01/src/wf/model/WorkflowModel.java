package wf.model;

import java.io.Serializable;

public class WorkflowModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String name;
	public int version;
	public String description;

	public WorkflowModel() {
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String n) {
		description = n;
	}

	public String getDescription() {
		return description;
	}

	public void setWorkflowVersion(int v) {
		version = v;
	}

	public int getWorkflowVersion() {
		return version;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
