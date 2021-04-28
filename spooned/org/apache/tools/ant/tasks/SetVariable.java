/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
public class SetVariable extends Task {
    private java.lang.String name;

    private java.lang.String value;

    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        getProject().setVariable(name, value);
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String getValue() {
        return value;
    }
}