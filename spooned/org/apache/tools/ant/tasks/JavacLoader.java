/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
import java.io.*;
import java.net.*;
public class JavacLoader extends Task {
    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        try {
            java.net.URL toolsJar = findToolsJar();
            java.lang.ClassLoader loader = new java.net.URLClassLoader(new java.net.URL[]{ getProject().getBase(), toolsJar }, getWorkspace().getClass().getClassLoader());
            getWorkspace().registerTask("javac", loader.loadClass("org.apache.tools.ant.tasks.Javac"));
        } catch (java.net.MalformedURLException exc) {
            throw new AntException("Bad URL", exc);
        } catch (java.lang.ClassNotFoundException exc) {
            throw new BuildException("Class not found");
        }
    }

    private java.net.URL findToolsJar() throws java.net.MalformedURLException {
        // I assume this won't work everywhere...
        return new java.io.File(new java.io.File(java.lang.System.getProperty("java.home")), "../lib/tools.jar").toURL();
    }
}