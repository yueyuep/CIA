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
package org.apache.ant.core.execution;
import java.util.*;
import org.apache.ant.core.model.*;
import org.apache.ant.core.support.*;
/**
 * The ExecutionContext interface provides a task or apsect instance with access to the
 * container-provided services. This is the only way to access the container.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class ExecutionContext {
    private org.apache.ant.core.execution.ExecutionFrame frame = null;

    private org.apache.ant.core.execution.BuildEventSupport eventSupport;

    private org.apache.ant.core.execution.BuildElement buildElement;

    public ExecutionContext(ExecutionFrame frame, BuildEventSupport eventSupport, BuildElement buildElement) {
        this.frame = frame;
        this.eventSupport = eventSupport;
        this.buildElement = buildElement;
    }

    /**
     * Log a mesage with the give priority.
     *
     * @param the
     * 		message to be logged.
     * @param msgLevel
     * 		the message priority at which this message is to be logged.
     */
    public void log(java.lang.String msg, int msgLevel) {
        eventSupport.fireMessageLogged(this, buildElement, msg, msgLevel);
    }

    public void setDataValue(java.lang.String name, java.lang.Object value) throws org.apache.ant.core.execution.ExecutionException {
        frame.setDataValue(name, value);
    }

    public java.lang.Object getDataValue(java.lang.String name) throws org.apache.ant.core.execution.ExecutionException {
        return frame.getDataValue(name);
    }

    /**
     * Replace ${} style constructions in the given value with the string value of
     * the corresponding data types.
     *
     * @param value
     * 		the string to be scanned for property references.
     */
    public java.lang.String replacePropertyRefs(java.lang.String value) throws org.apache.ant.core.execution.ExecutionException {
        return frame.replacePropertyRefs(value);
    }
}