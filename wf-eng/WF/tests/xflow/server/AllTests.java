package xflow.server;

import junit.framework.TestSuite;


import xflow.server.case_or.CaseOrTest;
import xflow.server.case_and.CaseAndTest;
import xflow.server.case_condition.CaseConditionTest;
import xflow.server.case1.Case1_Test;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 2:13:50 PM
 */
public class AllTests extends TestSuite{

  static public junit.framework.Test suite() {
    junit.framework.TestSuite newSuite = new junit.framework.TestSuite();
    newSuite.addTest( Case1_Test.suite() );
    newSuite.addTest( CaseAndTest.suite() );
    newSuite.addTest( CaseOrTest.suite() );
    newSuite.addTest( CaseConditionTest.suite() );
    return newSuite;
  };


}
