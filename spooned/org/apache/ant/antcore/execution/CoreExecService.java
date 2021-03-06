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
package org.apache.ant.antcore.execution;
import org.apache.ant.antcore.modelparser.XMLProjectParser;
import org.apache.ant.antcore.xml.XMLParseException;
import org.apache.ant.common.antlib.AntContext;
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.model.Project;
import org.apache.ant.common.service.ExecService;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.init.InitUtils;
import org.apache.ant.init.LoaderUtils;
/**
 * This is the core's implementation of the Execution Service.
 *
 * @author Conor MacNeill
 * @created 8 February 2002
 */
public class CoreExecService implements org.apache.ant.common.service.ExecService {
    /**
     * The Frame this service instance is working for
     */
    private org.apache.ant.antcore.execution.Frame frame;

    /**
     * A map of subbuild keys to the frame of the subbuild.
     */
    private java.util.Map subBuilds = new java.util.HashMap();

    /**
     * Constructor
     *
     * @param frame
     * 		the frame containing this context
     */
    protected CoreExecService(org.apache.ant.antcore.execution.Frame frame) {
        this.frame = frame;
    }

    /**
     * Execute a task. The task should have already been initialised by the
     * core. This is checked
     *
     * @param task
     * 		the task to be executed
     * @exception ExecutionException
     * 		if there is an execution problem
     */
    public void executeTask(org.apache.ant.common.antlib.Task task) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.common.antlib.AntContext context = task.getAntContext();
        if (!(context instanceof org.apache.ant.antcore.execution.ExecutionContext)) {
            throw new org.apache.ant.common.util.ExecutionException("The Task was not configured with an" + " appropriate context");
        }
        org.apache.ant.antcore.execution.ExecutionContext execContext = ((org.apache.ant.antcore.execution.ExecutionContext) (context));
        frame.getEventSupport().fireTaskStarted(task);
        java.lang.Throwable failureCause = null;
        try {
            java.lang.ClassLoader currentLoader = org.apache.ant.init.LoaderUtils.setContextLoader(execContext.getClassLoader());
            task.execute();
            org.apache.ant.init.LoaderUtils.setContextLoader(currentLoader);
        } catch (org.apache.ant.common.util.ExecutionException e) {
            failureCause = e;
            throw e;
        } catch (java.lang.Throwable e) {
            org.apache.ant.common.util.ExecutionException ee = new org.apache.ant.common.util.ExecutionException(e);
            failureCause = ee;
            throw ee;
        } finally {
            frame.getEventSupport().fireTaskFinished(task, failureCause);
        }
    }

    /**
     * Get the base directory for this execution of this frame
     *
     * @return the base directory
     */
    public java.io.File getBaseDir() {
        return frame.getBaseDir();
    }

    /**
     * get the name of the project associated with this execution.
     *
     * @return the project's name
     */
    public java.lang.String getProjectName() {
        return frame.getProjectName();
    }

    /**
     * Gets the Frame for a subbuild based on the key
     *
     * @param key
     * 		Description of the Parameter
     * @return the subbuild's Frame
     * @exception ExecutionException
     * 		if the build cannot be found.
     */
    private org.apache.ant.antcore.execution.Frame getSubbuildFrame(java.lang.Object key) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame subFrame = ((org.apache.ant.antcore.execution.Frame) (subBuilds.get(key)));
        if (subFrame == null) {
            throw new org.apache.ant.common.util.ExecutionException("Could not find execution frame " + "for subbuild");
        }
        return subFrame;
    }

    /**
     * Handle subbuild output.
     *
     * @param subbuildKey
     * 		the core's key for managing the subbuild.
     * @param line
     * 		the content produce by the current thread.
     * @param isErr
     * 		true if this content is from the thread's error stream.
     * @exception ExecutionException
     * 		if the subbuild cannot be found.
     */
    public void handleBuildOutput(java.lang.Object subbuildKey, java.lang.String line, boolean isErr) throws org.apache.ant.common.util.ExecutionException {
        getSubbuildFrame(subbuildKey).threadOutput(line, isErr);
    }

    /**
     * Run a build which have been previously setup
     *
     * @param targets
     * 		A list of targets to be run
     * @param key
     * 		Description of the Parameter
     * @exception ExecutionException
     * 		if the build cannot be run
     */
    public void runBuild(java.lang.Object key, java.util.List targets) throws org.apache.ant.common.util.ExecutionException {
        getSubbuildFrame(key).runBuild(targets);
        subBuilds.remove(key);
    }

    /**
     * Parse an XML file into a build model.
     *
     * @param xmlBuildFile
     * 		The file containing the XML build description.
     * @return A Project model for the build.
     * @exception ExecutionException
     * 		if the build cannot be parsed
     */
    public org.apache.ant.common.model.Project parseXMLBuildFile(java.io.File xmlBuildFile) throws org.apache.ant.common.util.ExecutionException {
        try {
            // Parse the build file into a project
            org.apache.ant.antcore.modelparser.XMLProjectParser parser = new org.apache.ant.antcore.modelparser.XMLProjectParser();
            return parser.parseBuildFile(org.apache.ant.init.InitUtils.getFileURL(xmlBuildFile));
        } catch (java.net.MalformedURLException e) {
            throw new org.apache.ant.common.util.ExecutionException(e);
        } catch (org.apache.ant.antcore.xml.XMLParseException e) {
            throw new org.apache.ant.common.util.ExecutionException(e);
        }
    }

    /**
     * Create a project reference.
     *
     * @param referenceName
     * 		the name under which the project will be
     * 		referenced.
     * @param model
     * 		the project model.
     * @exception ExecutionException
     * 		if the project cannot be referenced.
     */
    public void createProjectReference(java.lang.String referenceName, org.apache.ant.common.model.Project model) throws org.apache.ant.common.util.ExecutionException {
        frame.createProjectReference(referenceName, model);
    }

    /**
     * Setup a sub-build.
     *
     * @param model
     * 		the project model to be used for the build
     * @param properties
     * 		the initiali properties to be used in the build
     * @return Description of the Return Value
     * @exception ExecutionException
     * 		if the subbuild cannot be run
     */
    public java.lang.Object setupBuild(org.apache.ant.common.model.Project model, java.util.Map properties) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame newFrame = frame.createFrame(model);
        newFrame.setInitialProperties(properties);
        java.lang.Object key = new java.lang.Object();
        subBuilds.put(key, newFrame);
        return key;
    }

    /**
     * Setup a sub-build using the current frame's project model
     *
     * @param properties
     * 		the initiali properties to be used in the build
     * @return Description of the Return Value
     * @exception ExecutionException
     * 		if the subbuild cannot be run
     */
    public java.lang.Object setupBuild(java.util.Map properties) throws org.apache.ant.common.util.ExecutionException {
        return setupBuild(frame.getProject(), properties);
    }
}