/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
public class Echo extends Task {
    private java.lang.String message;

    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        java.lang.System.out.println(message);
    }

    public java.lang.String getMessage() {
        return message;
    }

    public void setMessage(java.lang.String message) {
        this.message = message;
    }
}