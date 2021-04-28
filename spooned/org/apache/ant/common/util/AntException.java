/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.ant.common.util;
/**
 * An AntException indicates some exceptional case has been encountered in
 * the processing of Ant. AntExceptions may accept a Throwable as a cause
 * allowing exceptions to be nested
 *
 * @author Conor MacNeill
 * @created 15 January 2002
 */
public abstract class AntException extends java.lang.Exception {
    /**
     * Exception that might have caused this one.
     */
    private java.lang.Throwable cause = null;

    /**
     * The location of the element which is associated with this exception
     * if known.
     */
    private org.apache.ant.common.util.Location location = Location.UNKNOWN_LOCATION;

    /**
     * Constructs an exception with the given descriptive message.
     *
     * @param msg
     * 		Description of or information about the exception.
     */
    public AntException(java.lang.String msg) {
        super(msg);
    }

    /**
     * Constructs an exception with the given message and exception as a
     * root cause.
     *
     * @param msg
     * 		Description of or information about the exception.
     * @param cause
     * 		Throwable that might have cause this one.
     */
    public AntException(java.lang.String msg, java.lang.Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    /**
     * Constructs an exception with the given message and exception as a
     * root cause and a location in a file.
     *
     * @param msg
     * 		Description of or information about the exception.
     * @param cause
     * 		Exception that might have cause this one.
     * @param location
     * 		Location in the project file where the error occured.
     */
    public AntException(java.lang.String msg, java.lang.Throwable cause, org.apache.ant.common.util.Location location) {
        this(msg, cause);
        setLocation(location, true);
    }

    /**
     * Constructs an exception with the given exception as a root cause.
     *
     * @param cause
     * 		Exception that might have caused this one.
     */
    public AntException(java.lang.Throwable cause) {
        super(cause.getMessage() == null ? cause.getClass().getName() : cause.getMessage());
        this.cause = cause;
    }

    /**
     * Constructs an exception with the given descriptive message and a
     * location in a file.
     *
     * @param msg
     * 		Description of or information about the exception.
     * @param location
     * 		Location in the project file where the error occured.
     */
    public AntException(java.lang.String msg, org.apache.ant.common.util.Location location) {
        super(msg);
        setLocation(location, true);
    }

    /**
     * Constructs an exception with the given exception as a root cause and
     * a location in a file.
     *
     * @param cause
     * 		Exception that might have cause this one.
     * @param location
     * 		Location in the project file where the error occured.
     */
    public AntException(java.lang.Throwable cause, org.apache.ant.common.util.Location location) {
        this(cause);
        setLocation(location, true);
    }

    /**
     * Sets the file location where the error occured.
     *
     * @param newLocation
     * 		the new location value
     * @param override
     * 		true if the location should override any currently
     * 		set location
     */
    public void setLocation(org.apache.ant.common.util.Location newLocation, boolean override) {
        if (override || (location == Location.UNKNOWN_LOCATION)) {
            if (newLocation == null) {
                this.location = Location.UNKNOWN_LOCATION;
            } else {
                this.location = newLocation;
            }
        }
    }

    /**
     * Returns the nested exception.
     *
     * @return the underlying exception
     */
    public java.lang.Throwable getCause() {
        return cause;
    }

    /**
     * Returns the file location where the error occured.
     *
     * @return the location value
     */
    public org.apache.ant.common.util.Location getLocation() {
        return location;
    }

    /**
     * Print the stack trace to System.err
     */
    public void printStackTrace() {
        printStackTrace(java.lang.System.err);
    }

    /**
     * Print the stack trace to the given PrintStream
     *
     * @param ps
     * 		the PrintStream onto which the stack trace of this
     * 		exception is to be printed
     */
    public void printStackTrace(java.io.PrintStream ps) {
        synchronized(ps) {
            super.printStackTrace(ps);
            if (cause != null) {
                ps.println("--- Nested Exception ---");
                cause.printStackTrace(ps);
            }
        }
    }

    /**
     * Print the stack trace to the given PrintWriter
     *
     * @param pw
     * 		the PrintWriter onto which the stack trace of this
     * 		exception is to be printed
     */
    public void printStackTrace(java.io.PrintWriter pw) {
        synchronized(pw) {
            super.printStackTrace(pw);
            if (cause != null) {
                pw.println("--- Nested Exception ---");
                cause.printStackTrace(pw);
            }
        }
    }
}