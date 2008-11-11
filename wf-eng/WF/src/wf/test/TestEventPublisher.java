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


package wf.test;

import wf.exceptions.XflowException;
import wf.jms.EventsPublisher;
import wf.model.WorkItem;

public class TestEventPublisher {

  public static void main (String[] args) {

    EventsPublisher ep = new EventsPublisher();
    Integer wid = new Integer(999);
    WorkItem wi = new WorkItem(wid);
    //	wi.setPayloadType("XML");
    wi.setProperty("PropA", "xxxxx");
    wi.setProperty("PropB", new Integer(9898));
    wi.setPayload(new Integer(1111));

    System.out.println ("Publishing Model Deployed Event");
    try {
      ep.publishModelDeployedEvent(
          "CreditApproval",
          1,
          "rtan");
    } catch (Exception e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing WorkflowStarted Event");
    try {
      ep.publishWorkflowStartedEvent(
          "CreditApproval",
          -1,
          new Integer( 123),
          new Integer(-1),
          "rtan",
          wi);
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing Variable Updated Event");
    try {
      ep.publishVariableUpdatedEvent(
          "CreditApproval",
          1,
          new Integer(0),
          "score",
          new Integer(100));
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }


    System.out.println ("Publishing Workflow Suspended Event");
    try {
      ep.publishWorkflowSuspendedEvent(
          "CreditApproval",
          -1,
          new Integer(123),
          "rtan");
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }


    System.out.println ("Publishing Workflow Resumed Event");
    try {
      ep.publishWorkflowResumedEvent(
          "CreditApproval",
          -1,
          new Integer(123),
          "rtan");
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing Workflow Aborted Event");
    try {
      ep.publishWorkflowAbortedEvent(
          "CreditApproval",
          -1,
          new Integer(123),
          "rtan");
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing Workflow Completed Event");
    try {
      ep.publishWorkflowCompletedEvent(
          "CreditApproval",
          -1,
          new Integer(123),
          "rtan");
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing Node Transition Event");
    wi.setPayload(new Integer(999));
    try {
      ep.publishNodeTransitionEvent(
          "CreditApproval",
          -1,
         new Integer( 123),
          "START",
          "Node1",
          wi);
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }

    System.out.println ("Publishing Proces Timeout Event");
    try {
      ep.publishProcessTimeoutEvent(
          "CreditApproval",
          -1,
          123,
          "P1");
    } catch (XflowException e) {
      System.out.println (e.getMessage());
    }
  }
}
