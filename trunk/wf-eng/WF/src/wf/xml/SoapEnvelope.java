package wf.xml;

import java.io.StringWriter;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.XMLUtils;

public class SoapEnvelope {

	static String nameSpace = "http://example.com/uvtwf";
	static String XflowPrefix = "XFLOW";

	public static void main(String[] args) throws Exception {
		try {
			SOAPEnvelope env = new SOAPEnvelope();

			env.addMapping(new Mapping(nameSpace, XflowPrefix));
			env.addAttribute(Constants.URI_SOAP11_ENV, "actor", "some-uri");
			env.addAttribute(Constants.URI_SOAP11_ENV, "mustUnderstand", "1");

			SOAPHeaderElement header = new SOAPHeaderElement(XMLUtils
					.StringToElement(nameSpace, "MyHeaderElement", ""));
			env.addHeader(header);

			SOAPBodyElement sbe = new SOAPBodyElement(XMLUtils.StringToElement(
					nameSpace, "MyMethod", "xxx"));
			env.addBodyElement(sbe);

			AxisClient tmpEngine = new AxisClient(new NullProvider());
			MessageContext msgContext = new MessageContext(tmpEngine);

			StringWriter writer = new StringWriter();
			writer.close();

			String s = writer.getBuffer().toString();
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
