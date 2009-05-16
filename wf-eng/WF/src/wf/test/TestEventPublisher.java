package wf.test;

import wf.exceptions.ExceptieWF;
import wf.jms.PublisherEvenimente;
import wf.model.ItemModel;

public class TestEventPublisher {

	public static void main(String[] args) {

		PublisherEvenimente ep = new PublisherEvenimente();
		Integer wid = new Integer(999);
		ItemModel wi = new ItemModel(wid);
		wi.setProperty("PropA", "xxxxx");
		wi.setProperty("PropB", new Integer(9898));
		wi.setPayload(new Integer(1111));

		System.out.println("Publishing Model Deployed Event");
		try {
			ep.publishModelDeployedEvent("CreditApproval", 1, "utilizator");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing WorkflowStarted Event");
		try {
			ep.publishWorkflowStartedEvent("CreditApproval", -1, new Integer(
					123), new Integer(-1), "utilizator", wi);
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Variable Updated Event");
		try {
			ep.publishVariableUpdatedEvent("CreditApproval", 1, new Integer(0),
					"score", new Integer(100));
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Workflow Suspended Event");
		try {
			ep.publishWorkflowSuspendedEvent("CreditApproval", -1, new Integer(
					123), "utilizator");
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Workflow Resumed Event");
		try {
			ep.publishWorkflowResumedEvent("CreditApproval", -1, new Integer(
					123), "utilizator");
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Workflow Aborted Event");
		try {
			ep.publishWorkflowAbortedEvent("CreditApproval", -1, new Integer(
					123), "utilizator");
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Workflow Completed Event");
		try {
			ep.publishWorkflowCompletedEvent("CreditApproval", -1, new Integer(
					123), "utilizator");
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Nod Transition Event");
		wi.setPayload(new Integer(999));
		try {
			ep.publishNodeTransitionEvent("CreditApproval", -1,
					new Integer(123), "START", "Node1", wi);
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Publishing Proces Timeout Event");
		try {
			ep.publishProcessTimeoutEvent("CreditApproval", -1, 123, "P1");
		} catch (ExceptieWF e) {
			System.out.println(e.getMessage());
		}
	}
}
