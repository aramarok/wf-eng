package wf.server;

import junit.framework.TestSuite;


import wf.server.case1.Case1_Test;
import wf.server.case_and.CaseAndTest;
import wf.server.case_condition.CaseConditionTest;
import wf.server.case_or.CaseOrTest;


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
