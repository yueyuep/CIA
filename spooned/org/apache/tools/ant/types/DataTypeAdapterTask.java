/* The Apache Software License, Version 1.1

Copyright (c) 2000-2001 The Apache Software Foundation.  All rights 
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
package org.apache.tools.ant.types;
/**
 * Use introspection to "adapt" a DataType to be accepted in a Task position
 *
 * @author j_a_fernandez@yahoo.com
 */
public class DataTypeAdapterTask extends Task implements RoleAdapter {
    java.lang.Object proxy;

    java.lang.String id = null;

    /**
     * Checks a class, whether it is suitable to be adapted.
     *
     * Throws a BuildException and logs as Project.MSG_ERR for
     * conditions, that will cause the task execution to fail.
     * Logs other suspicious conditions with Project.MSG_WARN.
     */
    public static void checkClass(final java.lang.Class typeClass, final Project project) {
        // Any class can be used as a data type
    }

    /**
     * Do the execution.
     */
    public void execute() throws org.apache.tools.ant.types.BuildException {
        if (id != null) {
            // Need to re-register this reference
            // The container has register the Adapter instead
            project.addReference(id, proxy);
        }
    }

    /**
     * Propagate configuration of Project
     */
    public void setProject(Project p) {
        super.setProject(p);
        // Check to see if the DataType has a setProject method to set
        if (proxy instanceof ProjectComponent) {
            ((ProjectComponent) (proxy)).setProject(p);
            return;
        }
        // This may not be needed
        // We are trying to set project even if is was not declared
        // just like TaskAdapter does for beans, this is not done
        // by the original code
        java.lang.reflect.Method setProjectM = null;
        try {
            java.lang.Class c = proxy.getClass();
            setProjectM = c.getMethod("setProject", new java.lang.Class[]{ org.apache.tools.ant.types.Project.class });
            if (setProjectM != null) {
                setProjectM.invoke(proxy, new java.lang.Object[]{ p });
            }
        } catch (java.lang.NoSuchMethodException e) {
            // ignore this if the class being used as a task does not have
            // a set project method.
        } catch (java.lang.Exception ex) {
            log("Error setting project in " + proxy.getClass(), Project.MSG_ERR);
            throw new BuildException(ex);
        }
    }

    /**
     * Set the target object class
     */
    public void setProxy(java.lang.Object o) {
        this.proxy = o;
    }

    public java.lang.Object getProxy() {
        return this.proxy;
    }

    public void setId(java.lang.String id) {
        log("Setting adapter id to: " + id, Project.MSG_DEBUG);
        this.id = id;
    }
}