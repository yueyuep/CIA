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
package org.apache.ant.antlib.script;
import com.ibm.bsf.BSFEngine;
import com.ibm.bsf.BSFException;
import com.ibm.bsf.BSFManager;
import org.apache.ant.common.antlib.AbstractTask;
import org.apache.ant.common.antlib.AntContext;
import org.apache.ant.common.antlib.DeferredTask;
import org.apache.ant.common.util.ExecutionException;
/**
 * Task to import a component or components from a library
 *
 * @author Conor MacNeill
 * @created 27 January 2002
 */
public class ScriptBase extends org.apache.ant.common.antlib.AbstractTask implements org.apache.ant.common.antlib.DeferredTask {
    /**
     * The script factory instance to be used by this script
     */
    private org.apache.ant.antlib.script.ScriptFactory factory;

    /**
     * the name to which this script has been defined
     */
    private java.lang.String scriptName;

    /**
     * the attribute values set by the core
     */
    private java.util.Map attributes = new java.util.HashMap();

    /**
     * Any embedded set by the core
     */
    private java.lang.String text = "";

    /**
     * A list of the nested element names which have been configured
     */
    private java.util.List nestedElementNames = new java.util.ArrayList();

    /**
     * A list of the nested elements objects which have been configured
     */
    private java.util.List nestedElements = new java.util.ArrayList();

    /**
     * Set the given attribute
     *
     * @param name
     * 		the name of the attribute
     * @param attributeValue
     * 		the new attribute value
     */
    public void setAttribute(java.lang.String name, java.lang.String attributeValue) {
        attributes.put(name, attributeValue);
    }

    /**
     * Add a nested element
     *
     * @param nestedElementName
     * 		the nested element's name
     * @param value
     * 		the object being added
     */
    public void addElement(java.lang.String nestedElementName, java.lang.Object value) {
        nestedElementNames.add(nestedElementName);
        nestedElements.add(value);
    }

    /**
     * Execute the script
     *
     * @exception ExecutionException
     * 		if tghe script execution fails
     */
    public void execute() throws org.apache.ant.common.util.ExecutionException {
        java.lang.String language = factory.getScriptLanguage(scriptName);
        java.lang.String script = factory.getScript(scriptName);
        try {
            com.ibm.bsf.BSFManager manager = new com.ibm.bsf.BSFManager();
            manager.declareBean("self", this, getClass());
            manager.declareBean("context", getAntContext(), org.apache.ant.common.antlib.AntContext.class);
            // execute the script
            com.ibm.bsf.BSFEngine engine = manager.loadScriptingEngine(language);
            engine.exec(scriptName, 0, 0, script);
            for (java.util.Iterator i = attributes.keySet().iterator(); i.hasNext();) {
                java.lang.String attributeName = ((java.lang.String) (i.next()));
                java.lang.String value = ((java.lang.String) (attributes.get(attributeName)));
                java.lang.StringBuffer setter = new java.lang.StringBuffer(attributeName);
                setter.setCharAt(0, java.lang.Character.toUpperCase(setter.charAt(0)));
                engine.call(null, "set" + setter, new java.lang.Object[]{ value });
            }
            java.util.Iterator i = nestedElementNames.iterator();
            java.util.Iterator j = nestedElements.iterator();
            while (i.hasNext()) {
                java.lang.String nestedName = ((java.lang.String) (i.next()));
                java.lang.Object nestedElement = j.next();
                java.lang.StringBuffer adder = new java.lang.StringBuffer(nestedName);
                adder.setCharAt(0, java.lang.Character.toUpperCase(adder.charAt(0)));
                engine.call(null, "add" + adder, new java.lang.Object[]{ nestedElement });
            } 
            engine.call(null, "execute", new java.lang.Object[]{  });
        } catch (com.ibm.bsf.BSFException e) {
            java.lang.Throwable t = e;
            java.lang.Throwable te = e.getTargetException();
            if (te != null) {
                if (te instanceof org.apache.ant.common.util.ExecutionException) {
                    throw ((org.apache.ant.common.util.ExecutionException) (te));
                } else {
                    t = te;
                }
            }
            throw new org.apache.ant.common.util.ExecutionException(t);
        }
    }

    /**
     * Defines the script.
     *
     * @param text
     * 		Sets the value for the script variable.
     */
    public void addText(java.lang.String text) {
        this.text += text;
    }

    /**
     * Sets the factory of the ScriptBase
     *
     * @param factory
     * 		the script factory this script instance will use
     */
    protected void setFactory(org.apache.ant.antlib.script.ScriptFactory factory) {
        this.factory = factory;
    }

    /**
     * set the name of the script
     *
     * @param scriptName
     * 		the script's defined name
     */
    protected void setScriptName(java.lang.String scriptName) {
        this.scriptName = scriptName;
    }
}