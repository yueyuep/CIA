/* The Apache Software License, Version 1.1

Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
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

4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
package org.apache.ant.engine;
import java.util.*;
import org.apache.ant.AntException;
import org.apache.ant.tasks.Task;
/**
 * The engine that actually invokes each Task. In addition to specifying a Task
 * to execute, it may be desirable to specify the root Task that will define
 * an execution cycle.
 */
public class TaskEngineImpl implements TaskEngine {
    /**
     * Analagous to a call stack, but with Tasks.
     */
    protected java.util.Stack taskStack = new java.util.Stack();

    /**
     * As the task stack is built, a mirror representation will also be
     * contructed that will hold property values.
     */
    protected java.util.Stack propertyStack = new java.util.Stack();

    /**
     * Keeps track of AntEngineListeners. We don't have to use Vector because we
     * take care of synchronization on the add, remove, and iteration operations.
     */
    protected java.util.ArrayList listenerList = new java.util.ArrayList();

    private int engineLevel = 0;

    /**
     * Constructor is private so it cannot be instantiated. Users of this class
     * will get an instance by using the getTaskEngine() method. This will allow
     * us to have a simple Factory implementation. We may use a Singleton
     * implementation, or a collection pool. The choice is up to us.
     */
    private TaskEngineImpl() {
        super();
    }

    /**
     * Return a usable instance of a TaskEngine to the requestor. Nothing
     * sophisticated yet, simple doles out a new instance each time.
     */
    public static org.apache.ant.engine.TaskEngine getTaskEngine() {
        return new org.apache.ant.engine.TaskEngineImpl();
    }

    /**
     * Walk the list of Tasks backwards until the root is reached. Keep track of
     * the Tasks along the way in a Stack. Return null if the root Task is not a
     * parent of the provided Task.
     */
    protected java.util.Stack getTaskStack(org.apache.ant.tasks.Task root, org.apache.ant.tasks.Task task) {
        java.util.Stack stack = new java.util.Stack();
        while (task != null) {
            stack.push(task);
            if (task == root) {
                return stack;
            }
            task = task.getParent();
        } 
        return null;
    }

    /**
     * Returns the next Task to be executed from the taskStack. The task is not
     * removed from the Stack.
     */
    public org.apache.ant.tasks.Task getNextExecuteTask() {
        try {
            return ((org.apache.ant.tasks.Task) (taskStack.peek()));
        } catch (java.util.EmptyStackException esx) {
            return null;
        }
    }

    /**
     * If no root is specified, we will assume that the user wants to execute
     * the Task with no root. This is accomplished by using the Task parameter
     * as its own root.
     */
    public void execute(org.apache.ant.tasks.Task task) throws org.apache.ant.AntException {
        execute(task, task);
    }

    /**
     * This is the workhorse, however it has been made to be very simple. Given
     * the ability to specify a path between root and the target Task, we build
     * a trail of Tasks to connect the two. Next we execute each Task on the way
     * between the two Tasks. Once we arrive at the Task to execute, we execute
     * all of its chlidren.
     */
    public void execute(org.apache.ant.tasks.Task root, org.apache.ant.tasks.Task task) throws org.apache.ant.AntException {
        fireEngineStart();
        try {
            taskStack = getTaskStack(root, task);
            if (taskStack == null) {
                throw new org.apache.ant.AntException("The execution root Task is not an ancestor of the execution Task.");
            }
            // Pop thru the stack and execute each Task we come across.
            while (!taskStack.isEmpty()) {
                executeTask(taskStack);
            } 
        } finally {
            fireEngineFinish();
        }
    }

    /**
     * A recursive routine that allows all Tasks in the stack to be executed. At
     * the same time, the stack may grow to include new Tasks.
     */
    protected void executeTask(java.util.Stack taskStack) throws org.apache.ant.AntException {
        org.apache.ant.tasks.Task task = ((org.apache.ant.tasks.Task) (taskStack.pop()));
        fireTaskStart(task);
        try {
            // Add a new property holder for this task to the property stack. Note
            // that the parent of the new holder is the current stack head.
            if (task.isPropertyContainer()) {
                if (propertyStack.isEmpty()) {
                    propertyStack.push(new HierarchicalHashtable());
                } else {
                    propertyStack.push(new HierarchicalHashtable(((HierarchicalHashtable) (propertyStack.peek()))));
                }
            }
            // Allow Task to do whatever it may need to do before touching its
            // children.
            task.init(this);
            // Iterate the Task's children and execute any priority Tasks.
            org.apache.ant.tasks.Task[] tasks = task.getChildren();
            for (int i = 0, c = tasks.length; i < c; i++) {
                if (tasks[i].getExecutionMode() == org.apache.ant.tasks.Task.EXECUTION_MODE_PRIORITY) {
                    taskStack.push(tasks[i]);
                    executeTask(taskStack);
                }
            }
            // Allow the Task to validate.
            task.validate();
            // Finally, execute the Task.
            fireTaskExecute(task);
            task.execute(this);
            // We can discard the no londer needed property holder.
            if (task.isPropertyContainer()) {
                propertyStack.pop();
            }
        } catch (org.apache.ant.AntException ax) {
            fireTaskException(task, ax);
        } finally {
            fireTaskFinish(task);
        }
    }

    /**
     * Causes an AntEvent to be generated and fired to all listeners.
     */
    public void message(org.apache.ant.tasks.Task task, java.lang.String message) {
        fireTaskMessage(task, message);
    }

