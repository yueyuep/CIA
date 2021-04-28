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
package org.apache.tools.ant.taskdefs.optional.rjunit;
import junit.runner.TestCollector;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
/**
 * An element representing the client configuration.
 *
 * <pre>
 * <!ELEMENT server (jvmarg)* (classpath)* (test)* (batchtest)*>
 * <!ATTLIST server port numeric 6666>
 * <!ATTLIST server host CDATA 127.0.0.1>
 * </pre>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public final class ClientElement extends org.apache.tools.ant.ProjectComponent {
    /**
     * resources
     */
    private static final org.apache.avalon.excalibur.i18n.Resources RES = org.apache.avalon.excalibur.i18n.ResourceManager.getPackageResources(org.apache.tools.ant.taskdefs.optional.rjunit.ClientElement.class);

    /**
     * port to contact the server. Default to 6666
     */
    private int port = 6666;

    /**
     * server hostname to connect to. Default to 127.0.0.1
     */
    private java.lang.String host = "127.0.0.1";

    /**
     * test collector elements
     */
    private java.util.ArrayList testCollectors = new java.util.ArrayList();

    /**
     * the command line to launch the TestRunner
     */
    private org.apache.tools.ant.types.CommandlineJava cmd = new org.apache.tools.ant.types.CommandlineJava();

    /**
     * the parent task
     */
    private org.apache.tools.ant.taskdefs.optional.rjunit.RJUnitTask parent;

    /**
     * help debug the TestRunner
     */
    private boolean debug = false;

    /**
     * create a new client
     */
    public ClientElement(org.apache.tools.ant.taskdefs.optional.rjunit.RJUnitTask value) {
        parent = value;
        cmd.setClassname("org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunner");
    }

    /**
     * core entry point
     */
    public final void execute() throws org.apache.tools.ant.BuildException {
        try {
            preExecute();
            doExecute();
        } finally {
            postExecute();
        }
    }

    protected void preExecute() throws org.apache.tools.ant.BuildException {
        // must appended to classpath to avoid conflicts.
        org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.addClasspathEntry(createClasspath(), "/junit/framework/TestCase.class");
        org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.addClasspathEntry(createClasspath(), "/org/apache/tools/ant/Task.class");
        org.apache.tools.ant.taskdefs.optional.rjunit.JUnitHelper.addClasspathEntry(createClasspath(), "/org/apache/tools/ant/taskdefs/optional/rjunit/remote/TestRunner.class");
    }

    protected void doExecute() throws org.apache.tools.ant.BuildException {
        java.io.File tmp = configureTestRunner();
        org.apache.tools.ant.taskdefs.Execute execute = new org.apache.tools.ant.taskdefs.Execute(new org.apache.tools.ant.taskdefs.LogStreamHandler(parent, org.apache.tools.ant.Project.MSG_VERBOSE, org.apache.tools.ant.Project.MSG_VERBOSE));
        execute.setCommandline(cmd.getCommandline());
        execute.setAntRun(project);
        log(org.apache.tools.ant.taskdefs.optional.rjunit.ClientElement.RES.getString("task.process-cmdline.log", cmd.toString()), Project.MSG_VERBOSE);
        int retVal = 0;
        try {
            retVal = execute.execute();
            if (retVal != 0) {
                throw new org.apache.tools.ant.BuildException("task.process-failed.error");
            }
        } catch (java.io.IOException e) {
            java.lang.String msg = org.apache.tools.ant.taskdefs.optional.rjunit.ClientElement.RES.getString("task.process-failed.error");
            throw new org.apache.tools.ant.BuildException(msg, e);
        } finally {
            tmp.delete();
        }
    }

    protected void postExecute() {
        // nothing
    }

    /**
     *
     *
     * @return all collected tests specified with test elements.
     */
    protected java.util.Enumeration collectTests() {
        final int count = testCollectors.size();
        final java.util.Enumeration[] tests = new java.util.Enumeration[count];
        for (int i = 0; i < count; i++) {
            junit.runner.TestCollector te = ((junit.runner.TestCollector) (testCollectors.get(i)));
            tests[i] = te.collectTests();
        }
        return new org.apache.tools.ant.taskdefs.optional.rjunit.CompoundEnumeration(tests);
    }

    /**
     * Configure the runner with the appropriate configuration file.
     *
     * @return the reference to the temporary configuration file
    to be deleted once the TestRunner has ended.
     */
    protected java.io.File configureTestRunner() throws org.apache.tools.ant.BuildException {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("debug", java.lang.String.valueOf(debug));
        props.setProperty("host", host);
        props.setProperty("port", java.lang.String.valueOf(port));
        // get all test classes to run...
        java.lang.StringBuffer buf = new java.lang.StringBuffer(10240);
        java.util.Enumeration classnames = collectTests();
        while (classnames.hasMoreElements()) {
            java.lang.String classname = ((java.lang.String) (classnames.nextElement()));
            buf.append(classname).append(" ");
        } 
        props.setProperty("classnames", buf.toString());
        // dump the properties to a temporary file.
        org.apache.tools.ant.util.FileUtils futils = org.apache.tools.ant.util.FileUtils.newFileUtils();
        java.io.File f = futils.createTempFile("junit-antrunner", "tmp", new java.io.File("."));
        java.io.OutputStream os = null;
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            os = new java.io.BufferedOutputStream(new java.io.FileOutputStream(f));
            props.store(baos, "JUnit Ant Runner configuration file");
            log(baos.toString(), Project.MSG_VERBOSE);
            os.write(baos.toByteArray());
        } catch (java.io.IOException e) {
            throw new org.apache.tools.ant.BuildException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (java.io.IOException e) {
                }
            }
        }
        // configure the runner
        cmd.createArgument().setValue("-file");
        cmd.createArgument().setValue(f.getAbsolutePath());
        return f;
    }

    // --- Ant bean setters
    /**
     * set the port to connect to
     */
    public void setPort(int value) {
        port = value;
    }

    /**
     * set the host to contact
     */
    public void setHost(java.lang.String value) {
        host = value;
    }

    /**
     * set debug mode for the runner. it will log a file to working dir
     */
    public void setDebug(boolean flag) {
        debug = flag;
    }

    /**
     * Create a new JVM argument.
     */
    public Commandline.Argument createJvmarg() {
        return cmd.createVmArgument();
    }

    /**
     * classpath to be set for running tests
     */
    public org.apache.tools.ant.types.Path createClasspath() {
        return cmd.createClasspath(getProject());
    }

    /**
     * add a single test element
     */
    public void addConfiguredTest(org.apache.tools.ant.taskdefs.optional.rjunit.TestElement value) {
        testCollectors.add(value);
    }

    /**
     * add a batch test element
     */
    public void addConfiguredBatchTest(org.apache.tools.ant.taskdefs.optional.rjunit.BatchTestElement value) {
        // add the classpath of batchtest to cmd classpath
        org.apache.tools.ant.types.Path path = value.getPath();
        cmd.getClasspath().append(path);
        testCollectors.add(value);
    }
}