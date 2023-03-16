/**
 * TODO Copyright Notice
 */
package com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.res;
import com.oracle.activation.eric.mobile.x2_0.sub_mobile_data.res.generated.*;

import com.mslv.studio.activation.implementation.IExitType;
import com.mslv.studio.activation.implementation.test.TestCaseExitType;
import com.mslv.studio.activation.implementation.test.TestCaseUserExitType;


/*
 * This testcase will execute a test against each .testdata file located in the test folder 
 * under this package.
 *
 * The test applies the rules defined in the related .testinfo file (if present).  If not present,
 * a check for successful completion and exit type will be performed.
 * 
 * Each .testdata file should contain entries, in property file format, for each parameter in the 
 * Atomic Action using the Atomic Action labels.  Indexed parameters should be represented as their
 * decomposed data structures and be populated like their related Scalar and Compound formats. 
 * 
 * To add additional custom tests for more complex unit testing, create a pulbic void no argument 
 * method beginning with 'test'.  Follow standard JUNIT assertions to indicate failure cases.
 * See the commented example below for a basic template.
 * 
 */
public class ResSub_mobile_dataTestCase extends BaseResSub_mobile_dataTestCase {

	public ResSub_mobile_dataTestCase(String name) {
		super(name);
	}

	/*
	 * This method is responsible for populating the Exit Type class returned by calling getExitType()
	 */
	public void addUserExitTypes(TestCaseExitType exitType) {
		// TODO - Populate the User Exit Type class with the related user exit type patterns, exit types, and descriptions.
		// The set of user exit types should match those configured against the implementation.

		exitType.addUserExitType(new TestCaseUserExitType(IExitType.FAIL, "Error response detected.", "ERROR")); // Update the regular expression based on the responses.  This is only an example.
	}

	/*
	public void testCustom() {
	
		// TODO - Create an instance of a live or dummy connection handler.
		IConnectionHandler connection = ...
	
		// TODO - Create and populate the Input bean.
		AddSubscriberInput input = ...
		
		AddSubscriberOutput output = new AddSubscriberOutput();
	
		IExitType exitType = getExitType();
		
		runTest(connection, input, output, exitType);
		
		// TODO - Assert exitType and output parameters are set according to the expected test results.
		String exitTypeValue = exitType.getType();
	
		assertNotNull("Expected exit type to be set.", exitTypeValue);
		assertTrue("Expected Success exit type.", exitTypeValue.equals(IExitType.SUCCEED));
	
	}
	*/

	/**
	 * The main method will run all the tests within this testcase.  The main can be invoked as part of 
	 * an automated test routine.
	 * 
	 * @param args no arguments required
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ResSub_mobile_dataTestCase.class);
	}

}
		