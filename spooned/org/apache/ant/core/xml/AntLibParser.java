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
package org.apache.ant.core.xml;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.apache.ant.core.execution.*;
import org.apache.ant.core.support.*;
import org.xml.sax.*;
/**
 * Parses the TASK-INF/antlib.xml file of an ant library
 * component. An Ant library may contains tasks, apsects and
 * other ant plug in components
 */
public class AntLibParser {
    public static final java.lang.String TASK_ELEMENT = "taskdef";

    public static final java.lang.String CONVERTER_ELEMENT = "converter";

    public static final java.lang.String ASPECT_ELEMENT = "aspect";

    /**
     * The factory used to create SAX parsers.
     */
    private javax.xml.parsers.SAXParserFactory parserFactory;

    /**
     * Parse the library definition
     *
     * @param libSource
     * 		the URL from where the library XML is read.
     * @throws SAXParseException
     * 		if there is a problem parsing the task definitions
     */
    public org.apache.ant.core.xml.AntLibrary parseAntLibrary(java.net.URL libSource, java.lang.ClassLoader componentLoader) throws org.apache.ant.core.xml.ConfigException {
        try {
            parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser saxParser = parserFactory.newSAXParser();
            org.xml.sax.XMLReader xmlReader = saxParser.getXMLReader();
            org.apache.ant.core.xml.AntLibParser.AntLibRootHandler rootHandler = new org.apache.ant.core.xml.AntLibParser.AntLibRootHandler(libSource, xmlReader, componentLoader);
            saxParser.parse(libSource.toString(), rootHandler);
            return rootHandler.getAntLibrary();
        } catch (org.xml.sax.SAXParseException e) {
            throw new ConfigException(e.getMessage(), e, new Location(libSource.toString(), e.getLineNumber(), e.getColumnNumber()));
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new ConfigException("Unable to parse Ant library component", e, new Location(libSource.toString()));
        } catch (org.xml.sax.SAXException e) {
            throw new ConfigException("Unable to parse Ant library component", e, new Location(libSource.toString()));
        } catch (java.io.IOException e) {
            throw new ConfigException("Unable to parse Ant library component", e, new Location(libSource.toString()));
        }
    }

    /**
     * The root handler handles the antlib element. An ant lib may
     * contain a number of different types of elements
     * <ul>
     *    <li>taskdef</li>
     *    <li>aspect</li>
     *    <li>converter</li>
     * </ul>
     */
    private class AntLibRootHandler extends RootHandler {
        private static final int STATE_LOOKING_FOR_ROOT = 1;

        private static final int STATE_ROOT_SEEN = 2;

        private static final int STATE_FINISHED = 3;

        private int state = org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_LOOKING_FOR_ROOT;

        /**
         * The AntLibrary that will be defined by parsing the library's definition
         * file.
         */
        private org.apache.ant.core.xml.AntLibrary library = null;

        private java.lang.ClassLoader componentLoader = null;

        /**
         * Create an Ant Library Root Handler.
         *
         * @param taskdefSource
         * 		the URL from where the task definitions exist
         * @param reader
         * 		the XML parser.
         */
        public AntLibRootHandler(java.net.URL taskdefSource, org.xml.sax.XMLReader reader, java.lang.ClassLoader componentLoader) {
            super(taskdefSource, reader);
            this.componentLoader = componentLoader;
        }

        /**
         * Get the library which has been parsed.
         *
         * @return an AntLibary with the library definitions
         */
        public org.apache.ant.core.xml.AntLibrary getAntLibrary() {
            return library;
        }

        /**
         * Start a new element in the root. This must be a taskdefs element
         * All other elements are invalid.
         *
         * @param uri
         * 		The Namespace URI.
         * @param localName
         * 		The local name (without prefix).
         * @param qualifiedName
         * 		The qualified name (with prefix)
         * @param attributes
         * 		The attributes attached to the element.
         * @throws SAXParseException
         * 		if there is a parsing problem.
         */
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qualifiedName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            switch (state) {
                case org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_LOOKING_FOR_ROOT :
                    if (qualifiedName.equals("antlib")) {
                        state = org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_ROOT_SEEN;
                        library = new AntLibrary();
                    } else {
                        throw new org.xml.sax.SAXParseException((("An Ant library component must start with an " + "<antlib> element and not with <") + qualifiedName) + ">", getLocator());
                    }
                    break;
                case org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_ROOT_SEEN :
                    if (qualifiedName.equals(org.apache.ant.core.xml.AntLibParser.TASK_ELEMENT)) {
                        createTaskDef(attributes);
                    } else if (qualifiedName.equals(org.apache.ant.core.xml.AntLibParser.CONVERTER_ELEMENT)) {
                        createConverterDef(attributes);
                    } else if (qualifiedName.equals(org.apache.ant.core.xml.AntLibParser.ASPECT_ELEMENT)) {
                        createAspectHandler(attributes);
                    } else {
                        throw new org.xml.sax.SAXParseException(("Unrecognized element <" + qualifiedName) + "> in Ant library definition", getLocator());
                    }
                    break;
            }
        }

