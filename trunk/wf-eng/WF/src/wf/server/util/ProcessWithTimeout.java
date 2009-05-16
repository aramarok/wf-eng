package wf.server.util;

public class ProcessWithTimeout {
    public String processName;
    public String timeoutHandler;
    public int timeoutMinutes;
    public int workflowId;

    @Override
    public String toString() {
	String s = "";
	s += "Integer: " + this.workflowId + " ProcessName: "
		+ this.processName + " Timeout: " + this.timeoutMinutes
		+ " Timeout Handler: " + this.timeoutHandler + "\n";
	return s;
    }
}
