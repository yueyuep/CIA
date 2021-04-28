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
package org.apache.tools.ant.xdoclet;
import com.sun.javadoc.*;
import org.apache.tools.ant.IntrospectionHelper;
import xdoclet.XDocletException;
import xdoclet.XDocletTagSupport;
import xdoclet.tags.AbstractProgramElementTagsHandler;
import xdoclet.util.TypeConversionUtil;
/**
 * Custom tag handler for XDoclet templates for Ant-specific processing.
 *
 * @author Erik Hatcher
 * @created February 17, 2002
 * @todo clean up logic so that all setters are gathered first (even
superclass) and sorted along wih them
 * @todo need to create better logic for finding proper setters
 * @todo add ifIsAntTask, among other convenience tags
 */
public class TaskTagsHandler extends xdoclet.XDocletTagSupport {
    /**
     * Default category for tasks without a category attribute.
     */
    public static final java.lang.String DEFAULT_CATEGORY = "other";

    /**
     * Iterates over all Ant tasks
     */
    public void forAllTasks(java.lang.String template, java.util.Properties attributes) throws xdoclet.XDocletException {
        ClassDoc[] classes = xdoclet.tags.AbstractProgramElementTagsHandler.getAllClasses();
        ClassDoc cur_class = null;
        for (int i = 0; i < classes.length; i++) {
            cur_class = classes[i];
            setCurrentClass(cur_class);
            if (TaskSubTask.isAntTask(cur_class)) {
                generate(template);
            }
        }
    }

    /**
     * Iterates over all Ant attributes.
     *
     * @param template
     * 		XDoclet template
     * @param attributes
     * 		Tag parameters
     * @exception XDocletException
     * 		Oops!
     */
    public void forAllAttributes(java.lang.String template, java.util.Properties attributes) throws xdoclet.XDocletException {
        // throw exception if not an Ant task
        ClassDoc cur_class = getCurrentClass();
        MethodDoc[] methods = getAttributeMethods(cur_class);
        for (int i = 0; i < methods.length; i++) {
            setCurrentMethod(methods[i]);
            generate(template);
        }
    }

    /**
     * Determines if there's at least one Ant attribute.
     *
     * @param template
     * 		XDoclet template
     * @param attributes
     * 		Tag parameters
     * @exception XDocletException
     * 		Oops!
     */
    public void ifHasAttributes(java.lang.String template, java.util.Properties attributes) throws xdoclet.XDocletException {
        // throw exception if not an Ant task
        ClassDoc cur_class = getCurrentClass();
        MethodDoc[] methods = getAttributeMethods(cur_class);
        if (methods.length > 0) {
            generate(template);
        }
    }

    /**
     * Iterates over all Ant nested element methods (addXXX, addConfiguredXXX, addXXX)
     *
     * @param template
     * 		XDoclet template
     * @param attributes
     * 		Tag parameters
     * @exception XDocletException
     * 		Oops!
     */
    public void forAllElements(java.lang.String template, java.util.Properties attributes) throws xdoclet.XDocletException {
        // throw exception if not an Ant task
        ClassDoc cur_class = getCurrentClass();
        MethodDoc[] methods = getElementMethods(cur_class);
        for (int i = 0; i < methods.length; i++) {
            setCurrentMethod(methods[i]);
            generate(template);
        }
    }

    /**
     * Provides the element name for the current method
     */
    public java.lang.String elementName() throws xdoclet.XDocletException {
        java.lang.String methodName = getCurrentMethod().name();
        java.lang.String elementName = "<not a valid element>";
        if (methodName.startsWith("addConfigured")) {
            elementName = methodName.substring(13, methodName.length());
        } else if (methodName.startsWith("add")) {
            elementName = methodName.substring(3, methodName.length());
        } else if (methodName.startsWith("create")) {
            elementName = methodName.substring(6, methodName.length());
        }
        return elementName.toLowerCase();
    }

    /**
     * Provides the element type for the current method
     */
    public java.lang.String elementType() throws xdoclet.XDocletException {
        ClassDoc classDoc = elementClassDoc();
        if (classDoc == null) {
            throw new xdoclet.XDocletException("Method is not an Ant element!");
        }
        return classDoc.qualifiedName();
    }

