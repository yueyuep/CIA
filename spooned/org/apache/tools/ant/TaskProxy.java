/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
/**
 * This class stores the information needed to
 *  instantiate a task object. It basically consists of
 *  the task name and a TaskData object, which stores the
 *  values of the fields.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class TaskProxy {
    private org.apache.tools.ant.Target target;

    private java.lang.String name;

    private org.apache.tools.ant.TaskData data;

    private java.lang.String location;

    public TaskProxy(org.apache.tools.ant.Target target, java.lang.String name) {
        this.target = target;
        this.name = name;
        this.data = new org.apache.tools.ant.TaskData(this);
    }

    public org.apache.tools.ant.Target getTarget() {
        return target;
    }

    public java.lang.String getName() {
        return name;
    }

    public java.lang.String getLocation() {
        return location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    public org.apache.tools.ant.TaskData getData() {
        return data;
    }

    /**
     * Finds the class for this task name, and creates an
     *  instance of it using TaskData.createBean().
     */
    public org.apache.tools.ant.Task createTask() throws org.apache.tools.ant.BuildException {
        java.lang.Class type = target.getProject().getWorkspace().getTaskClass(name);
        return ((org.apache.tools.ant.Task) (data.createBean(type)));
    }
}