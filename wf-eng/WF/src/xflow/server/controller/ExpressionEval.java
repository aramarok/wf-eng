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

package xflow.server.controller;

import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

public class ExpressionEval {

    private static Logger log = Logger.getLogger(ExpressionEval.class);

    public boolean evaluateRule (Object object, String rule){
        boolean result = true;
        
        log.info ("Evaluating rule on Java object: " + rule);

        // Break the rule string into components:
        //    LHS class, LHS method, operator, literal (TBD RHS class & method)
        StringTokenizer strTok = new StringTokenizer (rule, " ");
        String lhsTok = null;
        String opTok = null;
        String litTok = null;
        if (strTok.hasMoreTokens()) {
            lhsTok = strTok.nextToken();
            //if (lhsTok.startsWith ("get") == false) {
            //    lhsTok = "get" + lhsTok;
	    //}
        }
        if (strTok.hasMoreTokens()) {
            opTok = strTok.nextToken();
        }
        if (strTok.hasMoreTokens()) {
            litTok = strTok.nextToken();
            if (litTok.startsWith ("'")) {
                StringTokenizer tk2 = new StringTokenizer (rule, "'");
                String dontCare = tk2.nextToken();
                litTok = tk2.nextToken();
            }
        }
       
        // Must have a well-formed rule string
        if (lhsTok == null || opTok == null || litTok == null) {
            log.error ("No rule or malformed rule");
            return false;
        }

        // Reflect on workflow object
        try {
            Class workflowObjectClass = object.getClass();

            // Get the method and return type for workflow object 
            // System.out.println ("Invoking " + lhsMethodTok + " on " +
            //                     lhsClassTok);
            Method method = workflowObjectClass.getMethod(lhsTok,
                                                         new Class[] {} );
            Object resultObj = method.invoke(object, new Object[] {} );
            //System.out.println ("Result is " + resultObj);
            result = applyRule (resultObj, opTok, litTok);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean applyRule (Object o, String op, String lit) {
        Class oClass = o.getClass();
        String className = oClass.getName();
        boolean result = false;

        //System.out.println ("className = " + className);
        log.info ("Applying rule: " + o + " " + op + " " + lit);

        if (className.equals("java.lang.Integer")) {
            Integer iobj = (Integer)o;
            int lhsVal = iobj.intValue();
            iobj = new Integer(lit);
            int rhsVal = iobj.intValue();
            if (op.equals("==")) {
               result = (lhsVal == rhsVal);
            } else if (op.equals("!=")) {
               result = (lhsVal != rhsVal);
            } else if (op.equals(">=")) {
               result = (lhsVal >= rhsVal);
            } else if (op.equals("<=")) {
               result = (lhsVal <= rhsVal);
            } else if (op.equals(">")) {
               result = (lhsVal > rhsVal);
            } else if (op.equals("<")) {
               result = (lhsVal < rhsVal);
            }
        } else if (className.equals("java.lang.Float")) {
            Float iobj = (Float)o;
            float lhsVal = iobj.floatValue();
            iobj = new Float(lit);
            float rhsVal = iobj.floatValue();
            if (op.equals("==")) {
               result = (lhsVal == rhsVal);
            } else if (op.equals("!=")) {
               result = (lhsVal != rhsVal);
            } else if (op.equals(">=")) {
               result = (lhsVal >= rhsVal);
            } else if (op.equals("<=")) {
               result = (lhsVal <= rhsVal);
            } else if (op.equals(">")) {
               result = (lhsVal > rhsVal);
            } else if (op.equals("<")) {
               result = (lhsVal < rhsVal);
            } 
        } else if (className.equals("java.lang.Double")) {
            Double iobj = (Double)o;
            double lhsVal = iobj.doubleValue();
            iobj = new Double(lit);
            double rhsVal = iobj.doubleValue();
            if (op.equals("==")) {
               result = (lhsVal == rhsVal);
            } else if (op.equals("!=")) {
               result = (lhsVal != rhsVal);
            } else if (op.equals(">=")) {
               result = (lhsVal >= rhsVal);
            } else if (op.equals("<=")) {
               result = (lhsVal <= rhsVal);
            } else if (op.equals(">")) {
               result = (lhsVal > rhsVal);
            } else if (op.equals("<")) {
               result = (lhsVal < rhsVal);
            }
        } else if (className.equals("java.lang.Boolean")) {
            Boolean iobj = (Boolean)o;
            boolean lhsVal = iobj.booleanValue();
            iobj = new Boolean(lit);
            boolean rhsVal = iobj.booleanValue();
            if (op.equals("==")) {
               result = (lhsVal == rhsVal);
            } else if (op.equals("!=")) {
               result = (lhsVal != rhsVal);
            } 
        } else if (className.equals("java.lang.String")) {
            String lhsVal = (String)o;
            String rhsVal = lit;
            if (op.equals("==")) {
               result = (lhsVal.equals(rhsVal));
            } else if (op.equals("!=")) {
               result = (!lhsVal.equals(rhsVal));
            } 
        }

        log.info ("apply Rule returning: " + result);
        return result;
    }
}