    /**
     * Provides the element type for the current method.  If the return type
     * is null, the first parameter is used.
     */
    private org.apache.tools.ant.xdoclet.ClassDoc elementClassDoc() throws xdoclet.XDocletException {
        ClassDoc classDoc = null;
        java.lang.String methodName = getCurrentMethod().name();
        if ((methodName.startsWith("addConfigured") || methodName.startsWith("add")) || methodName.startsWith("create")) {
            classDoc = getCurrentMethod().returnType().asClassDoc();
            if (classDoc == null) {
                Parameter[] params = getCurrentMethod().parameters();
                if (params.length == 1) {
                    classDoc = params[0].type().asClassDoc();
                }
            }
        }
        return classDoc;
    }

    /**
     * Provides the Ant task name.
     *
     * @see #getTaskName(ClassDoc)
     * @doc:tag type="content"
     */
    public java.lang.String taskName() throws xdoclet.XDocletException {
        return org.apache.tools.ant.xdoclet.TaskTagsHandler.getTaskName(getCurrentClass());
    }

    private static java.lang.String[] fluffPrefixes = new java.lang.String[]{ "set a", "set the", "sets a", "sets the" };

    public java.lang.String shortMethodDescription() throws xdoclet.XDocletException {
        Tag[] tags = getCurrentMethod().firstSentenceTags();
        java.lang.String desc = null;
        if ((tags != null) && (tags.length > 0)) {
            desc = tags[0].text();
        }
        if ((desc == null) || (desc.length() == 0)) {
            desc = "no description";
        }
        desc = desc.trim();
        java.lang.String descLower = desc.toLowerCase();
        for (int i = 0; i < org.apache.tools.ant.xdoclet.TaskTagsHandler.fluffPrefixes.length; i++) {
            java.lang.String prefix = org.apache.tools.ant.xdoclet.TaskTagsHandler.fluffPrefixes[i].toLowerCase() + " ";
            if (descLower.startsWith(prefix)) {
                desc = desc.substring(prefix.length());
                break;
            }
        }
        desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
        return desc;
    }

    /**
     * Provides the Ant task name.
     *
     * Order of rules:
     * <ol>
     *   <li>Value of @ant:task name="..."</li>
     *   <li>Lowercased classname with "Task" suffix removed</li>
     * </ol>
     */
    public static final java.lang.String getTaskName(ClassDoc clazz) throws xdoclet.XDocletException {
        // sheesh!  There should be a friendlier method than this!
        java.lang.String tagValue = getTagValue(clazz, "ant:task", "name", -1, null, null, null, null, null, false, XDocletTagSupport.FOR_CLASS, false);
        if (tagValue == null) {
            // use classname, but strip "Task" suffix if there
            tagValue = clazz.name();
            if (tagValue.endsWith("Task")) {
                tagValue = tagValue.substring(0, tagValue.indexOf("Task"));
            }
            tagValue = tagValue.toLowerCase();
        }
        return tagValue;
    }

    /**
     * Provides the Ant category name.
     *
     * @see #getCategoryName(ClassDoc)
     */
    public java.lang.String categoryName() throws xdoclet.XDocletException {
        return org.apache.tools.ant.xdoclet.TaskTagsHandler.getCategoryName(getCurrentClass());
    }

    /**
     * Provides the Ant category name as the Value of the category attribute,
     * <code>@ant:task&nbsp;category="..."</code>.
     */
    public static final java.lang.String getCategoryName(ClassDoc clazz) throws xdoclet.XDocletException {
        java.lang.String tagValue = getTagValue(clazz, "ant:task", "category", -1, null, null, null, null, null, false, XDocletTagSupport.FOR_CLASS, false);
        if (tagValue != null) {
            tagValue = tagValue.toLowerCase();
        } else {
            tagValue = org.apache.tools.ant.xdoclet.TaskTagsHandler.DEFAULT_CATEGORY;
        }
        return tagValue;
    }

    /**
     *
     *
     * @todo refactor to cache methods per class, and save some time
     */
    private org.apache.tools.ant.xdoclet.MethodDoc[] getAttributeMethods(ClassDoc cur_class) throws xdoclet.XDocletException {
        // Use Ant's own introspection mechanism to gather the
        // attributes this class supports
        org.apache.tools.ant.IntrospectionHelper is = null;
        try {
            is = org.apache.tools.ant.IntrospectionHelper.getHelper(java.lang.Class.forName(cur_class.qualifiedName()));
        } catch (java.lang.ClassNotFoundException e) {
            throw new xdoclet.XDocletException(e, e.getMessage());
        }
        // Regroup the attributes, since IntrospectionHelper
        // doesn't give us the whole data structure directly
        Enumeration = is.getAttributes();
        java.util.Properties attributeTypeMap = new java.util.Properties();
        // System.out.println(name + " = " + type.getName());
        // We need to return MethodDoc[] from this method
        // so get all methods from the current class
        // And now filter the MethodDoc's based
        // on what IntrospectionHelper says
        // inner classes are noted with $ in our map, but not
        // n the parameter type name.
        // System.out.println(methodName + " : " + attributeName + " : " + attributeType);
    }

