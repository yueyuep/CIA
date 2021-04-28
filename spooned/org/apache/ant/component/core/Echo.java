/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
package org.apache.ant.component.core;
import java.io.*;
import org.apache.ant.core.execution.*;
import org.apache.ant.core.types.*;
/**
 * Echo
 *
 * @author costin@dnt.ro
 */
public class Echo extends AbstractTask {
    protected java.lang.String message = "";// required


    protected java.io.File file = null;

    protected boolean append = false;

    // by default, messages are always displayed
    protected int logLevel = BuildEvent.MSG_WARN;

    /**
     * Does the work.
     *
     * @throws ExecutionException
     * 		if someting goes wrong with the build
     */
    public void execute() throws org.apache.ant.component.core.ExecutionException {
        if (file == null) {
            log(message, logLevel);
        } else {
            java.io.FileWriter out = null;
            try {
                out = new java.io.FileWriter(file.getAbsolutePath(), append);
                out.write(message, 0, message.length());
            } catch (java.io.IOException ioe) {
                throw new ExecutionException(ioe);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (java.io.IOException ioex) {
                    }
                }
            }
        }
    }

    /**
     * Sets the message variable.
     *
     * @param msg
     * 		Sets the value for the message variable.
     */
    public void setMessage(java.lang.String msg) {
        this.message = msg;
    }

    /**
     * Sets the file attribute.
     */
    public void setFile(java.io.File file) {
        this.file = file;
    }

    /**
     * Shall we append to an existing file?
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * Set a multiline message.
     */
    public void addText(java.lang.String msg) {
        message += msg;
    }

    /**
     * Set the logging level to one of
     * <ul>
     *  <li>error</li>
     *  <li>warning</li>
     *  <li>info</li>
     *  <li>verbose</li>
     *  <li>debug</li>
     * <ul>
     * <p>The default is &quot;warning&quot; to ensure that messages are
     * displayed by default when using the -quiet command line option.</p>
     */
    public void setLevel(org.apache.ant.component.core.Echo.EchoLevel echoLevel) {
        java.lang.String option = echoLevel.getValue();
        if (option.equals("error")) {
            logLevel = BuildEvent.MSG_ERR;
        } else if (option.equals("warning")) {
            logLevel = BuildEvent.MSG_WARN;
        } else if (option.equals("info")) {
            logLevel = BuildEvent.MSG_INFO;
        } else if (option.equals("verbose")) {
            logLevel = BuildEvent.MSG_VERBOSE;
        } else {
            // must be "debug"
            logLevel = BuildEvent.MSG_DEBUG;
        }
    }

    public static class EchoLevel extends EnumeratedAttribute {
        public java.lang.String[] getValues() {
            return new java.lang.String[]{ "error", "warning", "info", "verbose", "debug" };
        }
    }
}