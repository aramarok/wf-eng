


package wf.test;

import wf.exceptions.XflowException;
import wf.jms.EventsPublisher;
import wf.model.WorkItem;

public class TestEventPublisher {

  public static void main (String[] args) {

    EventsPublisher ep = new EventsPublisher();
    Integer wid = new Integer(999);
    WorkItem wi = new WorkItem(wid);
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
