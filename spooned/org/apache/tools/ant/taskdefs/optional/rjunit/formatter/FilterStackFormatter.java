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
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData;
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent;
import org.apache.tools.ant.util.StringUtils;
/**
 * Filtered Formatter that strips out unwanted stack frames from the full
 * stack trace, for instance it will filter the lines containing the
 * following matches:
 * <pre>
 *   junit.framework.TestCase
 *   junit.framework.TestResult
 *   junit.framework.TestSuite
 *   junit.framework.Assert.
 *   junit.swingui.TestRunner
 *   junit.awtui.TestRunner
 *   junit.textui.TestRunner
 *   java.lang.reflect.Method.invoke(
 *   org.apache.tools.ant.
 * </pre>
 * Removing all the above will help to make stacktrace more readable.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class FilterStackFormatter extends org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterFormatter {
    /**
     * the set of matches to look for in a stack trace
     */
    private static final java.lang.String[] DEFAULT_TRACE_FILTERS = new java.lang.String[]{ "junit.framework.TestCase", "junit.framework.TestResult", "junit.framework.TestSuite", "junit.framework.Assert."// don't filter AssertionFailure
    , "junit.swingui.TestRunner", "junit.awtui.TestRunner", "junit.textui.TestRunner", "java.lang.reflect.Method.invoke(", "org.apache.tools.ant." };

    private final java.lang.String[] filters = org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterStackFormatter.getFilters();

    /**
     * Creates a new <tt>FilterStackFormatter</tt>
     *
     * @param formatter
     * 		the formatter to be filtered.
     */
    public FilterStackFormatter(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter formatter) {
        super(formatter);
    }

    public void onTestFailure(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        filterEvent(evt);
        super.onTestFailure(evt);
    }

    public void onTestError(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        filterEvent(evt);
        super.onTestFailure(evt);
    }

    protected void filterEvent(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        java.lang.String filteredTrace = filter(evt.getError().getStackTrace());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData error = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData(evt.getError().getType(), evt.getError().getMessage(), filteredTrace);
        evt.setError(error);
    }

    protected java.lang.String filter(java.lang.String trace) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(trace, "\r\n");
        java.lang.StringBuffer buf = new java.lang.StringBuffer(trace.length());
        while (st.hasMoreTokens()) {
            java.lang.String line = st.nextToken();
            if (accept(line)) {
                buf.append(line).append(StringUtils.LINE_SEP);
            }
        } 
        return buf.toString();
    }

    /**
     * Check whether or not the line should be accepted.
     *
     * @param line
     * 		the line to be check for acceptance.
     * @return <tt>true</tt> if the line is accepted, <tt>false</tt> if not.
     */
    protected boolean accept(java.lang.String line) {
        for (int i = 0; i < filters.length; i++) {
            if (line.indexOf(filters[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     *
     * @return the filters to use for this
     */
    protected static java.lang.String[] getFilters() {
        // @fixme hack for now, need something better.
        // using configuration properties ?
        java.lang.String filters = java.lang.System.getProperty("ant.rjunit.stacktrace.filters");
        if (filters == null) {
            return org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterStackFormatter.DEFAULT_TRACE_FILTERS;
        }
        java.util.StringTokenizer st = new java.util.StringTokenizer(filters, ",");
        java.lang.String[] results = new java.lang.String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            results[i++] = st.nextToken();
        } 
        return results;
    }
}