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
import org.apache.log4j.Logger;

public class SoapEnvelope {

	private static Logger log = Logger.getLogger(SoapEnvelope.class);
	
	static String nameSpace = "http://example.com/uvtwf";
	static String wfPrefix = "WF";

	public static void main(String[] args) throws Exception {
		try {
			SOAPEnvelope env = new SOAPEnvelope();

			env.addMapping(new Mapping(nameSpace, wfPrefix));
			env.addAttribute(Constants.URI_SOAP11_ENV, "actor", "some-uri");
			env.addAttribute(Constants.URI_SOAP11_ENV, "mustUnderstand", "1");

			SOAPHeaderElement header = new SOAPHeaderElement(XMLUtils
					.StringToElement(nameSpace, "MyHeaderElement", ""));
			env.addHeader(header);

			SOAPBodyElement sbelem = new SOAPBodyElement(XMLUtils.StringToElement(
					nameSpace, "MyMethod", "xxx"));
			env.addBodyElement(sbelem);

			AxisClient tmpEngine = new AxisClient(new NullProvider());
			MessageContext msgContext = new MessageContext(tmpEngine);
			log.info(msgContext.toString());
			
			StringWriter writer = new StringWriter();
			writer.close();

			String s = writer.getBuffer().toString();
			log.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
