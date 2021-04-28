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
package org.apache.tools.ant.taskdefs.optional.rjunit.formatter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper;
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData;
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent;
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.StringUtils;
/**
 * XML Formatter. Due to the nature of the XML we are forced to store
 * everything in memory until it is finished. It might be resource
 * intensive when running lots of testcases.
 *
 * <testsuites stop="true">
 *  <testsuite name="" time="">
 *    <testcase name="" time="">
 *      <error/>
 *    </testcase>
 *    <testcase name="" time="">
 *      <failure/>
 *    </testcase>
 *  </testsuite>
 * </testsuites>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class XMLFormatter extends org.apache.tools.ant.taskdefs.optional.rjunit.formatter.BaseStreamFormatter {
    /**
     * the testsuites element for the aggregate document
     */
    public static final java.lang.String TESTSUITES = "testsuites";

    /**
     * the testsuite element
     */
    public static final java.lang.String TESTSUITE = "testsuite";

    /**
     * the testcase element
     */
    public static final java.lang.String TESTCASE = "testcase";

    /**
     * the error element
     */
    public static final java.lang.String ERROR = "error";

    /**
     * the failure element
     */
    public static final java.lang.String FAILURE = "failure";

    /**
     * the system-err element
     */
    public static final java.lang.String SYSTEM_ERR = "system-err";

    /**
     * the system-out element
     */
    public static final java.lang.String SYSTEM_OUT = "system-out";

    /**
     * package attribute for the aggregate document
     */
    public static final java.lang.String ATTR_PACKAGE = "package";

    /**
     * name attribute for property, testcase and testsuite elements
     */
    public static final java.lang.String ATTR_NAME = "name";

    /**
     * time attribute for testcase and testsuite elements
     */
    public static final java.lang.String ATTR_TIME = "time";

    /**
     * errors attribute for testsuite elements
     */
    public static final java.lang.String ATTR_ERRORS = "errors";

    /**
     * failures attribute for testsuite elements
     */
    public static final java.lang.String ATTR_FAILURES = "failures";

    /**
     * tests attribute for testsuite elements
     */
    public static final java.lang.String ATTR_TESTS = "tests";

    /**
     * type attribute for failure and error elements
     */
    public static final java.lang.String ATTR_TYPE = "type";

    /**
     * message attribute for failure elements
     */
    public static final java.lang.String ATTR_MESSAGE = "message";

    /**
     * the properties element
     */
    public static final java.lang.String PROPERTIES = "properties";

    /**
     * the property element
     */
    public static final java.lang.String PROPERTY = "property";

    /**
     * value attribute for property elements
     */
    public static final java.lang.String ATTR_VALUE = "value";

    /**
     * The XML document.
     */
    private org.w3c.dom.Document doc = org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.getDocumentBuilder().newDocument();

    /**
     * The wrapper for the whole testsuite.
     */
    private org.w3c.dom.Element rootElement = doc.createElement(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.TESTSUITES);

    private org.w3c.dom.Element lastTestElement = null;

    private org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent lastTestEvent = null;

    private org.w3c.dom.Element lastSuiteElement = null;

    private long programStart;

    public void onSuiteStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        java.lang.String fullclassname = evt.getName();
        int pos = fullclassname.lastIndexOf('.');
        // a missing . might imply no package at all. Don't get fooled.
        java.lang.String pkgName = (pos == (-1)) ? "" : fullclassname.substring(0, pos);
        java.lang.String classname = (pos == (-1)) ? fullclassname : fullclassname.substring(pos + 1);
        org.w3c.dom.Element suite = doc.createElement(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.TESTSUITE);
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_NAME, classname);
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_PACKAGE, pkgName);
        rootElement.appendChild(suite);
        lastSuiteElement = suite;
    }

    public void onSuiteEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        org.w3c.dom.Element suite = lastSuiteElement;
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary summary = evt.getSummary();
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_TIME, java.lang.String.valueOf(summary.elapsedTime() / 1000.0F));
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_TESTS, java.lang.String.valueOf(summary.runCount()));
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_FAILURES, java.lang.String.valueOf(summary.failureCount()));
        suite.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_ERRORS, java.lang.String.valueOf(summary.errorCount()));
        lastSuiteElement = null;
    }

    public void onRunEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        final java.lang.String elapsedTime = java.lang.String.valueOf(evt.getTimeStamp() - programStart);
        rootElement.setAttribute("elapsed_time", elapsedTime);
        // Output properties
        final org.w3c.dom.Element propsElement = doc.createElement(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.PROPERTIES);
        rootElement.appendChild(propsElement);
        final java.util.Properties props = evt.getProperties();
        if (props != null) {
            java.util.Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                java.lang.String name = ((java.lang.String) (e.nextElement()));
                org.w3c.dom.Element propElement = doc.createElement(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.PROPERTY);
                propElement.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_NAME, name);
                propElement.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_VALUE, props.getProperty(name));
                propsElement.appendChild(propElement);
            } 
        }
        close();
    }

    public void onRunStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        programStart = evt.getTimeStamp();
        final java.lang.String date = org.apache.tools.ant.util.DateUtils.format(programStart, DateUtils.ISO8601_DATETIME_PATTERN);
        rootElement.setAttribute("program_start", date);
    }

    public void onRunStopped(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        rootElement.setAttribute("stopped", "true");
        onRunEnded(evt);
    }

    public void onTestStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        org.w3c.dom.Element test = doc.createElement(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.TESTCASE);
        java.lang.String name = org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.getTestName(evt.getName());
        test.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_NAME, name);
        java.lang.String suiteName = org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.getSuiteName(evt.getName());
        java.lang.String lastSuiteName = (lastSuiteElement.getAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_PACKAGE) + ".") + lastSuiteElement.getAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_NAME);
        if (!suiteName.equals(lastSuiteName)) {
            throw new org.apache.tools.ant.BuildException((("Received testcase from test " + suiteName) + " and was expecting ") + lastSuiteElement.getAttribute("name"));
        }
        lastSuiteElement.appendChild(test);
        lastTestElement = test;
        lastTestEvent = evt;
    }

    public void onTestEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        // with a TestSetup, startTest and endTest are not called.
        if (lastTestEvent == null) {
            onTestStarted(evt);
        }
        float time = (evt.getTimeStamp() - lastTestEvent.getTimeStamp()) / 1000.0F;
        lastTestElement.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_TIME, java.lang.Float.toString(time));
        lastTestElement = null;
        lastTestEvent = null;
    }

    public void onTestError(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        onTestFailure(evt);
    }

    public void onTestFailure(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        java.lang.String type = (evt.getType() == org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent.TEST_FAILURE) ? org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.FAILURE : org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ERROR;
        org.w3c.dom.Element nested = doc.createElement(type);
        lastTestElement.appendChild(nested);
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData error = evt.getError();
        nested.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_MESSAGE, error.getMessage());
        nested.setAttribute(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter.ATTR_TYPE, error.getType());
        org.w3c.dom.Text text = doc.createTextNode(error.getStackTrace());
        nested.appendChild(text);
        onTestEnded(evt);
    }

    protected void close() {
        // the underlying writer uses UTF8 encoding
        getWriter().println("<?xml version='1.0' encoding='UTF-8' ?>");
        java.lang.String now = org.apache.tools.ant.util.DateUtils.format(new java.util.Date(), DateUtils.ISO8601_DATETIME_PATTERN);
        rootElement.setAttribute("snapshot_created", now);
        try {
            final org.apache.tools.ant.util.DOMElementWriter domWriter = new org.apache.tools.ant.util.DOMElementWriter();
            domWriter.write(rootElement, getWriter(), 0, "  ");
        } catch (java.io.IOException e) {
            throw new org.apache.tools.ant.BuildException(e);
        } finally {
            super.close();
        }
    }

    private static javax.xml.parsers.DocumentBuilder getDocumentBuilder() {
        try {
            return javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (java.lang.Exception exc) {
            throw new java.lang.ExceptionInInitializerError(exc);
        }
    }
}