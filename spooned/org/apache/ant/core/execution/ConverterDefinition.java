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
/**
 * A Converter definition defines a class which will convert
 * a string into an instance of a particular type of class. Converters
 * are typically only needed when some context information is
 * required to perform the conversion.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class ConverterDefinition {
    /**
     * The URL of the library which defines this converter
     */
    private java.net.URL converterLibraryURL;

    /**
     * The converter's class name
     */
    private java.lang.String converterClassName;

    /**
     * The converted class returned by this converter
     */
    private java.lang.String targetClassName;

    /**
     * The converter class loaded from the loader on demand.
     */
    private java.lang.Class converterClass = null;

    /**
     * The class to which this converter converts.
     */
    private java.lang.Class targetClass = null;

    /**
     * The converters's class loader.
     */
    private java.lang.ClassLoader converterClassLoader;

    public ConverterDefinition(java.net.URL converterLibraryURL, java.lang.String converterClassName, java.lang.String targetClassName, java.lang.ClassLoader converterClassLoader) {
        this.converterLibraryURL = converterLibraryURL;
        this.converterClassName = converterClassName;
        this.targetClassName = targetClassName;
        this.converterClassLoader = converterClassLoader;
    }

    /**
     * Get the name of the class that this converter will return.
     */
    public java.lang.String getTargetClassName() {
        return targetClassName;
    }

    /**
     * Get the classname of the converter that is being defined.
     */
    public java.lang.String getConverterClassName() {
        return converterClassName;
    }

    /**
     * Get the URL where this converter was defined.
     *
     * @returns a URL of the lib defintion file
     */
    public java.net.URL getLibraryURL() {
        return converterLibraryURL;
    }

    /**
     * Get the converter class
     *
     * @return a class object for this converter
     */
    public synchronized java.lang.Class getConverterClass() throws java.lang.ClassNotFoundException {
        if (converterClass == null) {
            converterClass = java.lang.Class.forName(converterClassName, true, converterClassLoader);
        }
        return converterClass;
    }

    /**
     * Get the converter class
     *
     * @return a class object for this converter
     */
    public synchronized java.lang.Class getTargetClass() throws java.lang.ClassNotFoundException {
        if (targetClass == null) {
            targetClass = java.lang.Class.forName(targetClassName, true, converterClassLoader);
        }
        return targetClass;
    }
}