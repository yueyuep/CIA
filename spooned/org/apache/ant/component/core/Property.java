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
import java.net.*;
import java.util.*;
import org.apache.ant.core.execution.*;
/**
 *
 */
public class Property extends AbstractTask {
    private java.lang.String name;

    private java.lang.String value;

    private java.net.URL file;

    private java.lang.String resource;

    // private Path classpath;
    private java.lang.String env;

    // private Reference ref = null;
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String getValue() {
        return value;
    }

    public void setFile(java.net.URL file) {
        this.file = file;
    }

    public java.net.URL getFile() {
        return file;
    }

    public void setLocation(java.io.File location) {
        setValue(location.getAbsolutePath());
    }

    // public void setRefid(Reference ref) {
    // this.ref = ref;
    // }
    // 
    // public Reference getRefid() {
    // return ref;
    // }
    // 
    public void setResource(java.lang.String resource) {
        this.resource = resource;
    }

    public java.lang.String getResource() {
        return resource;
    }

    public void setEnvironment(java.lang.String env) {
        this.env = env;
    }

    public java.lang.String getEnvironment() {
        return env;
    }

    // public void setClasspath(Path classpath) {
    // if (this.classpath == null) {
    // this.classpath = classpath;
    // } else {
    // this.classpath.append(classpath);
    // }
    // }
    // 
    // public Path createClasspath() {
    // if (this.classpath == null) {
    // this.classpath = new Path(project);
    // }
    // return this.classpath.createPath();
    // }
    // 
    // public void setClasspathRef(Reference r) {
    // createClasspath().setRefid(r);
    // }
    // 
    public void execute() throws org.apache.ant.component.core.ExecutionException {
        if ((name != null) && (value != null)) {
            getTaskContext().setDataValue(name, value);
        }
        if (file != null) {
            loadFile(file);
        }
        // if (resource != null) loadResource(resource);
        // 
        // if (env != null) loadEnvironment(env);
        // 
        // if ((name != null) && (ref != null)) {
        // Object obj = ref.getReferencedObject(getProject());
        // if (obj != null) {
        // addProperty(name, obj.toString());
        // }
        // }
    }

    protected void loadFile(java.net.URL url) throws org.apache.ant.component.core.ExecutionException {
        java.util.Properties props = new java.util.Properties();
        log("Loading " + url, BuildEvent.MSG_VERBOSE);
        try {
            java.io.InputStream stream = null;
            if (url.getProtocol().equals("file")) {
                java.io.File file = new java.io.File(url.getFile());
                if (file.exists()) {
                    stream = new java.io.FileInputStream(file);
                }
            } else {
                stream = url.openStream();
            }
            if (stream != null) {
                try {
                    props.load(stream);
                    resolveAllProperties(props);
                    addProperties(props);
                } finally {
                    stream.close();
                }
            }
        } catch (java.io.IOException e) {
            throw new ExecutionException("Unable to load property file: " + url, e);
        }
    }

    protected void addProperties(java.util.Properties properties) throws org.apache.ant.component.core.ExecutionException {
        for (java.util.Iterator i = properties.keySet().iterator(); i.hasNext();) {
            java.lang.String propertyName = ((java.lang.String) (i.next()));
            java.lang.String propertyValue = properties.getProperty(propertyName);
            getTaskContext().setDataValue(propertyName, getTaskContext().replacePropertyRefs(propertyValue));
        }
    }

    private void resolveAllProperties(java.util.Properties props) throws org.apache.ant.component.core.ExecutionException {
        for (java.util.Iterator propIterator = props.keySet().iterator(); propIterator.hasNext();) {
            java.lang.String name = ((java.lang.String) (propIterator.next()));
            java.lang.String value = props.getProperty(name);
            boolean resolved = false;
            while (!resolved) {
                java.util.List fragments = new java.util.ArrayList();
                java.util.List propertyRefs = new java.util.ArrayList();
                ExecutionFrame.parsePropertyString(value, fragments, propertyRefs);
                resolved = true;
                if (propertyRefs.size() != 0) {
                    java.lang.StringBuffer sb = new java.lang.StringBuffer();
                    java.util.Iterator i = fragments.iterator();
                    java.util.Iterator j = propertyRefs.iterator();
                    while (i.hasNext()) {
                        java.lang.String fragment = ((java.lang.String) (i.next()));
                        if (fragment == null) {
                            java.lang.String propertyName = ((java.lang.String) (j.next()));
                            if (propertyName.equals(name)) {
                                throw new ExecutionException(((("Property " + name) + " from ") + file) + " was circularly defined.");
                            }
                            if (props.containsKey(propertyName)) {
                                fragment = props.getProperty(propertyName);
                                resolved = false;
                            } else {
                                fragment = ("${" + propertyName) + "}";
                            }
                        }
                        sb.append(fragment);
                    } 
                    value = sb.toString();
                    props.put(name, value);
                }
            } 
        }
    }
}