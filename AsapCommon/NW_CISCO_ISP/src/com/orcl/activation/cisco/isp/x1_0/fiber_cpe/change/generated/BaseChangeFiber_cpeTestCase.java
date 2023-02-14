/*
 * WARNING: DO NOT MODIFY THE CONTENTS OF THIS FILE.
 *
 *    This file contains generated code.  Any changes to the file may be overwritten.
 * All process logic should be implemented in the related Processor class and 
 * non-generated supporting classes.
 *
 *    Do not place this file under source control.  Doing so may prevent proper generation
 * and synchronization of this file with the supporting model.
 *
 *=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 */
package com.orcl.activation.cisco.isp.x1_0.fiber_cpe.change.generated;

import com.orcl.activation.cisco.isp.x1_0.fiber_cpe.change.ChangeFiber_cpeProcessor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import com.mslv.studio.activation.implementation.ConsoleLogger;
import com.mslv.studio.activation.implementation.IConnectionHandler;
import com.mslv.studio.activation.implementation.IExitType;
import com.mslv.studio.activation.implementation.ILogger;
import com.mslv.studio.activation.implementation.test.TestCaseConnectionHandler;
import com.mslv.studio.activation.implementation.test.TestCaseExitType;


/**
 * This class implements a JUnit testcase.  The testcase is comprised of
 * a number of tests.  The testcase can be executed as a JavaApplication
 * or a JUnit Test within MetaSolv Studio.
 * 
 * Add a method prefixed by "test" for each test.  Within the method
 * include assertions to check various values and conditions to pass
 * or fail steps of the test.
 * 
 * Use the following assert methods to check specific values during test
 * execution. There are numerous type specific methods for each flavour
 * of assertion.
 * 
 * Exceptions thrown from the test are caught and reported as a test
 * failure. Ensure expected test exceptions are caught and asserted
 * approrpriately.
 * 
 * Assert the two objects are the equivalent (i.e.
 * actual.equals(expected) ) assertEquals(message, expected, actual);
 * 
 * Assert the two objects are the same object instance (i.e. ==)
 * assertSame(message, expected, actual); assertNotSame(message,
 * expected, actual);
 * 
 * Check a condition using: assertTrue(message, condition);
 * assertFalse(message, condition);
 * 
 * Check for Null using: assertNull(message, object);
 * assertNotNull(message, object);
 *
 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
 */
