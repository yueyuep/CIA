/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
import java.io.*;
/**
 * This class represents an error within Ant. The end
 *  user should not be able to trigger this exception under
 *  normal circumstances. The BuildException class should
 *  be used instead to indicate that a compile or some other task
 *  has failed.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class AntException extends java.lang.RuntimeException {
    private java.lang.Throwable cause;

    public AntException(java.lang.String msg) {
        super(msg);
    }

    public AntException(java.lang.String msg, java.lang.Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public java.lang.Throwable getCause() {
        return cause;
    }

    public void printStackTrace() {
        printStackTrace(java.lang.System.err);
    }

    public void printStackTrace(java.io.PrintStream out) {
        super.printStackTrace(out);
        if (cause != null) {
            cause.printStackTrace(out);
        }
    }

    public void printStackTrace(java.io.PrintWriter out) {
        super.printStackTrace(out);
        if (cause != null) {
            cause.printStackTrace(out);
        }
    }
}