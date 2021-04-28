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
import org.apache.tools.ant.util.StringUtils;
/**
 * Provide the basic events to be used during the tests.
 * This is not very extensible but since the events should be somewhat
 * limited, for now this is better to do it like this.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class TestRunEvent extends java.util.EventObject {
    // received from clients
    public static final int RUN_STARTED = 0;

    public static final int RUN_ENDED = 1;

    public static final int RUN_STOPPED = 2;

    public static final int TEST_STARTED = 3;

    public static final int TEST_FAILURE = 4;

    public static final int TEST_ERROR = 5;

    public static final int TEST_ENDED = 6;

    public static final int SUITE_STARTED = 7;

    public static final int SUITE_ENDED = 8;

    // received from server
    public static final int RUN_STOP = 9;

    /**
     * the type of event
     */
    private int type = -1;

    /**
     * timestamp for all events
     */
    private long timestamp = java.lang.System.currentTimeMillis();

    /**
     * name of testcase(method name) or testsuite (classname)
     */
    private java.lang.String name;

    /**
     * stacktrace for error or failure
     */
    private org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData error;

    /**
     * properties for end of testrun
     */
    private java.util.Properties props;

    /**
     * handy result for each end of sequence
     */
    private org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary result;

    public TestRunEvent(java.lang.Integer id, int type) {
        super(id);
        this.type = type;
    }

    public TestRunEvent(java.lang.Integer id, int type, java.lang.String name, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary result) {
        this(id, type, name);
        this.result = result;
    }

    public TestRunEvent(java.lang.Integer id, int type, java.lang.String name) {
        this(id, type);
        this.name = name;
    }

    public TestRunEvent(java.lang.Integer id, int type, java.util.Properties props, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary result) {
        this(id, type);
        this.props = props;
        this.result = result;
    }

    public TestRunEvent(java.lang.Integer id, int type, java.lang.String name, java.lang.Throwable t) {
        this(id, type, name);
        this.error = new org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData(t);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setError(org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData error) {
        this.error = error;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public void setProperties(java.util.Properties props) {
        this.props = props;
    }

    public int getType() {
        return type;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public java.lang.String getName() {
        return name;
    }

    public org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestSummary getSummary() {
        return result;
    }

    public org.apache.tools.ant.taskdefs.optional.rjunit.remote.ExceptionData getError() {
        return error;
    }

    public java.util.Properties getProperties() {
        return props;
    }

    public boolean equals(java.lang.Object o) {
        if (o instanceof org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent) {
            org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent other = ((org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent) (o));
            return (((((type == other.type) && (timestamp == other.timestamp)) && (name == null ? other.name == null : name.equals(other.name))) && (error == null ? other.error == null : error.equals(other.error))) && (props == null ? other.props == null : props.equals(other.props))) && (result == null ? other.result == null : result.equals(other.result));
        }
        return false;
    }

    public java.lang.String toString() {
        java.lang.StringBuffer buf = new java.lang.StringBuffer();
        buf.append("id: ").append(source);
        buf.append("type: ").append(type);
        return buf.toString();
    }
}