    /**
     *
     *
     * @todo add checks for number parameters and appropriate return value
    check for proper exception too?
    method prefixes: add, create, addConfigured (but not addText)
     * @todo add DynamicConfigurator (this should be noted in the template, not dealt with here)
     */
    private org.apache.tools.ant.xdoclet.MethodDoc[] getElementMethods(ClassDoc cur_class) throws xdoclet.XDocletException {
        // Use Ant's own introspection mechanism to gather the
        // elements this class supports
        org.apache.tools.ant.IntrospectionHelper is = null;
        try {
            is = org.apache.tools.ant.IntrospectionHelper.getHelper(java.lang.Class.forName(cur_class.qualifiedName()));
        } catch (java.lang.ClassNotFoundException e) {
            throw new xdoclet.XDocletException(e.getMessage());
        }
        // Regroup the elements, since IntrospectionHelper
        // doesn't give us the whole data structure directly
        Enumeration = is.getNestedElements();
        java.util.Properties elementTypeMap = new java.util.Properties();
        // System.out.println(name + " = " + type.getName());
        // We need to return MethodDoc[] from this method
        // so get all methods from the current class
        // And now filter the MethodDoc's based
        // on what IntrospectionHelper says
        // Object create(), void add(Object), void addConfigured(Object)
        // true if addXXX or addConfiguredXXX
        // inner classes are noted with $ in our map, but not
        // the parameter type name.
    }

    /**
     * This is a slightly refactored (thank you IntelliJ) version of
     * some cut-and-paste from XDoclet code.  It sorts all methods together
     * rather than in batches of superclasses like XDoclet stuff does.
     */
    private org.apache.tools.ant.xdoclet.MethodDoc[] getMethods(ClassDoc cur_class) throws xdoclet.XDocletException {
        java.util.Map already = new java.util.HashMap();
        java.util.List methods = new java.util.ArrayList();
        while (cur_class != null) {
            // hardcoded to stop when it hits Task, nothing there
            // or above that needs to be processed
            if (cur_class.qualifiedName().equals("org.apache.tools.ant.Task") || cur_class.qualifiedName().equals("org.apache.tools.ant.taskdefs.MatchingTask")) {
                break;
            }
            java.util.List curMethods = java.util.Arrays.asList(cur_class.methods());
            for (int j = 0; j < curMethods.size(); j++) {
                MethodDoc method = ((MethodDoc) (curMethods.get(j)));
                if (isDeprecated(method)) {
                    continue;
                }
                if (shouldIgnore(method)) {
                    continue;
                }
                java.lang.String methodName = method.name();
                if (method.containingClass() == cur_class) {
                    if (already.containsKey(methodName) == false) {
                        already.put(methodName, method);
                        methods.add(method);
                    }
                }
            }
            cur_class = cur_class.superclass();
        } 
        return sortMethods(methods);
    }

    private boolean isDeprecated(MethodDoc method) {
        Tag[] tags = method.tags();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].name().equals("@deprecated")) {
                return true;
            }
        }
        return false;
    }

    /**
     * For now, lump attributes and elements together since we won't
     * have those tags on the same method.
     */
    private boolean shouldIgnore(MethodDoc method) throws xdoclet.XDocletException {
        java.lang.String value = getTagValue(method, "ant:attribute", "ignore", -1, null, null, null, null, null, false, XDocletTagSupport.FOR_METHOD, false);
        if ("true".equals(value)) {
            return true;
        }
        value = getTagValue(method, "ant:element", "ignore", -1, null, null, null, null, null, false, XDocletTagSupport.FOR_METHOD, false);
        if ("true".equals(value)) {
            return true;
        }
        return false;
    }

    private org.apache.tools.ant.xdoclet.MethodDoc[] sortMethods(java.util.List methods) {
        // sort methods
        java.util.Collections.sort(methods, new java.util.Comparator() {
            public int compare(java.lang.Object o1, java.lang.Object o2) {
                MethodDoc m1 = ((MethodDoc) (o1));
                MethodDoc m2 = ((MethodDoc) (o2));
                return m1.name().compareTo(m2.name());
            }

            public boolean equals(java.lang.Object obj) {
                // dumb
                return obj == this;
            }
        });
        return ((MethodDoc[]) (methods.toArray(new MethodDoc[0])));
    }
}