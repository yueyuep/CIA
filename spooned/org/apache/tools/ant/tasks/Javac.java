/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
public class Javac extends Task {
    private org.apache.tools.ant.tasks.Fileset[] fileset;

    private java.lang.String dest;

    private java.lang.String classpath;

    private java.lang.String compilerclass = null;

    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        if (compilerclass == null) {
            compilerclass = "com.sun.tools.javac.Main";
        }
        java.util.List argList = new java.util.ArrayList();
        argList.add("-d");
        argList.add(dest);
        if (classpath != null) {
            argList.add("-classpath");
            // Replace the project's path separator with the system's path separator
            argList.add(classpath.replace(getProject().getPathSeparator(), java.io.File.pathSeparatorChar));
        }
        for (int i = 0; i < fileset.length; i++) {
            fileset[i].getFiles(argList);
        }
        java.lang.String[] args = ((java.lang.String[]) (argList.toArray(new java.lang.String[argList.size()])));
        try {
            new java.io.File(dest).mkdirs();
            java.lang.Class type = getClass().getClassLoader().loadClass(compilerclass);
            java.lang.reflect.Method method = type.getMethod("main", new java.lang.Class[]{ args.getClass() });
            getWorkspace().info("Running javac...");
            method.invoke(null, new java.lang.Object[]{ args });
        } catch (java.lang.reflect.InvocationTargetException exc) {
            java.lang.Throwable cause = exc.getTargetException();
            if (cause instanceof ExitException) {
                if (((ExitException) (cause)).getStatus() != 0) {
                    throw new BuildException("Compile failed");
                }
            } else {
                throw new AntException("Error running compiler", exc);
            }
        } catch (java.lang.ClassNotFoundException exc) {
            throw new BuildException("Compiler class not found. Makes sure tools.jar is in your classpath");
        } catch (java.lang.IllegalAccessException exc) {
            throw new AntException("Unable to access compiler class", exc);
        } catch (java.lang.NoSuchMethodException exc) {
            throw new AntException("Unable to find main method on compiler class", exc);
        }
    }

    public java.lang.String getDest() {
        return dest;
    }

    public void setDest(java.lang.String dest) {
        this.dest = dest;
    }

    public java.lang.String getClasspath() {
        return classpath;
    }

    public void setClasspath(java.lang.String classpath) {
        this.classpath = classpath;
    }

    public org.apache.tools.ant.tasks.Fileset[] getFileset() {
        return fileset;
    }

    public void setFileset(Fileset[] fileset) {
        this.fileset = fileset;
    }
}