

package wf.server.controller;

import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

public class ExpressionEval {

    private static Logger log = Logger.getLogger(ExpressionEval.class);

    public boolean evaluateRule (Object object, String rule){
        boolean result = true;
        
        log.info ("Evaluating rule on Java object: " + rule);
        StringTokenizer strTok = new StringTokenizer (rule, " ");
        String lhsTok = null;
        String opTok = null;
        String litTok = null;
        if (strTok.hasMoreTokens()) {
            lhsTok = strTok.nextToken();
	
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
        if (lhsTok == null || opTok == null || litTok == null) {
            log.error ("No rule or malformed rule");
            return false;
        }
        try {
            Class workflowObjectClass = object.getClass();
            Method method = workflowObjectClass.getMethod(lhsTok,
                                                         new Class[] {} );
            Object resultObj = method.invoke(object, new Object[] {} );
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
