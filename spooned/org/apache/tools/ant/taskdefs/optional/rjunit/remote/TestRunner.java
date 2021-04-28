/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.tools.ant.taskdefs.optional.rjunit.remote;
import java.io.IOException;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.PlainFormatter;
import org.apache.tools.ant.util.StringUtils;
/**
 * TestRunner for running tests and send results to a remote server.
 *
 * <i>
 * This code is based on the code from Erich Gamma made for the
 * JUnit plugin for <a href="http://www.eclipse.org">Eclipse</a> and is
 * merged with code originating from Ant 1.4.x.
 * </i>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class TestRunner implements junit.framework.TestListener {
    /**
     * unique identifier for the runner
     */
    private final java.lang.Integer id = new java.lang.Integer(new java.util.Random().nextInt());

    /**
     * host to connect to
     */
    private java.lang.String host = "127.0.0.1";

    /**
     * port to connect to
     */
    private int port = -1;

    /**
     * handy debug flag
     */
    private boolean debug = false;

    /**
     * the list of test class names to run
     */
    private final java.util.ArrayList testClassNames = new java.util.ArrayList();

    /**
     * result of the current test
     */
    private junit.framework.TestResult testResult;

    /**
     * client socket to communicate with the server
     */
    private java.net.Socket clientSocket;

    /**
     * writer to send message to the server
     */
    private org.apache.tools.ant.taskdefs.optional.rjunit.remote.Messenger messenger;

    /**
     * helpful formatter to debug events directly here
     */
    private final org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter debugFormatter = new org.apache.tools.ant.taskdefs.optional.rjunit.formatter.PlainFormatter();

    /**
     * bean constructor
     */
    public TestRunner() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("file", "rjunit-client-debug.log");
        debugFormatter.init(props);
    }

    /**
     * Set the debug mode.
     *
     * @param debug
     * 		true to set to debug mode otherwise false.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Set the port to connect to the server
     *
     * @param port
     * 		a valid port number.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Set the hostname of the server
     *
     * @param host
     * 		the hostname or ip of the server
     */
    public void setHost(java.lang.String host) {
        this.host = host;
    }

    /**
     * Add a test class name to be executed by this runner.
     *
     * @param classname
     * 		the class name of the test to run.
     */
    public void addTestClassName(java.lang.String classname) {
        testClassNames.add(classname);
    }

    /**
     * Thread listener for a shutdown from the server
     * Note that it will stop any running test.
     */
    private class StopThread extends java.lang.Thread {
        public void run() {
            try {
                org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = null;
                if ((evt = messenger.read()) != null) {
                    if (evt.getType() == TestRunEvent.RUN_STOP) {
                        TestRunner.this.stop();
                    }
                }
            } catch (java.lang.Exception e) {
                TestRunner.this.stop();
            }
        }
    }

    /**
     * Entry point for command line.
     * Usage:
     * <pre>
     * TestRunner -classnames <classnames> -port <port> -host <host> -debug
     * -file
     * -classnames <list of whitespace separated classnames to run>
     * -port       <port to connect to>
     * -host       <host to connect to>
     * -debug      to run in debug mode
     * </pre>
     */
    public static void main(java.lang.String[] args) throws java.lang.Exception {
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner testRunServer = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner();
        testRunServer.init(args);
        testRunServer.run();
    }

    /**
     * Parses the arguments of command line.
     * testClassNames, host, port, listeners and debug mode are set
     *
     * @see #main(String[])
     */
    protected void init(java.lang.String[] args) throws java.lang.Exception {
        for (int i = 0; i < args.length; i++) {
            if ("-file".equalsIgnoreCase(args[i])) {
                // @fixme if you mix file and other options it will be a mess,
                // not important right now.
                java.io.FileInputStream fis = new java.io.FileInputStream(args[++i]);
                java.util.Properties props = new java.util.Properties();
                props.load(fis);
                fis.close();
                init(props);
            }
            if ("-classnames".equalsIgnoreCase(args[i])) {
                for (int j = ++i; j < args.length; j++) {
                    if (args[j].startsWith("-"))
                        break;

                    addTestClassName(args[j]);
                }
            }
            if ("-port".equalsIgnoreCase(args[i])) {
                setPort(java.lang.Integer.parseInt(args[++i]));
            }
            if ("-host".equalsIgnoreCase(args[i])) {
                setHost(args[++i]);
            }
            if ("-debug".equalsIgnoreCase(args[i])) {
                setDebug(true);
            }
        }
    }

    /**
     * Initialize the TestRunner from properties.
     *
     * @param props
     * 		the properties containing configuration data.
     * @see #init(String[])
     */
    protected void init(java.util.Properties props) {
        if (props.getProperty("debug") != null) {
            setDebug(true);
        }
        java.lang.String port = props.getProperty("port");
        if (port != null) {
            setPort(java.lang.Integer.parseInt(port));
        }
        java.lang.String host = props.getProperty("host");
        if (host != null) {
            setHost(host);
        }
        java.lang.String classnames = props.getProperty("classnames");
        if (classnames != null) {
            java.util.StringTokenizer st = new java.util.StringTokenizer(classnames);
            while (st.hasMoreTokens()) {
                addTestClassName(st.nextToken());
            } 
        }
    }

    public final void run() throws java.lang.Exception {
        if (testClassNames.size() == 0) {
            throw new java.lang.IllegalArgumentException("No TestCase specified");
        }
        connect();
        testResult = new junit.framework.TestResult();
        testResult.addListener(this);
        runTests();
        testResult.removeListener(this);
        if (testResult != null) {
            testResult.stop();
            testResult = null;
        }
    }

    /**
     * Transform all classnames into instantiated <tt>Test</tt>.
     *
     * @throws Exception
     * 		a generic exception that can be thrown while
     * 		instantiating a test case.
     */
    protected java.util.Map getSuites() throws java.lang.Exception {
        final int count = testClassNames.size();
        log(("Extracting testcases from " + count) + " classnames...");
        final java.util.Map suites = new java.util.HashMap();
        for (int i = 0; i < count; i++) {
            java.lang.String classname = ((java.lang.String) (testClassNames.get(i)));
            try {
                junit.framework.Test test = org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.getTest(null, classname);
                if (test != null) {
                    suites.put(classname, test);
                }
            } catch (java.lang.Exception e) {
                // notify log error instead ?
                log("Could not get Test instance from " + classname);
                log(e);
            }
        }
        log(("Extracted " + suites.size()) + " testcases.");
        return suites;
    }

    private void runTests() throws java.lang.Exception {
        java.util.Map suites = getSuites();
        // count all testMethods and inform TestRunListeners
        int count = countTests(suites.values());
        log("Total tests to run: " + count);
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.RUN_STARTED);
        if (debug) {
            debugFormatter.onRunStarted(evt);
        }
        fireEvent(evt);
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary runSummary = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary();
        runSummary.start(testResult);
        for (java.util.Iterator it = suites.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry entry = ((java.util.Map.Entry) (it.next()));
            java.lang.String name = ((java.lang.String) (entry.getKey()));
            junit.framework.Test test = ((junit.framework.Test) (entry.getValue()));
            if (test instanceof junit.framework.TestCase) {
                test = new junit.framework.TestSuite(name);
            }
            runTest(test, name);
        }
        runSummary.stop(testResult);
        // inform TestRunListeners of test end
        int type = ((testResult == null) || testResult.shouldStop()) ? TestRunEvent.RUN_STOPPED : TestRunEvent.RUN_ENDED;
        evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, type, java.lang.System.getProperties(), runSummary);
        if (debug) {
            debugFormatter.onRunEnded(evt);
        }
        fireEvent(evt);
        log(("Finished after " + runSummary.elapsedTime()) + "ms");
        shutDown();
    }

    /**
     * run a single suite and dispatch its results.
     *
     * @param test
     * 		the instance of the testsuite to run.
     * @param name
     * 		the name of the testsuite (classname)
     */
    private void runTest(junit.framework.Test test, java.lang.String name) {
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.SUITE_STARTED, name);
        if (debug) {
            debugFormatter.onSuiteStarted(evt);
        }
        fireEvent(evt);
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary suiteSummary = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary();
        suiteSummary.start(testResult);
        try {
            test.run(testResult);
        } finally {
            suiteSummary.stop(testResult);
            evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.SUITE_ENDED, name, suiteSummary);
            if (debug) {
                debugFormatter.onSuiteEnded(evt);
            }
            fireEvent(evt);
        }
    }

    /**
     * count the number of test methods in all tests
     */
    private final int countTests(java.util.Collection tests) {
        int count = 0;
        for (java.util.Iterator it = tests.iterator(); it.hasNext();) {
            junit.framework.Test test = ((junit.framework.Test) (it.next()));
            count = count + test.countTestCases();
        }
        return count;
    }

    protected void stop() {
        if (testResult != null) {
            testResult.stop();
        }
    }

    /**
     * connect to the specified host and port.
     *
     * @throws IOException
     * 		if any error occurs during connection.
     */
    protected void connect() throws java.io.IOException {
        log(((("Connecting to " + host) + " on port ") + port) + "...");
        clientSocket = new java.net.Socket(host, port);
        messenger = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.Messenger(clientSocket.getInputStream(), clientSocket.getOutputStream());
        new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner.StopThread().start();
    }

    protected void shutDown() {
        try {
            if (messenger != null) {
                messenger.close();
                messenger = null;
            }
        } catch (java.io.IOException e) {
            log(e);
        }
        try {
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        } catch (java.io.IOException e) {
            log(e);
        }
    }

    protected void fireEvent(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        try {
            messenger.writeEvent(evt);
        } catch (java.io.IOException e) {
            log(e);
        }
    }

    // -------- JUnit TestListener implementation
    public void startTest(junit.framework.Test test) {
        java.lang.String testName = test.toString();
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.TEST_STARTED, testName);
        if (debug) {
            debugFormatter.onTestStarted(evt);
        }
        fireEvent(evt);
    }

    public void addError(junit.framework.Test test, java.lang.Throwable t) {
        java.lang.String testName = test.toString();
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.TEST_ERROR, testName, t);
        if (debug) {
            debugFormatter.onTestError(evt);
        }
        fireEvent(evt);
    }

    /**
     * this implementation is for JUnit &lt; 3.4
     *
     * @see #addFailure(Test, Throwable)
     */
    public void addFailure(junit.framework.Test test, junit.framework.AssertionFailedError afe) {
        addFailure(test, ((java.lang.Throwable) (afe)));
    }

    /**
     * This implementation is for JUnit &lt;= 3.4
     *
     * @see #addFailure(Test, AssertionFailedError)
     */
    public void addFailure(junit.framework.Test test, java.lang.Throwable t) {
        java.lang.String testName = test.toString();
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.TEST_FAILURE, testName, t);
        if (debug) {
            debugFormatter.onTestFailure(evt);
        }
        fireEvent(evt);
    }

    public void endTest(junit.framework.Test test) {
        java.lang.String testName = test.toString();
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent(id, TestRunEvent.TEST_ENDED, testName);
        if (debug) {
            debugFormatter.onTestEnded(evt);
        }
        fireEvent(evt);
    }

    public void log(java.lang.String msg) {
        if (debug) {
            java.lang.System.out.println(msg);
        }
    }

    public void log(java.lang.Throwable t) {
        if (debug) {
            t.printStackTrace();
        }
    }
}