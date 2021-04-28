/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
public class Jar extends Task {
    private java.lang.String jarfile;

    private java.lang.String basedir;

    private java.lang.String manifest;

    public java.lang.String getJarfile() {
        return jarfile;
    }

    public void setJarfile(java.lang.String jarfile) {
        this.jarfile = jarfile;
    }

    public java.lang.String getBasedir() {
        return basedir;
    }

    public void setBasedir(java.lang.String basedir) {
        this.basedir = basedir;
    }

    public java.lang.String getManifest() {
        return manifest;
    }

    public void setManifest(java.lang.String manifest) {
        this.manifest = manifest;
    }

    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        java.io.File dir = new java.io.File(jarfile).getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        java.util.List argList = new java.util.ArrayList();
        if (manifest == null) {
            argList.add("-cf");
        } else {
            argList.add("-cmf");
            argList.add(manifest);
        }
        argList.add(jarfile);
        argList.add("-C");
        argList.add(basedir);
        argList.add(".");
        java.lang.String[] args = ((java.lang.String[]) (argList.toArray(new java.lang.String[argList.size()])));
        try {
            java.lang.Class type = getClass().getClassLoader().loadClass("sun.tools.jar.Main");
            java.lang.reflect.Method method = type.getMethod("main", new java.lang.Class[]{ args.getClass() });
            getWorkspace().info("Running jar...");
            method.invoke(null, new java.lang.Object[]{ args });
        } catch (java.lang.reflect.InvocationTargetException exc) {
            java.lang.Throwable cause = exc.getTargetException();
            if (cause instanceof ExitException) {
                if (((ExitException) (cause)).getStatus() != 0) {
                    throw new BuildException("Build failed");
                }
            } else {
                throw new AntException("Error running jar", exc);
            }
        } catch (java.lang.ClassNotFoundException exc) {
            throw new AntException("Jar class not found. Makes sure tools.jar is in your classpath");
        } catch (java.lang.Exception exc) {
            throw new AntException("Error running jar", exc);
        }
    }
}