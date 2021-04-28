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
/**
 * Dispatch messages to appropriate listener methode based on event id.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class EventDispatcher {
    private static final java.util.HashMap eventMap = new java.util.HashMap(3);

    static {
        registerDefaults();
    }

    /**
     * the set of registered listeners
     */
    private java.util.ArrayList listeners = new java.util.ArrayList();

    /**
     * Add a new listener.
     *
     * @param listener
     * 		a listener that will receive events from the client.
     */
    public void addListener(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener) {
        listeners.add(listener);
    }

    public void removeListener(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener) {
        listeners.remove(listener);
    }

    /**
     * Process a message from the client and dispatch the
     * appropriate message to the listeners.
     */
    public void dispatchEvent(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
        final java.lang.Integer type = new java.lang.Integer(evt.getType());
        final org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction action = ((org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction) (org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.eventMap.get(type)));
        if (action == null) {
            return;
        }
        synchronized(listeners) {
            final int count = listeners.size();
            for (int i = 0; i < count; i++) {
                org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener = ((org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener) (listeners.get(i)));
                action.dispatch(listener, evt);
            }
        }
    }

    private static void registerDefaults() {
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.RUN_STARTED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.RunStartedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.RUN_ENDED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.RunEndedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.TEST_STARTED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.TestStartedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.TEST_ENDED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.TestEndedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.TEST_FAILURE, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.TestFailureAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.TEST_ERROR, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.TestErrorAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.SUITE_STARTED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.SuiteStartedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.SUITE_ENDED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.SuiteEndedAction());
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.registerAction(TestRunEvent.RUN_STOPPED, new org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.RunStoppedAction());
    }

    private static void registerAction(int id, org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction action) {
        org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.eventMap.put(new java.lang.Integer(id), action);
    }

    public interface EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt);
    }

    private static class RunStartedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onRunStarted(evt);
        }
    }

    private static class RunEndedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onRunEnded(evt);
        }
    }

    private static class TestStartedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onTestStarted(evt);
        }
    }

    private static class TestEndedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onTestEnded(evt);
        }
    }

    private static class TestFailureAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onTestFailure(evt);
        }
    }

    private static class TestErrorAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onTestError(evt);
        }
    }

    private static class SuiteStartedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onSuiteStarted(evt);
        }
    }

    private static class SuiteEndedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onSuiteEnded(evt);
        }
    }

    private static class RunStoppedAction implements org.apache.tools.ant.taskdefs.optional.rjunit.remote.EventDispatcher.EventAction {
        public void dispatch(org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunListener listener, org.apache.tools.ant.taskdefs.optional.rjunit.remote.TestRunEvent evt) {
            listener.onRunStopped(evt);
        }
    }
}