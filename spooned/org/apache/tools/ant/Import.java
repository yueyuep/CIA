/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
/**
 * Represents an import statement from a project.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class Import {
    private org.apache.tools.ant.Project project;

    private java.lang.String name;

    private java.lang.String location;

    public Import(org.apache.tools.ant.Project project, java.lang.String name) {
        this.project = project;
        this.name = name;
        this.location = location;
    }

    public org.apache.tools.ant.Project getProject() {
        return project;
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
}