abstract public class BaseChangeFiber_cpeTestCase extends TestCase {
	/*
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public static final String TESTDATA_EXTENSION = "testdata";
	public static final String TESTINFO_EXTENSION = "testinfo";

	public String currentFileName;

	public int messageCount = 1;

	/**
	 * Initialize the logger to a console logger rather than the Diagnosis logger since we are sandbox testing
	 * To assert logging information, a custom ILogger can be defined and appropriate assert statements included,
	 * similar to the ConnectionHandler approach.
	 */
	ILogger logger = new ConsoleLogger();

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public BaseChangeFiber_cpeTestCase(String name) {
		super(name);
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private static final String ROOT = "src/";
	private static final String TEST_FOLDER = ROOT + "/com/orcl/activation/cisco/isp/x1_0/fiber_cpe/change/test/";

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private ChangeFiber_cpeInput makeChangeFiber_cpeInput(String filename) {

		File inputFile = new File(TEST_FOLDER + filename);

		try {
			FileInputStream inputStream = new FileInputStream(inputFile);
			Properties parms = new Properties();
			parms.load(inputStream);

			ChangeFiber_cpeInput input = new ChangeFiber_cpeInput(parms);

			return input;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		return null;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private ISystemParameters makeSystemParameters(String filename) {

		File inputFile = new File(TEST_FOLDER + filename);

		try {
			FileInputStream inputStream = new FileInputStream(inputFile);
			Properties parms = new Properties();
			parms.load(inputStream);

			ISystemParameters input = new SystemParameters(parms, null, null, logger);

			return input;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		return null;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private void runTest(String filename) {

		currentFileName = filename;
		logger.setContext(this);
		logger.logDebug("Begin Test: " + currentFileName);

		String testResultsFile = filename.substring(0, filename.lastIndexOf(TESTDATA_EXTENSION));
		testResultsFile = testResultsFile + TESTINFO_EXTENSION;

		TestInfo testInfo = new TestInfo(TEST_FOLDER + testResultsFile);

		IConnectionHandler connection = getDefaultConnectionHandler(testInfo);

		ISystemParameters systemParameters = makeSystemParameters(filename);

		ChangeFiber_cpeInput input = makeChangeFiber_cpeInput(filename);

		ChangeFiber_cpeOutput output = new ChangeFiber_cpeOutput();

		IExitType exitType = getExitType();

		runTest(connection, input, output, exitType, systemParameters);

		if (testInfo.checkExitType()) {
			String exitTypeValue = exitType.getType();

			assertNotNull(currentFileName + ": Expected exit type to be set.", exitTypeValue);
			assertEquals(currentFileName + ": Unexpected Exit Type.", testInfo.getExitType(), exitTypeValue);
		}

		logger.setContext(this);
		logger.logDebug("End Test: " + currentFileName);

	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public IExitType getExitType() {
		TestCaseExitType exitType = new TestCaseExitType();

		addUserExitTypes(exitType);
		return exitType;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private IConnectionHandler getDefaultConnectionHandler(final TestInfo info) {
		IConnectionHandler connection = new TestCaseConnectionHandler(logger) {

			public String sendRequest(String message) {
				logger.logDebug("Request: " + message);

				if (info.checkSendRequest()) {
					String expectedRequest = info.getSendRequest(messageCount);

					assertEquals(currentFileName + ": Unexpected send request.", expectedRequest, message);
				}

				String response = info.getResponse(messageCount);

				logger.logDebug("Response: " + response);

				messageCount++;

				return response;
			}
		};
		return connection;
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private void runTest(IConnectionHandler connection, ChangeFiber_cpeInput input, ChangeFiber_cpeOutput output,
			IExitType exitType, ISystemParameters systemParameters) {
		assertNotNull(exitType);

		ChangeFiber_cpeProcessor processor = new ChangeFiber_cpeProcessor();

		logger.setContext(this);
		logger.logDebug("execute() - Start");
		logger.logDebug("execute() - Parameters passed:\n" + input.toString());

		processor.init(null, logger);

		try {

			logger.setContext(processor);
			processor.execute(input, output, connection, exitType, systemParameters);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception: " + e.getMessage());
		}

		logger.setContext(this);
		logger.logDebug("Exit Type: " + exitType.getType() + " Description: " + exitType.getDescription());
		logger.logDebug("execute() - End");
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	abstract public void addUserExitTypes(TestCaseExitType exitType);

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	public void testInputFiles() {

		// Find all property files in the test folder and call the test with that data.
		File f = new File(TEST_FOLDER);

		FileFilter filter = new FileFilter() {

			public boolean accept(File pathname) {

				if (pathname.isFile()) {
					String filename = pathname.getName();

					if (filename.endsWith(".testdata")) {
						return true;
					}
				}
				return false;
			}
		};

		File propertyFiles[] = f.listFiles(filter);

		// If there is a test folder, run the tests within the folder
		if (f.exists() && f.isDirectory()) {

			for (int i = 0; i < propertyFiles.length; i++) {
				File propertyFile = propertyFiles[i];

				runTest(propertyFile.getName());
			}
		}
	}

	/**
	 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
	 */
	private class TestInfo {

		/*
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		private Properties parms;
		private boolean testInfoFound = false;

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public TestInfo(String filename) {

			File inputFile = new File(filename);
			parms = new Properties();

			try {

				if (inputFile.exists()) {
					FileInputStream inputStream = new FileInputStream(inputFile);
					parms.load(inputStream);
					testInfoFound = true;
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public boolean exists() {
			return testInfoFound;
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public boolean checkExitType() {
			String value = parms.getProperty("exittype.check");

			if ((value != null) && value.equalsIgnoreCase("false")) {
				return false;
			}

			return true;
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public String getExitType() {
			String value = parms.getProperty("exittype.value");

			if (value != null) {
				if (value.equals("SUCCEED")) {
					return IExitType.SUCCEED;
				} else if (value.equals("DELAYED_FAIL")) {
					return IExitType.DELAYED_FAIL;
				} else if (value.equals("FAIL")) {
					return IExitType.FAIL;
				} else if (value.equals("MAINTENANCE")) {
					return IExitType.MAINTENANCE;
				} else if (value.equals("RETRY")) {
					return IExitType.RETRY;
				} else if (value.equals("SOFT_FAIL")) {
					return IExitType.SOFT_FAIL;
				} else if (value.equals("STOP")) {
					return IExitType.STOP;
				}

				return value;
			}

			return IExitType.SUCCEED;
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public boolean checkSendRequest() {
			String value = parms.getProperty("request.check");

			if ((value != null) && value.equalsIgnoreCase("true")) {
				return true;
			}

			return false;
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public String getSendRequest(int messageCount) {
			String value;

			if (messageCount == 1) {
				// Check for a request without the numeric suffix first.
				value = parms.getProperty("request.value");

				if (value == null) {
					value = parms.getProperty("request.value." + messageCount);
				}
			} else {
				value = parms.getProperty("request.value." + messageCount);
			}

			if (value != null) {
				return value;
			}

			return "";
		}

		/**
		 * @generated DO NOT MODIFY THE CONTENTS OF THIS FILE.
		 */
		public String getResponse(int messageCount) {
			String value;

			if (messageCount == 1) {
				// Check for a request without the numeric suffix first.
				value = parms.getProperty("response.value");

				if (value == null) {
					value = parms.getProperty("response.value." + messageCount);
				}
			} else {
				value = parms.getProperty("response.value." + messageCount);
			}

			if (value != null) {
				return value;
			}

			return "";
		}

	}
}
		