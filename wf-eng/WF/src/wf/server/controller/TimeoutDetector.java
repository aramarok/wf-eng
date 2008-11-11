/*
* ====================================================================
*
* XFLOW - Process Management System
* Copyright (C) 2003 Rob Tan
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions, and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions, and the disclaimer that follows
*    these conditions in the documentation and/or other materials
*    provided with the distribution.
*
* 3. The name "XFlow" must not be used to endorse or promote products
*    derived from this software without prior written permission.  For
*    written permission, please contact rcktan@yahoo.com
*
* 4. Products derived from this software may not be called "XFlow", nor
*    may "XFlow" appear in their name, without prior written permission
*    from the XFlow Project Management (rcktan@yahoo.com)
*
* In addition, we request (but do not require) that you include in the
* end-user documentation provided with the redistribution and/or in the
* software itself an acknowledgement equivalent to the following:
*     "This product includes software developed by the
*      XFlow Project (http://xflow.sourceforge.net/)."
* Alternatively, the acknowledgment may be graphical using the logos
* available at http://xflow.sourceforge.net/
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
*
* ====================================================================
* This software consists of voluntary contributions made by many
* individuals on behalf of the XFlow Project and was originally
* created by Rob Tan (rcktan@yahoo.com)
* For more information on the XFlow Project, please see:
*           <http://xflow.sourceforge.net/>.
* ====================================================================
*/

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
    // Get start times of items sitting in inboxes that qualify for timeout checks
    try {
      Date stime = Persistence.getInboxP().getTimeStarted (pto.workflowId, pto.processName);
      if (stime == null) {
        return;
      }
      long startTimeMillis = stime.getTime();
      long currentTimeMillis = System.currentTimeMillis();
      long diffTimeMillis = currentTimeMillis - startTimeMillis;
      int  diffTimeMins = (int)diffTimeMillis/60000;
      //System.out.println ("Diff time is: " + diffTimeMins + " mins.");
      if (diffTimeMins > pto.timeoutMinutes) {
        log.info ("Processing timed out: " + pto.workflowId + " " + pto.processName);

        // Update the inbox item with the timeout flag
        Persistence.getInboxP().setTimeoutFlag (pto.workflowId, pto.processName);

        // Start the timeout handler workflow
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
