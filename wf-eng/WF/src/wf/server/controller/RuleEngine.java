package wf.server.controller;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.xml.sax.SAXException;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;

public class RuleEngine {

	private static Logger log = Logger.getLogger(RuleEngine.class);

	private static String clean(String exp) {
		Pattern p = Pattern.compile("\\[ *");
		Matcher m = p.matcher(exp);
		String r = m.replaceAll("\\[");
		p = Pattern.compile(" *\\]");
		m = p.matcher(r);
		r = m.replaceAll("\\]");
		return r;
	}

	public static boolean evaluate(ItemModel witem, String r)
			throws JaxenException, IOException, ParserConfigurationException,
			ExceptieWF, SAXException {
		boolean result = true;
		String rule = clean(r);
		rule = rule.substring(1);
		StringTokenizer strtok = new StringTokenizer(rule, "]");
		rule = strtok.nextToken();

		strtok = new StringTokenizer(rule, " ");
		String tok = strtok.nextToken();

		StringTokenizer strtok2 = new StringTokenizer(tok, ".");
		tok = strtok2.nextToken();

		if (tok != null && tok.equals("property")) {
			String propName = strtok2.nextToken();
			String oper = strtok.nextToken();
			String propValue = strtok.nextToken();
			log.info("propName = " + propName);
			log.info("oper     = " + oper);
			log.info("propValue = " + propValue);
			Object prop = witem.getProperty(propName);
			if (prop == null) {
				throw new ExceptieWF("Property does not exist: "
						+ propName);
			}
			result = new ExpressionEval().applyRule(prop, oper, propValue);

		} else {
			String payloadType = witem.getPayloadType();
			if (payloadType == null) {
				throw new ExceptieWF(
						"Payload type not defined in work item");
			}
			if (payloadType.equals(ItemModel.XML)) {
				result = evaluateRuleOnXmlPayload((String) witem.getPayload(),
						r);
			} else if (payloadType.equals(ItemModel.JAVA_OBJECT)) {
				result = new ExpressionEval().evaluateRule(witem.getPayload(),
						rule);
			}
		}
		return result;
	}

	public static boolean evaluateRuleOnXmlPayload(String xml, String rule)
			throws JaxenException, IOException, ParserConfigurationException,
			SAXException {
		return XPathRuleEngine.executeRule(xml, rule, null);
	}

}
