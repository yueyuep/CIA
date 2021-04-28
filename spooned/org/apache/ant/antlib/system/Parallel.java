/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.ant.antlib.system;
import org.apache.ant.common.antlib.AbstractTask;
import org.apache.ant.common.antlib.AntContext;
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.antlib.TaskContainer;
import org.apache.ant.common.service.ExecService;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.common.util.Location;
/**
 * Implements a multi threaded task execution.
 *
 * @author Thomas Christen <a href="mailto:chr@active.ch">chr@active.ch</a>
 * @author Conor MacNeill
 * @created 27 February 2002
 */
public class Parallel extends org.apache.ant.common.antlib.AbstractTask implements org.apache.ant.common.antlib.TaskContainer {
    /**
     * Class which stores information about the thread to which each task is
     * associated
     *
     * @author Conor MacNeill
     * @created 27 February 2002
     */
    private class TaskThread extends java.lang.Thread {
        /**
         * The exception thrown, if any, by the task running in this thread
         */
        private java.lang.Throwable exception;

        /**
         * The task running is this thread
         */
        private org.apache.ant.common.antlib.Task task;

        /**
         * An identifier for the thread
         */
        private int taskNumber;

        /**
         * Construct a new TaskThread
         *
         * @param task
         * 		the Task to be executed in a seperate thread
         * @param taskNumber
         * 		the thread's identifier
         */
        TaskThread(int taskNumber, org.apache.ant.common.antlib.Task task) {
            this.task = task;
            this.taskNumber = taskNumber;
        }

        /**
         * Get the exception thrown by the task, if any.
         *
         * @return the Throwable instance thrown by the task or null if
        nothing was thrown.
         */
        public java.lang.Throwable getException() {
            return exception;
        }

        /**
         * Executes the task within a thread and takes care about Exceptions
         * raised within the task.
         */
        public void run() {
            try {
                org.apache.ant.common.antlib.AntContext context = getAntContext();
                org.apache.ant.common.service.ExecService execService = ((org.apache.ant.common.service.ExecService) (context.getCoreService(org.apache.ant.common.service.ExecService.class)));
                execService.executeTask(task);
            } catch (java.lang.Throwable t) {
                exception = t;
            }
        }
    }

    /**
     * Collection holding the nested tasks
     */
    private java.util.List nestedTasks = new java.util.ArrayList();

    /**
     * Add a nested task to execute parallel (asynchron).
     *
     * @param nestedTask
     * 		Nested task to be executed in parallel
     */
    public void addNestedTask(org.apache.ant.common.antlib.Task nestedTask) {
        nestedTasks.add(nestedTask);
    }

    /**
     * Block execution until the specified time or for a specified amount of
     * milliseconds and if defined, execute the wait status.
     *
     * @exception ExecutionException
     * 		if any of the nested tasks throws an
     * 		exception
     */
    public void execute() throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antlib.system.Parallel.TaskThread[] threads = new org.apache.ant.antlib.system.Parallel.TaskThread[nestedTasks.size()];
        int threadNumber = 0;
        for (java.util.Iterator i = nestedTasks.iterator(); i.hasNext(); threadNumber++) {
            org.apache.ant.common.antlib.Task nestedTask = ((org.apache.ant.common.antlib.Task) (i.next()));
            threads[threadNumber] = new org.apache.ant.antlib.system.Parallel.TaskThread(threadNumber, nestedTask);
        }
        // now start all threads
        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
        }
        // now join to all the threads
        for (int i = 0; i < threads.length; ++i) {
            try {
                threads[i].join();
            } catch (java.lang.InterruptedException ie) {
                // who would interrupt me at a time like this?
            }
        }
        // now did any of the threads throw an exception
        java.lang.StringBuffer exceptionMessage = new java.lang.StringBuffer();
        java.lang.String lSep = java.lang.System.getProperty("line.separator");
        int numExceptions = 0;
        java.lang.Throwable firstException = null;
        org.apache.ant.common.util.Location firstLocation = org.apache.ant.common.util.Location.UNKNOWN_LOCATION;
        for (int i = 0; i < threads.length; ++i) {
            java.lang.Throwable t = threads[i].getException();
            if (t != null) {
                numExceptions++;
                if (firstException == null) {
                    firstException = t;
                }
                if ((t instanceof org.apache.ant.common.util.ExecutionException) && (firstLocation == org.apache.ant.common.util.Location.UNKNOWN_LOCATION)) {
                    firstLocation = ((org.apache.ant.common.util.ExecutionException) (t)).getLocation();
                }
                exceptionMessage.append(lSep);
                exceptionMessage.append(t.getMessage());
            }
        }
        if (numExceptions == 1) {
            if (firstException instanceof org.apache.ant.common.util.ExecutionException) {
                throw ((org.apache.ant.common.util.ExecutionException) (firstException));
            } else {
                throw new org.apache.ant.common.util.ExecutionException(firstException);
            }
        } else if (numExceptions > 1) {
            throw new org.apache.ant.common.util.ExecutionException(exceptionMessage.toString(), firstLocation);
        }
    }
}