        public void createTaskDef(org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            java.util.Set validAttributes = new java.util.HashSet();
            validAttributes.add("name");
            validAttributes.add("classname");
            java.util.Map attributeValues = AttributeValidator.validateAttributes(org.apache.ant.core.xml.AntLibParser.TASK_ELEMENT, attributes, validAttributes, getLocator());
            java.lang.String taskName = ((java.lang.String) (attributeValues.get("name")));
            java.lang.String className = ((java.lang.String) (attributeValues.get("classname")));
            if (taskName == null) {
                throw new org.xml.sax.SAXParseException(("'name' attribute is required in a <" + org.apache.ant.core.xml.AntLibParser.TASK_ELEMENT) + "> element", getLocator());
            }
            if (className == null) {
                throw new org.xml.sax.SAXParseException((("'classname' attribute is required in a " + "<") + org.apache.ant.core.xml.AntLibParser.TASK_ELEMENT) + "> element", getLocator());
            }
            TaskDefinition taskdef = new TaskDefinition(getSourceURL(), taskName, className, componentLoader);
            library.addTaskDefinition(taskdef);
        }

        public void createConverterDef(org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            java.util.Set validAttributes = new java.util.HashSet();
            validAttributes.add("target");
            validAttributes.add("classname");
            java.util.Map attributeValues = AttributeValidator.validateAttributes(org.apache.ant.core.xml.AntLibParser.CONVERTER_ELEMENT, attributes, validAttributes, getLocator());
            java.lang.String targetClassName = ((java.lang.String) (attributeValues.get("target")));
            java.lang.String className = ((java.lang.String) (attributeValues.get("classname")));
            if (targetClassName == null) {
                throw new org.xml.sax.SAXParseException(("'target' attribute is required in a <" + org.apache.ant.core.xml.AntLibParser.CONVERTER_ELEMENT) + "> element", getLocator());
            }
            if (className == null) {
                throw new org.xml.sax.SAXParseException((("'classname' attribute is required in a " + "<") + org.apache.ant.core.xml.AntLibParser.CONVERTER_ELEMENT) + "> element", getLocator());
            }
            ConverterDefinition converterDef = new ConverterDefinition(getSourceURL(), className, targetClassName, componentLoader);
            library.addConverterDefinition(converterDef);
        }

        public void createAspectHandler(org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            java.util.Set validAttributes = new java.util.HashSet();
            validAttributes.add("prefix");
            validAttributes.add("classname");
            java.util.Map attributeValues = AttributeValidator.validateAttributes(org.apache.ant.core.xml.AntLibParser.ASPECT_ELEMENT, attributes, validAttributes, getLocator());
            java.lang.String aspectPrefix = ((java.lang.String) (attributeValues.get("prefix")));
            java.lang.String aspectClassname = ((java.lang.String) (attributeValues.get("classname")));
            if (aspectPrefix == null) {
                throw new org.xml.sax.SAXParseException(("'prefix' attribute is required in a <" + org.apache.ant.core.xml.AntLibParser.ASPECT_ELEMENT) + "> element", getLocator());
            }
            if (aspectClassname == null) {
                throw new org.xml.sax.SAXParseException((("'classname' attribute is required in a " + "<") + org.apache.ant.core.xml.AntLibParser.ASPECT_ELEMENT) + "> element", getLocator());
            }
            AspectDefinition aspectDef = new AspectDefinition(getSourceURL(), aspectPrefix, aspectClassname, componentLoader);
            library.addAspectDefinition(aspectDef);
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) {
            if ((state == org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_ROOT_SEEN) && qName.equals("antlib")) {
                state = org.apache.ant.core.xml.AntLibParser.AntLibRootHandler.STATE_FINISHED;
            }
        }
    }
}