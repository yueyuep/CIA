/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
import java.net.*;
/**
 * The only task that gets loaded by default. It can be used
 *  to dynamically load any other required tasks.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class Load extends Task {
    private java.lang.String name;

    private java.lang.String classname;

    public void execute() throws org.apache.tools.ant.BuildException {
        try {
            getWorkspace().debug("Loading " + name);
            java.lang.ClassLoader loader = new java.net.URLClassLoader(new java.net.URL[]{ getProject().getBase() }, getWorkspace().getClass().getClassLoader());
            getWorkspace().registerTask(name, loader.loadClass(classname));
        } catch (java.lang.ClassNotFoundException exc) {
            throw new BuildException(("Class \"" + classname) + "\" not found");
        }
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getClassname() {
        return classname;
    }

    public void setClassname(java.lang.String classname) {
        this.classname = classname;
    }
}