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
package xflow.util;

import org.apache.axis.MessageContext;
import org.apache.axis.Constants;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.XMLUtils;

import java.io.StringWriter;

public class XflowSoapEnvelope {

  static String XflowNS = "http://xenopsys.org/xflow";
  static String XflowPrefix = "XFLOW";

  public static void main(String[] args) throws Exception {
    try {
      SOAPEnvelope env = new SOAPEnvelope();

      env.addMapping(new Mapping(XflowNS, XflowPrefix));
      env.addAttribute(Constants.URI_SOAP11_ENV, "actor", "some-uri");
      env.addAttribute(Constants.URI_SOAP11_ENV, "mustUnderstand", "1");

      SOAPHeaderElement header =
          new SOAPHeaderElement(XMLUtils.StringToElement(XflowNS,
              "MyHeaderElement",
              ""));
      env.addHeader(header);

      SOAPBodyElement sbe = new SOAPBodyElement(XMLUtils.StringToElement(XflowNS, "MyMethod", "xxx"));
      env.addBodyElement(sbe);

      AxisClient tmpEngine = new AxisClient(new NullProvider());
      MessageContext msgContext = new MessageContext(tmpEngine);

      StringWriter writer = new StringWriter();
    //  SerializationContext serializeContext = new SerializationContext(writer, msgContext);
    //  env.output(serializeContext);
      writer.close();

      String s = writer.getBuffer().toString();
      System.out.println(s);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
