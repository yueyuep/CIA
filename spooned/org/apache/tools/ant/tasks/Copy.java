/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.tasks;
import java.io.*;
public class Copy extends Task {
    private java.lang.String src;

    private java.lang.String dest;

    public void execute() throws org.apache.tools.ant.tasks.BuildException {
        try {
            java.io.FileInputStream in = new java.io.FileInputStream(src);
            java.io.FileOutputStream out = new java.io.FileOutputStream(dest);
            byte[] buf = new byte[4096];
            int len = 0;
            while ((len = in.read(buf)) != (-1)) {
                out.write(buf, 0, len);
            } 
        } catch (java.io.FileNotFoundException exc) {
            throw new BuildException("File not found");
        } catch (java.io.IOException exc) {
            throw new AntException("Error copying files", exc);
        }
    }

    public java.lang.String getSrc() {
        return src;
    }

    public void setSrc(java.lang.String src) {
        this.src = src;
    }

    public java.lang.String getDest() {
        return dest;
    }

    public void setDest(java.lang.String dest) {
        this.dest = dest;
    }
}