
package wf.server.util;


public class ProcessWithTimeout {
    public int    workflowId;
    public String processName;
    public int    timeoutMinutes;
    public String timeoutHandler;

    public String toString() {
        String s = "";
        s += "Integer: " + workflowId +
             " ProcessName: " + processName +
             " Timeout: " + timeoutMinutes +
             " Timeout Handler: " + timeoutHandler + "\n";
        return s;
    }
}
