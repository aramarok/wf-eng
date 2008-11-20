package wf.test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import wf.client.auth.User;
import wf.model.WorkItem;

public class StartWorkflowWS {
	public static void main(String[] args) throws Exception {

		String endpoint = "http://localhost:8080/axis/services/WfService";
		String method = "startWorkflow";
		Service service = new Service();
		Call call = (Call) service.createCall();

		QName qn = new QName("urn:WfService", "Integer");

		call.registerTypeMapping(Integer.class, qn, new BeanSerializerFactory(
				Integer.class, qn), new BeanDeserializerFactory(Integer.class,
				qn));

		qn = new QName("urn:WfService", "Integer");
		call.registerTypeMapping(Integer.class, qn, new BeanSerializerFactory(
				Integer.class, qn), new BeanDeserializerFactory(Integer.class,
				qn));

		QName qn1 = new QName("urn:WfService", "WorkItem");
		call.registerTypeMapping(WorkItem.class, qn1,
				new BeanSerializerFactory(WorkItem.class, qn1),
				new BeanDeserializerFactory(WorkItem.class, qn1));

		QName qn2 = new QName("urn:WfService", "User");
		call.registerTypeMapping(User.class, qn2, new BeanSerializerFactory(
				User.class, qn2), new BeanDeserializerFactory(User.class, qn2));

		call.setTargetEndpointAddress(new java.net.URL(endpoint));
		call.setOperationName(method);
		call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
		call.addParameter("op2", XMLType.XSD_INT, ParameterMode.IN);
		call.addParameter("op3", qn1, ParameterMode.IN);
		call.addParameter("op4", qn2, ParameterMode.IN);

		String workflowName = args[0];
		Integer version = new Integer(-1);
		WorkItem witem = new WorkItem();
		witem.setPayload(new Integer(1));
		User user = new User("user", "password");
		call.invoke(new Object[] { workflowName, version, witem, user });

		System.out.println("Workflow started.");
	}
}
