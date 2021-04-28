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
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestSuite;
import org.apache.tools.ant.taskdefs.optional.rjunit.TestRunRecorder;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.PlainFormatter;
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener;
/**
 * TestCase for the test runner.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class TestRunnerTest extends junit.framework.TestCase implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener {
    public static final int PORT = 1234;

    protected org.apache.tools.ant.taskdefs.optional.rjunit.remote.Server server;

    protected org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner runner;

    protected org.apache.tools.ant.taskdefs.optional.rjunit.TestRunRecorder recorder;

    protected boolean done;

    public TestRunnerTest(java.lang.String s) {
        super(s);
    }

    protected void setUp() throws java.lang.Exception {
        server = createServer();
        server.start();
        runner = createClient();
        recorder = new org.apache.tools.ant.taskdefs.optional.rjunit.TestRunRecorder();
        server.addListener(recorder);
        server.addListener(this);
    }

    protected void tearDown() throws java.lang.Exception {
        server.shutdown();
        runner.stop();
    }

    protected org.apache.tools.ant.taskdefs.optional.rjunit.remote.Server createServer() throws java.lang.Exception {
        return new org.apache.tools.ant.taskdefs.optional.rjunit.remote.Server(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunnerTest.PORT);
    }

    protected org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner createClient() throws java.lang.Exception {
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner client = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner();
        client.setDebug(true);
        client.setHost("127.0.0.1");
        client.setPort(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunnerTest.PORT);
        return client;
    }

    public void testNullTestCase() throws java.lang.Exception {
        runner.addTestClassName(TestCases.NullTestCase.class.getName());
        // server.addListener( new PlainFormatter() );
        runner.run();
        synchronized(this) {
            while (!done) {
                wait();
            } 
        }
        assertEquals(1, recorder.runStarted.size());
        /* assertTrue(recorder.runStarted.elementAt(0).toSt("testSuccess"));
        assertTrue(started.contains("testFailure"));
        assertTrue(started.contains("testError"));
         */
    }

    public void testFailSetupTestCase() throws java.lang.Exception {
        runner.addTestClassName(TestCases.FailSetupTestSuite.class.getName());
        runner.run();
        synchronized(this) {
            while (!done) {
                wait();
            } 
        }
        assertEquals(1, recorder.runStarted.size());
        assertEquals(1, recorder.runEnded.size());
    }

    public void testFailSetupTestSuite() throws java.lang.Exception {
        runner.addTestClassName(TestCases.FailSetupTestSuite.class.getName());
        runner.run();
        synchronized(this) {
            while (!done) {
                wait();
            } 
        }
        assertEquals(1, recorder.runStarted.size());
        assertEquals(1, recorder.runEnded.size());
    }

    public static void main(java.lang.String[] args) {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunnerTest.class);
        junit.textui.TestRunner.run(suite);
    }

    // TestRunListener implementation
    public void onTestStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }

    public void onTestEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }

    public void onTestFailure(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }

    public void onRunStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent count) {
    }

    public void onRunEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        synchronized(this) {
            done = true;
            notify();
        }
    }

    public void onRunStopped(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        synchronized(this) {
            done = true;
            notify();
        }
    }

    public void onSuiteStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }

    public void onSuiteEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }

    public void onTestError(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
    }
}