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
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.event.BuildEvent;
import org.apache.ant.common.event.BuildListener;
import org.apache.ant.common.event.MessageLevel;
import org.apache.ant.common.model.ModelElement;
import org.apache.ant.common.util.DemuxOutputReceiver;
import org.apache.ant.common.util.ExecutionException;
/**
 * BuildEventSupport is used by classes which which to send build events to
 * the BuildListeners
 *
 * @author Conor MacNeill
 * @created 15 January 2002
 */
public class BuildEventSupport implements org.apache.ant.common.util.DemuxOutputReceiver {
    /**
     * The listeners attached to the object which contains this support
     * object
     */
    private java.util.List listeners = new java.util.ArrayList();

    /**
     * Records the latest task to be executed on a thread (Thread to Task).
     */
    private java.util.Map threadTasks = new java.util.HashMap();

    /**
     * Gets the listeners of the BuildEventSupport
     *
     * @return the listeners value
     */
    public java.util.Iterator getListeners() {
        return listeners.iterator();
    }

    /**
     * Add a listener
     *
     * @param listener
     * 		the listener to be added
     */
    public void addBuildListener(org.apache.ant.common.event.BuildListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * 		the listener to be removed
     */
    public void removeBuildListener(org.apache.ant.common.event.BuildListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire a build started event
     *
     * @param element
     * 		the build element with which the event is associated
     */
    public void fireBuildStarted(org.apache.ant.common.model.ModelElement element) {
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(element, org.apache.ant.common.event.BuildEvent.BUILD_STARTED);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.buildStarted(event);
        }
    }

    /**
     * Fir a build finished event
     *
     * @param element
     * 		the build element with which the event is associated
     * @param cause
     * 		an exception if there was a failure in the build
     */
    public void fireBuildFinished(org.apache.ant.common.model.ModelElement element, java.lang.Throwable cause) {
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(element, org.apache.ant.common.event.BuildEvent.BUILD_FINISHED, cause);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.buildFinished(event);
        }
    }

    /**
     * fire a target started event
     *
     * @param element
     * 		the build element with which the event is associated
     */
    public void fireTargetStarted(org.apache.ant.common.model.ModelElement element) {
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(element, org.apache.ant.common.event.BuildEvent.TARGET_STARTED);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.targetStarted(event);
        }
    }

    /**
     * fire a target finished event
     *
     * @param element
     * 		the build element with which the event is associated
     * @param cause
     * 		an exception if there was a failure in the target's task
     */
    public void fireTargetFinished(org.apache.ant.common.model.ModelElement element, java.lang.Throwable cause) {
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(element, org.apache.ant.common.event.BuildEvent.TARGET_FINISHED, cause);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.targetFinished(event);
        }
    }

    /**
     * fire a task started event
     *
     * @param task
     * 		the task with which the event is associated
     */
    public void fireTaskStarted(org.apache.ant.common.antlib.Task task) {
        synchronized(this) {
            threadTasks.put(java.lang.Thread.currentThread(), task);
        }
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(task, org.apache.ant.common.event.BuildEvent.TASK_STARTED);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.taskStarted(event);
        }
    }

    /**
     * fire a task finished event
     *
     * @param task
     * 		the task with which the event is associated
     * @param cause
     * 		an exception if there was a failure in the task
     */
    public void fireTaskFinished(org.apache.ant.common.antlib.Task task, java.lang.Throwable cause) {
        java.lang.System.out.flush();
        java.lang.System.err.flush();
        synchronized(this) {
            threadTasks.remove(java.lang.Thread.currentThread());
        }
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(task, org.apache.ant.common.event.BuildEvent.TASK_FINISHED, cause);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.taskFinished(event);
        }
    }

    /**
     * Send a message event
     *
     * @param source
     * 		the build element with which the event is associated
     * @param message
     * 		the message to be sent
     * @param priority
     * 		the priority of the message
     */
    public void fireMessageLogged(java.lang.Object source, java.lang.String message, int priority) {
        org.apache.ant.common.event.BuildEvent event = new org.apache.ant.common.event.BuildEvent(source, message, priority);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (i.next()));
            listener.messageLogged(event);
        }
    }

    /**
     * Demultiplexes output so that each task receives the appropriate
     * messages. If the current thread is not currently executing a task,
     * the message is logged directly.
     *
     * @param line
     * 		Message to handle. Should not be <code>null</code>.
     * @param isError
     * 		Whether the text represents an error (<code>true</code>
     * 		) or information (<code>false</code>).
     */
    public void threadOutput(java.lang.String line, boolean isError) {
        org.apache.ant.common.antlib.Task task = ((org.apache.ant.common.antlib.Task) (threadTasks.get(java.lang.Thread.currentThread())));
        if (task != null) {
            try {
                if (isError) {
                    task.handleSystemErr(line);
                } else {
                    task.handleSystemOut(line);
                }
                return;
            } catch (org.apache.ant.common.util.ExecutionException e) {
                // ignore just log normally
            }
        }
        fireMessageLogged(this, line, isError ? org.apache.ant.common.event.MessageLevel.MSG_ERR : org.apache.ant.common.event.MessageLevel.MSG_INFO);
    }
}