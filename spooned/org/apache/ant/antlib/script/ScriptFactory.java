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
import org.apache.ant.common.antlib.AntContext;
import org.apache.ant.common.antlib.StandardLibFactory;
import org.apache.ant.common.service.ComponentService;
import org.apache.ant.common.util.ExecutionException;
/**
 * The ScriptFactory class is a factory for the Scripting tasks. It stores
 * the scripts as they are defined
 *
 * @author Conor MacNeill
 * @created 11 February 2002
 */
public class ScriptFactory extends org.apache.ant.common.antlib.StandardLibFactory {
    /**
     * An inner class used to record information about defined scripts.
     *
     * @author Conor MacNeill
     * @created 11 February 2002
     */
    private static class ScriptInfo {
        /**
         * the scripting langauge to use
         */
        private java.lang.String language;

        /**
         * the script itself
         */
        private java.lang.String script;

        /**
         * Constructor for the ScriptInfo object
         *
         * @param language
         * 		the language the script is written in
         * @param script
         * 		the script
         */
        public ScriptInfo(java.lang.String language, java.lang.String script) {
            this.language = language;
            this.script = script;
        }

        /**
         * Gets the language of the Script
         *
         * @return the language value
         */
        public java.lang.String getLanguage() {
            return language;
        }

        /**
         * Gets the script.
         *
         * @return the script text
         */
        public java.lang.String getScript() {
            return script;
        }
    }

    /**
     * The core's Component Service instance
     */
    private org.apache.ant.common.service.ComponentService componentService;

    /**
     * the scripts that have been defined
     */
    private java.util.Map scripts = new java.util.HashMap();

    /**
     * Initialise the factory
     *
     * @param context
     * 		the factory's context
     * @exception ExecutionException
     * 		if the factory cannot be initialized
     */
    public void init(org.apache.ant.common.antlib.AntContext context) throws org.apache.ant.common.util.ExecutionException {
        super.init(context);
        componentService = ((org.apache.ant.common.service.ComponentService) (context.getCoreService(org.apache.ant.common.service.ComponentService.class)));
        try {
            java.lang.Class.forName("com.ibm.bsf.BSFManager");
        } catch (java.lang.ClassNotFoundException e) {
            throw new org.apache.ant.common.util.ExecutionException("The script Ant library requires " + "bsf.jar to be available");
        } catch (java.lang.NoClassDefFoundError e) {
            throw new org.apache.ant.common.util.ExecutionException((("The script Ant library requires " + "bsf.jar to be available. The class ") + e.getMessage()) + "appears to be missing");
        }
    }

    /**
     * Create an instance of the given component class
     *
     * @param componentClass
     * 		the class for which an instance is required
     * @param localName
     * 		the name within the library undeer which the task is
     * 		defined
     * @return an instance of the required class
     * @exception InstantiationException
     * 		if the class cannot be instantiated
     * @exception IllegalAccessException
     * 		if the instance cannot be accessed
     * @exception ExecutionException
     * 		if there is a problem creating the task
     */
    public java.lang.Object createComponent(java.lang.Class componentClass, java.lang.String localName) throws java.lang.InstantiationException, java.lang.IllegalAccessException, org.apache.ant.common.util.ExecutionException {
        java.lang.Object component = super.createComponent(componentClass, localName);
        if (component instanceof org.apache.ant.antlib.script.ScriptDef) {
            org.apache.ant.antlib.script.ScriptDef scriptDef = ((org.apache.ant.antlib.script.ScriptDef) (component));
            scriptDef.setFactory(this);
        } else if (component instanceof org.apache.ant.antlib.script.ScriptBase) {
            org.apache.ant.antlib.script.ScriptBase scriptBase = ((org.apache.ant.antlib.script.ScriptBase) (component));
            scriptBase.setFactory(this);
            scriptBase.setScriptName(localName);
        }
        return component;
    }

    /**
     * Get the script language of a script
     *
     * @param scriptName
     * 		the name the script is defined under
     * @return the script language name
     */
    protected java.lang.String getScriptLanguage(java.lang.String scriptName) {
        org.apache.ant.antlib.script.ScriptFactory.ScriptInfo scriptInfo = ((org.apache.ant.antlib.script.ScriptFactory.ScriptInfo) (scripts.get(scriptName)));
        return scriptInfo.getLanguage();
    }

    /**
     * Get a script.
     *
     * @param scriptName
     * 		the name the script is defined under
     * @return the script text
     */
    protected java.lang.String getScript(java.lang.String scriptName) {
        org.apache.ant.antlib.script.ScriptFactory.ScriptInfo scriptInfo = ((org.apache.ant.antlib.script.ScriptFactory.ScriptInfo) (scripts.get(scriptName)));
        return scriptInfo.getScript();
    }

    /**
     * Define a script
     *
     * @param name
     * 		the name the script is to be defined under
     * @param language
     * 		the language of the scripr
     * @param script
     * 		the script text
     * @exception ExecutionException
     * 		if the script cannot be defined
     */
    protected void defineScript(java.lang.String name, java.lang.String language, java.lang.String script) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antlib.script.ScriptFactory.ScriptInfo scriptDefinition = new org.apache.ant.antlib.script.ScriptFactory.ScriptInfo(language, script);
        scripts.put(name, scriptDefinition);
        componentService.taskdef(this, org.apache.ant.antlib.script.ScriptBase.class.getClassLoader(), name, org.apache.ant.antlib.script.ScriptBase.class.getName());
    }
}