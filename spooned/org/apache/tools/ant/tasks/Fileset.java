/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
import java.io.*;
import java.util.*;
public class Fileset {
    private java.lang.String src;

    public java.lang.String getSrc() {
        return src;
    }

    public void setSrc(java.lang.String src) {
        this.src = src;
    }

    public void getFiles(java.util.List results) throws org.apache.tools.ant.tasks.BuildException {
        if (src == null) {
            throw new BuildException("Missing property \"src\"", null);// LOCATION

        }
        java.io.File dir = new java.io.File(src);
        if (!dir.exists()) {
            throw new BuildException(src + " does not exist", null);// LOCATION!!!

        }
        getFiles(dir, results);
    }

    private void getFiles(java.io.File file, java.util.List results) {
        if (file.isDirectory()) {
            java.io.File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                getFiles(files[i], results);
            }
        } else if (file.getPath().endsWith(".java")) {
            results.add(file.getPath());
        }
    }
}