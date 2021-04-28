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
import org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent;
/**
 * A base class that can be used to filter data.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public abstract class FilterFormatter implements org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter {
    private org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter formatter;

    protected FilterFormatter(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter formatter) {
        setFormatter(formatter);
    }

    /**
     * final to enforce chaining of initialization
     */
    public final void init(java.util.Properties props) throws org.apache.tools.ant.BuildException {
        formatter.init(props);
    }

    public void onSuiteStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onSuiteStarted(evt);
    }

    public void onSuiteEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onSuiteEnded(evt);
    }

    public void onTestStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onTestStarted(evt);
    }

    public void onTestEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onTestEnded(evt);
    }

    public void onTestFailure(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onTestFailure(evt);
    }

    public void onTestError(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onTestError(evt);
    }

    public void onRunStarted(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onRunStarted(evt);
    }

    public void onRunEnded(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onRunEnded(evt);
    }

    public void onRunStopped(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        formatter.onRunEnded(evt);
    }

    /**
     * set the wrapped formatter
     */
    protected void setFormatter(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter value) {
        formatter = value;
    }

    /**
     * return the wrapped formatter
     */
    protected org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter getFormatter() {
        return formatter;
    }
}