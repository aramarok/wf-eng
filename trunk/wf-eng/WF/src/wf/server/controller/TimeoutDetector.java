

package wf.server.controller;

import org.apache.log4j.Logger;

import wf.db.Persistence;
import wf.model.WorkItem;
import wf.server.util.ProcessWithTimeout;

import java.util.List;
import java.util.Date;

public class TimeoutDetector extends Thread {
  private static Logger log = Logger.getLogger(TimeoutDetector.class);

  public void run () {
    while (true) {
      try {
        List v = Persistence.getWorkflowP().getProcessesWithTimeouts();
        for (int i = 0; i < v.size(); i++) {
          ProcessWithTimeout pto = (ProcessWithTimeout) v.get(i);
          processTimeout (pto);
        }
        Thread.sleep (60000);
      } catch (Exception e) {
        log.error( "Ignorable exception", e );
      }
    }
  }

  public void processTimeout (ProcessWithTimeout pto) {
    try {
      Date stime = Persistence.getInboxP().getTimeStarted (pto.workflowId, pto.processName);
      if (stime == null) {
        return;
      }
      long startTimeMillis = stime.getTime();
      long currentTimeMillis = System.currentTimeMillis();
      long diffTimeMillis = currentTimeMillis - startTimeMillis;
      int  diffTimeMins = (int)diffTimeMillis/60000;
      if (diffTimeMins > pto.timeoutMinutes) {
        log.info ("Processing timed out: " + pto.workflowId + " " + pto.processName);
        Persistence.getInboxP().setTimeoutFlag (pto.workflowId, pto.processName);
        if (pto.timeoutHandler != null && !pto.timeoutHandler.equals("")) {
          WorkItem witem = new WorkItem();
          witem.setPayload("Timeout");
          witem.setProperty ("Integer", new Integer(pto.workflowId));
          witem.setProperty ("Process", pto.processName);
          WorkflowProcessor.getInstance().startWorkflow (pto.timeoutHandler, -1, witem, "System");
        }
      }
    } catch (Exception e) {
      log.info( "processTimeout", e );
    }
  }

}