    // //////////////////////////////////////////////////////////////////////////
    // Listener Support                            //
    // //////////////////////////////////////////////////////////////////////////
    public synchronized void addAntEngineListener(AntEngineListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public synchronized void removeAntEngineListener(AntEngineListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }

    protected synchronized void fireEngineStart() {
        if ((engineLevel++) > 0)
            return;

        AntEvent e = new AntEvent(this);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).engineStart(e);
        }
    }

    protected synchronized void fireEngineFinish() {
        if ((--engineLevel) > 0)
            return;

        AntEvent e = new AntEvent(this);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).engineFinish(e);
        }
    }

    protected synchronized void fireTaskStart(org.apache.ant.tasks.Task task) {
        AntEvent e = new AntEvent(this, task);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).taskStart(e);
        }
    }

    protected synchronized void fireTaskExecute(org.apache.ant.tasks.Task task) {
        AntEvent e = new AntEvent(this, task);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).taskExecute(e);
        }
    }

    protected synchronized void fireTaskFinish(org.apache.ant.tasks.Task task) {
        AntEvent e = new AntEvent(this, task);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).taskFinish(e);
        }
    }

    protected synchronized void fireTaskMessage(org.apache.ant.tasks.Task task, java.lang.String message) {
        AntEvent e = new AntEvent(this, task);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).taskMessage(e, message);
        }
    }

    protected synchronized void fireTaskException(org.apache.ant.tasks.Task task, org.apache.ant.AntException exception) {
        AntEvent e = new AntEvent(this, task);
        for (int i = 0; i < listenerList.size(); i++) {
            ((AntEngineListener) (listenerList.get(i))).taskException(e, exception);
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // Property Support Methods                        //
    // //////////////////////////////////////////////////////////////////////////
    /**
     * This is the routine that will perform key substitution. Phrase will come
     * in as "src/${someparam}" and be converted to the appropriate "normalized"
     * string. I suppose while I'm doing this we should support phrases with
     * nested keys, such as "src/${build${token}}". Also, we should properly
     * handle cases where ${someparam} will evaluate to ${anotherparam}.
     * <p></p>
     * One thing that will be different from the Ant 1.2 mechanismoccurs when a
     * parameter value is not found. The substitution routine inserts it back in
     * the phrase unchanged. I have opted to insert a zero-length string
     * instead.
     * <p></p>
     * I should add a switch to the engine that will give the user the ability
     * to throw an exception if a key is not found. Pretty easy, except this
     * method is a strange place for an AntException to be thrown. Perhaps I
     * should use a RuntimeException instead...
     * <p></p>
     * A brief rundown on the logic here:
     *     I check for the first instances of a key prefix.
     *     If none found we return the phrase as is.
     *     If key prefix is found get location of next key prefix and suffix.
     *     If suffix is found first, we have found a key.
     *     If there is no suffix, we return the phrase.
     */
    static final java.lang.String KEY_PREFIX = "${";

    static final java.lang.String KEY_SUFFIX = "}";

    protected java.lang.String substitute(java.lang.String phrase) {
        java.lang.StringBuffer sb = new java.lang.StringBuffer(phrase);
        int startPoint = 0;
        while ((startPoint >= 0) && (startPoint < phrase.length())) {
            int pre1 = startPoint + phrase.substring(startPoint).indexOf(org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX);
            if (pre1 < 0)
                break;

            int suf1 = phrase.substring(pre1 + org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX.length()).indexOf(org.apache.ant.engine.TaskEngineImpl.KEY_SUFFIX);
            if (suf1 < 0)
                break;

            suf1 = (suf1 + pre1) + org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX.length();
            int pre2 = phrase.substring(pre1 + org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX.length()).indexOf(org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX);
            if (pre2 < 0) {
                pre2 = phrase.length() + 1;
            } else {
                pre2 = (pre2 + pre1) + org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX.length();
            }
            if (suf1 < pre2) {
                // we have found a token
                java.lang.String key = sb.substring(pre1 + org.apache.ant.engine.TaskEngineImpl.KEY_PREFIX.length(), suf1);
                sb.delete(pre1, suf1 + 1);
                java.lang.Object value = getPropertyValueNoSubstitution(key);
                if (value != null) {
                    sb.insert(pre1, value.toString());
                }
                return substitute(sb.toString());
            }
            startPoint = pre2;
        } 
        return sb.toString();
    }

    public java.util.List getPropertyNames() {
        if (propertyStack.isEmpty())
            return new java.util.ArrayList();

        HierarchicalHashtable hash = ((HierarchicalHashtable) (propertyStack.peek()));
        return hash.getPropertyNames();
    }

    public java.lang.Object getPropertyValue(java.lang.String name) {
        if (propertyStack.isEmpty())
            return null;

        HierarchicalHashtable hash = ((HierarchicalHashtable) (propertyStack.peek()));
        java.lang.Object result = hash.getPropertyValue(name);
        if (result instanceof java.lang.String) {
            return substitute(((java.lang.String) (result)));
        } else {
            return result;
        }
    }

    protected java.lang.Object getPropertyValueNoSubstitution(java.lang.String name) {
        if (propertyStack.isEmpty())
            return null;

        HierarchicalHashtable hash = ((HierarchicalHashtable) (propertyStack.peek()));
        return hash.getPropertyValue(name);
    }

    public void setPropertyValue(java.lang.String name, java.lang.Object value) {
        if (propertyStack.isEmpty())
            return;

        HierarchicalHashtable hash = ((HierarchicalHashtable) (propertyStack.peek()));
        hash.setPropertyValue(name, value);
    }

    public void removePropertyValue(java.lang.String name) {
        if (propertyStack.isEmpty())
            return;

        HierarchicalHashtable hash = ((HierarchicalHashtable) (propertyStack.peek()));
        hash.remove(name);
    }
}