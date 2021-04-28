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
import org.apache.ant.core.model.*;
import org.apache.ant.core.support.*;
import org.xml.sax.*;
/**
 * Parses a project from an XML source using a SAX Parser.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class XMLProjectParser {
    private java.util.Stack recursionCheck = new java.util.Stack();

    /**
     * The factory used to create SAX parsers.
     */
    private javax.xml.parsers.SAXParserFactory parserFactory;

    private static org.apache.ant.core.xml.Location getLocation(org.xml.sax.Locator locator) {
        return new Location(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
    }

    /**
     * Parse a build file form the given URL.
     *
     * @param buildSource
     * 		the URL from where the build source may be read.
     * @throws SAXParseException
     * 		if there is a problem parsing the build file.
     */
    public org.apache.ant.core.xml.Project parseBuildFile(java.net.URL buildSource) throws org.apache.ant.core.xml.ConfigException {
        try {
            parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser saxParser = parserFactory.newSAXParser();
            org.xml.sax.XMLReader xmlReader = saxParser.getXMLReader();
            recursionCheck.push(buildSource);
            org.apache.ant.core.xml.XMLProjectParser.ProjectRootHandler projectRootHandler = new org.apache.ant.core.xml.XMLProjectParser.ProjectRootHandler(buildSource, xmlReader);
            saxParser.parse(buildSource.toString(), projectRootHandler);
            return projectRootHandler.getProject();
        } catch (org.xml.sax.SAXParseException e) {
            throw new ConfigException(e.getMessage(), e, new Location(buildSource.toString(), e.getLineNumber(), e.getColumnNumber()));
        } catch (org.apache.ant.core.xml.XMLProjectParser.NoProjectReadException e) {
            throw new ConfigException("No project defined in build source", e, new Location(buildSource.toString()));
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new ConfigException("Unable to parse project: " + e.getMessage(), e, new Location(buildSource.toString()));
        } catch (org.xml.sax.SAXException e) {
            throw new ConfigException("Unable to parse project: " + e.getMessage(), e, new Location(buildSource.toString()));
        } catch (java.io.IOException e) {
            throw new ConfigException("Unable to parse project: " + e.getMessage(), e, new Location(buildSource.toString()));
        }
    }

    /**
     * The root handler handles the start of parsing. This element looks for the
     * root element which must be a project element. It then delegates handling of the
     * project element to a project handler from which it extracts the parsed project.
     */
    private class ProjectRootHandler extends RootHandler {
        /**
         * The project handler created to parse the project element.
         */
        org.apache.ant.core.xml.XMLProjectParser.ProjectHandler projectHandler;

        /**
         * Create a Root Handler.
         *
         * @param buildSource
         * 		the URL containing the build definition
         * @param reader
         * 		the XML parser.
         */
        public ProjectRootHandler(java.net.URL buildSource, org.xml.sax.XMLReader reader) {
            super(buildSource, reader);
        }

        /**
         * Start a new element in the root. This must be a project element
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
            if (qualifiedName.equals("project")) {
                projectHandler = new org.apache.ant.core.xml.XMLProjectParser.ProjectHandler(getXMLReader(), this, getLocator(), attributes, getSourceURL());
            } else {
                throw new org.xml.sax.SAXParseException(("Build file should start with a <project> element not <" + qualifiedName) + ">", getLocator());
            }
        }

        /**
         * Get the project that has been parsed from the element
         *
         * @return the project that has been parsed from the build osurce
         * @throws NoProjectReadException
         * 		thrown if no project was read in.
         */
        public org.apache.ant.core.xml.Project getProject() throws org.apache.ant.core.xml.XMLProjectParser.NoProjectReadException {
            if (projectHandler == null) {
                throw new org.apache.ant.core.xml.XMLProjectParser.NoProjectReadException();
            }
            return projectHandler.getProject();
        }
    }

    /**
     * Root Handler for include elements.
     *
     * Includes must contain either a project (which is being extended) or
     * a fragment element which contains the fragment to be included.
     */
    private class IncludeRootHandler extends RootHandler {
        /**
         * The project into which the fragment is to be included.
         */
        private org.apache.ant.core.xml.Project project;

        /**
         * Create an Include Root Handler.
         *
         * @param buildSource
         * 		the URL containing the fragment definition
         * @param reader
         * 		the XML parser.
         * @param project
         * 		the project into which the fragment's elements will be included.
         */
        public IncludeRootHandler(java.net.URL buildSource, org.xml.sax.XMLReader reader, Project project) {
            super(buildSource, reader);
            this.project = project;
        }

        /**
         * Start a new element in the include root. This must be a project element
         * or a fragment element. All other elements are invalid.
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
            if (qualifiedName.equals("project") || qualifiedName.equals("fragment")) {
                // if it is a fragment, it must have no attributes
                // any project attributes are ignored
                if (qualifiedName.equals("fragment") && (attributes.getLength() != 0)) {
                    throw new org.xml.sax.SAXParseException("<fragment> element may not have any attributes", getLocator());
                }
                new org.apache.ant.core.xml.XMLProjectParser.ProjectHandler(getXMLReader(), this, getLocator(), getSourceURL(), project);
            } else {
                throw new org.xml.sax.SAXParseException((("An included file should contain either a " + "<project> or <fragment> element and not a <") + qualifiedName) + "> element", getLocator());
            }
        }
    }

    /**
     * Element to parse the project element.
     *
     * The project handler creates a number of different handlers to which it
     * delegates processing of child elements.
     */
    private class ProjectHandler extends ElementHandler {
        /**
         * The project being parsed.
         */
        private org.apache.ant.core.xml.Project project;

        /**
         * The sourceURL for the current content being added to the project.
         */
        private java.net.URL sourceURL;

        /**
         * Create a ProjectHandler to read in a complete project.
         *
         * @param xmlReader
         * 		the XML parser being used to parse the project element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param attributes
         * 		the project element's attributes.
         * @param projectSource
         * 		the URL from which the XML source is being parsed.
         */
        public ProjectHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, org.xml.sax.Attributes attributes, java.net.URL projectSource) throws org.xml.sax.SAXParseException {
            super(xmlReader, parent, locator);
            this.sourceURL = projectSource;
            project = new Project(projectSource, org.apache.ant.core.xml.XMLProjectParser.getLocation(locator));
            java.lang.String base = null;
            java.lang.String defaultTarget = null;
            java.lang.String projectName = null;
            java.util.Map aspects = new java.util.HashMap();
            for (int i = 0; i < attributes.getLength(); ++i) {
                java.lang.String attributeName = attributes.getQName(i);
                java.lang.String attributeValue = attributes.getValue(i);
                if (attributeName.equals("base")) {
                    base = attributeValue;
                } else if (attributeName.equals("default")) {
                    defaultTarget = attributeValue;
                } else if (attributeName.equals("name")) {
                    projectName = attributeValue;
                } else if (attributeName.indexOf(":") != (-1)) {
                    // potential aspect attribute
                    aspects.put(attributeName, attributeValue);
                } else {
                    throw new org.xml.sax.SAXParseException((("The attribute '" + attributeName) + "' is not ") + "supported by the <project> element", getLocator());
                }
            }
            project.setDefaultTarget(defaultTarget);
            project.setBase(base);
            project.setName(projectName);
            project.setAspects(aspects);
        }

        /**
         * Create a Project handler for an included fragment. The elements
         * from the fragment are added to the given project.
         *
         * @param xmlReader
         * 		the XML parser being used to parse the project element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param includeSource
         * 		the URL from which the XML source is being included.
         * @param project
         * 		the project to which the included fragments elements are added.
         */
        public ProjectHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, java.net.URL includeSource, Project project) {
            super(xmlReader, parent, locator);
            this.sourceURL = includeSource;
            this.project = project;
        }

        /**
         * Start a new element in the project. Project currently handle the
         * following elements
         * <ul>
         *   <li>import</li>
         *   <li>include</li>
         *   <li>target</li>
         * </ul>
         *
         * Everything else is treated as a task.
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
            if (qualifiedName.equals("import")) {
                org.apache.ant.core.xml.XMLProjectParser.ImportHandler importHandler = new org.apache.ant.core.xml.XMLProjectParser.ImportHandler(getXMLReader(), this, getLocator(), attributes, sourceURL);
                try {
                    project.importProject(importHandler.getImportName(), importHandler.getImportedProject());
                } catch (ProjectModelException e) {
                    throw new org.xml.sax.SAXParseException(e.getMessage(), getLocator(), e);
                }
            } else if (qualifiedName.equals("include")) {
                org.apache.ant.core.xml.XMLProjectParser.IncludeHandler includeHandler = new org.apache.ant.core.xml.XMLProjectParser.IncludeHandler(getXMLReader(), this, getLocator(), attributes, sourceURL, project);
            } else if (qualifiedName.equals("target")) {
                org.apache.ant.core.xml.XMLProjectParser.TargetHandler targetHandler = new org.apache.ant.core.xml.XMLProjectParser.TargetHandler(getXMLReader(), this, getLocator(), attributes);
                try {
                    project.addTarget(targetHandler.getTarget());
                } catch (ProjectModelException e) {
                    throw new org.xml.sax.SAXParseException(e.getMessage(), getLocator(), e);
                }
            } else {
                // everything else is a task
                org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler taskElementHandler = new org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler(getXMLReader(), this, getLocator(), attributes, qualifiedName);
                project.addTask(taskElementHandler.getTaskElement());
            }
        }

        /**
         * Get the project that has been parsed from the XML source
         *
         * @return the project model of the parsed project.
         */
        public org.apache.ant.core.xml.Project getProject() {
            return project;
        }
    }

    /**
     * The import handler handles the importing of one project into another.
     *
     * The project to be imported is parsed with a new parser and then added to the
     * current project under the given import name
     */
    private class ImportHandler extends ElementHandler {
        /**
         * The attribute used to name the import.
         */
        public static final java.lang.String IMPORT_NAME_ATTR = "name";

        /**
         * The attribute name used to locate the project to be imported.
         */
        public static final java.lang.String IMPORT_SYSTEMID_ATTR = "project";

        /**
         * The project that has been imported.
         */
        private org.apache.ant.core.xml.Project importedProject;

        /**
         * The name under which the project is being imported.
         */
        private java.lang.String importName;

        /**
         * The systemId (URL) where the project is to be imported from.
         */
        private java.lang.String projectSystemId;

        /**
         * Create an import handler to import a project.
         *
         * @param xmlReader
         * 		the XML parser being used to parse the import element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param attributes
         * 		attributes of the import statement.
         * @param importingSource
         * 		the URL of the importing source.
         */
        public ImportHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, org.xml.sax.Attributes attributes, java.net.URL importingSource) throws org.xml.sax.SAXParseException {
            super(xmlReader, parent, locator);
            for (int i = 0; i < attributes.getLength(); ++i) {
                java.lang.String attributeName = attributes.getQName(i);
                java.lang.String attributeValue = attributes.getValue(i);
                if (attributeName.equals(org.apache.ant.core.xml.XMLProjectParser.ImportHandler.IMPORT_NAME_ATTR)) {
                    importName = attributeValue;
                } else if (attributeName.equals(org.apache.ant.core.xml.XMLProjectParser.ImportHandler.IMPORT_SYSTEMID_ATTR)) {
                    projectSystemId = attributeValue;
                } else {
                    throw new org.xml.sax.SAXParseException(("Attribute " + attributeName) + " is not allowed in an <import> element", getLocator());
                }
            }
            if (importName == null) {
                throw new org.xml.sax.SAXParseException(("Attribute " + org.apache.ant.core.xml.XMLProjectParser.ImportHandler.IMPORT_NAME_ATTR) + " is required in an <import> element", getLocator());
            }
            if (projectSystemId == null) {
                throw new org.xml.sax.SAXParseException(("Attribute " + org.apache.ant.core.xml.XMLProjectParser.ImportHandler.IMPORT_SYSTEMID_ATTR) + " is required in an <import> element", getLocator());
            }
            // create a new parser to read this project relative to the
            // project's URI
            try {
                java.net.URL importURL = new java.net.URL(importingSource, projectSystemId);
                javax.xml.parsers.SAXParser importSAXParser = parserFactory.newSAXParser();
                org.xml.sax.XMLReader importXMLReader = importSAXParser.getXMLReader();
                if (recursionCheck.contains(importURL)) {
                    throw new org.xml.sax.SAXParseException(("Circular import detected when importing '" + importURL) + "'", getLocator());
                }
                recursionCheck.push(importURL);
                org.apache.ant.core.xml.XMLProjectParser.ProjectRootHandler importRootHandler = new org.apache.ant.core.xml.XMLProjectParser.ProjectRootHandler(importURL, importXMLReader);
                importSAXParser.parse(importURL.toString(), importRootHandler);
                if (recursionCheck.pop() != importURL) {
                    throw new java.lang.RuntimeException("Failure to pop expected element off recursion stack");
                }
                importedProject = importRootHandler.getProject();
            } catch (org.xml.sax.SAXParseException e) {
                throw e;
            } catch (org.apache.ant.core.xml.XMLProjectParser.NoProjectReadException e) {
                throw new org.xml.sax.SAXParseException("No project was imported from " + projectSystemId, getLocator());
            } catch (java.net.MalformedURLException e) {
                throw new org.xml.sax.SAXParseException((("Unable to import project from " + projectSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                throw new org.xml.sax.SAXParseException((("Unable to parse project imported from " + projectSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (org.xml.sax.SAXException e) {
                throw new org.xml.sax.SAXParseException((("Unable to parse project imported from " + projectSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (java.io.IOException e) {
                throw new org.xml.sax.SAXParseException((("Error reading project imported from " + projectSystemId) + ": ") + e.getMessage(), getLocator());
            }
        }

        /**
         * Import does not support nested elements. This method will always throw an
         * exception
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
         * 		always.
         */
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qualifiedName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            // everything is a task
            throw new org.xml.sax.SAXParseException("<import> does not support nested elements", getLocator());
        }

        /**
         * Get the project imported.
         *
         * @return an imported Project.
         */
        public org.apache.ant.core.xml.Project getImportedProject() {
            return importedProject;
        }

        /**
         * Get the name under which the project is imported.
         *
         * @return the import name of the project
         */
        public java.lang.String getImportName() {
            return importName;
        }
    }

    /**
     * The include handler is used to read in included projects or
     * fragments into a project.
     */
    private class IncludeHandler extends ElementHandler {
        /**
         * The attribute name which identifies the fragment to be included
         */
        public static final java.lang.String INCLUDE_SYSTEMID_ATTR = "fragment";

        /**
         * The system id of the fragment to be included.
         */
        private java.lang.String includeSystemId;

        /**
         * Create an IncludeHandler to include an element into the
         * current project
         *
         * @param xmlReader
         * 		the XML parser being used to parse the include element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param attributes
         * 		attributes of the include statement.
         * @param includingSource
         * 		the URL of the including source.
         * @param project
         * 		the project into which the included elements are added.
         */
        public IncludeHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, org.xml.sax.Attributes attributes, java.net.URL includingSource, Project project) throws org.xml.sax.SAXParseException {
            super(xmlReader, parent, locator);
            for (int i = 0; i < attributes.getLength(); ++i) {
                java.lang.String attributeName = attributes.getQName(i);
                java.lang.String attributeValue = attributes.getValue(i);
                if (attributeName.equals(org.apache.ant.core.xml.XMLProjectParser.IncludeHandler.INCLUDE_SYSTEMID_ATTR)) {
                    includeSystemId = attributeValue;
                } else {
                    throw new org.xml.sax.SAXParseException(("Attribute " + attributeName) + " is not allowed in an <include> element", getLocator());
                }
            }
            if (includeSystemId == null) {
                throw new org.xml.sax.SAXParseException(("Attribute " + org.apache.ant.core.xml.XMLProjectParser.IncludeHandler.INCLUDE_SYSTEMID_ATTR) + " is required in an <include> element", getLocator());
            }
            // create a new parser to read this project relative to the
            // project's URI
            try {
                java.net.URL includeURL = new java.net.URL(includingSource, includeSystemId);
                javax.xml.parsers.SAXParser includeSAXParser = parserFactory.newSAXParser();
                org.xml.sax.XMLReader includeXMLReader = includeSAXParser.getXMLReader();
                if (recursionCheck.contains(includeURL)) {
                    throw new org.xml.sax.SAXParseException(("Circular include detected when including '" + includeURL) + "'", getLocator());
                }
                recursionCheck.push(includeURL);
                org.apache.ant.core.xml.XMLProjectParser.IncludeRootHandler includeRootHandler = new org.apache.ant.core.xml.XMLProjectParser.IncludeRootHandler(includeURL, includeXMLReader, project);
                includeSAXParser.parse(includeURL.toString(), includeRootHandler);
                if (recursionCheck.pop() != includeURL) {
                    throw new java.lang.RuntimeException("Failure to pop expected element off recursion stack");
                }
            } catch (org.xml.sax.SAXParseException e) {
                throw e;
            } catch (java.net.MalformedURLException e) {
                throw new org.xml.sax.SAXParseException((("Unable to include " + includeSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                throw new org.xml.sax.SAXParseException((("Unable to parse include " + includeSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (org.xml.sax.SAXException e) {
                throw new org.xml.sax.SAXParseException((("Unable to parse include " + includeSystemId) + ": ") + e.getMessage(), getLocator());
            } catch (java.io.IOException e) {
                throw new org.xml.sax.SAXParseException((("Error reading include " + includeSystemId) + ": ") + e.getMessage(), getLocator());
            }
        }

        /**
         * Include does not support nested elements. This method will always throw an
         * exception
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
         * 		always.
         */
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qualifiedName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            // everything is a task
            throw new org.xml.sax.SAXParseException("<include> does not support nested elements", getLocator());
        }
    }

    /**
     * A TargetHandler process the Target element.
     */
    private class TargetHandler extends ElementHandler {
        /**
         * The target being configured.
         */
        private org.apache.ant.core.xml.Target target;

        /**
         * Create a Target handler. Event element in a target is
         * considered to be a task
         *
         * @param xmlReader
         * 		the XML parser being used to parse the target element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param attributes
         * 		attributes of the target
         */
        public TargetHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            super(xmlReader, parent, locator);
            java.lang.String targetName = null;
            java.lang.String depends = null;
            java.util.Map aspects = new java.util.HashMap();
            for (int i = 0; i < attributes.getLength(); ++i) {
                java.lang.String attributeName = attributes.getQName(i);
                java.lang.String attributeValue = attributes.getValue(i);
                if (attributeName.equals("name")) {
                    targetName = attributeValue;
                } else if (attributeName.equals("depends")) {
                    depends = attributeValue;
                } else if (attributeName.indexOf(":") != (-1)) {
                    // potential aspect attribute
                    aspects.put(attributeName, attributeValue);
                } else {
                    throw new org.xml.sax.SAXParseException((("The attribute '" + attributeName) + "' is not ") + "supported by the <target> element", getLocator());
                }
            }
            if (targetName == null) {
                throw new org.xml.sax.SAXParseException("Targets must have a name attribute", locator);
            }
            target = new Target(org.apache.ant.core.xml.XMLProjectParser.getLocation(locator), targetName);
            target.setAspects(aspects);
            if (depends != null) {
                java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(depends, ",");
                while (tokenizer.hasMoreTokens()) {
                    java.lang.String dependency = tokenizer.nextToken();
                    target.addDependency(dependency);
                } 
            }
        }

        /* Process an element within this target. All elements within the target are 
        treated as tasks.

        @param uri The Namespace URI.
        @param localName The local name (without prefix).
        @param qualifiedName The qualified name (with prefix)
        @param attributes The attributes attached to the element. 

        @throws SAXParseException if there is a parsing problem.
         */
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qualifiedName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
            // everything is a task
            org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler taskHandler = new org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler(getXMLReader(), this, getLocator(), attributes, qualifiedName);
            target.addTask(taskHandler.getTaskElement());
        }

        /**
         * Get the target parsed by this handler.
         *
         * @return the Target model object parsed by this handler.
         */
        public org.apache.ant.core.xml.Target getTarget() {
            return target;
        }
    }

    /**
     * A TaskElementHandler parses the task elements of a build
     */
    private class TaskElementHandler extends ElementHandler {
        /**
         * The task element being parsed by this handler.
         */
        private org.apache.ant.core.xml.TaskElement taskElement;

        /**
         * Create a task element handler to parse a task element
         *
         * @param xmlReader
         * 		the XML parser being used to parse the task element.
         * @param parent
         * 		the parent element handler.
         * @param locator
         * 		the SAX locator object used to associate elements with source
         * 		locations.
         * @param attributes
         * 		attributes of the task element
         * @param elementTagName
         * 		the name of the task element.
         */
        public TaskElementHandler(org.xml.sax.XMLReader xmlReader, org.xml.sax.ContentHandler parent, org.xml.sax.Locator locator, org.xml.sax.Attributes attributes, java.lang.String elementTagName) {
            super(xmlReader, parent, locator);
            taskElement = new TaskElement(org.apache.ant.core.xml.XMLProjectParser.getLocation(locator), elementTagName);
            java.util.Map aspects = new java.util.HashMap();
            for (int i = 0; i < attributes.getLength(); ++i) {
                java.lang.String attributeName = attributes.getQName(i);
                java.lang.String attributeValue = attributes.getValue(i);
                if (attributeName.indexOf(":") != (-1)) {
                    // potential aspect attribute
                    aspects.put(attributeName, attributeValue);
                } else {
                    taskElement.addAttribute(attributeName, attributeValue);
                }
            }
            taskElement.setAspects(aspects);
        }

        /**
         * Process a nested element of this task element. All nested elements
         * of a taskElement are themselves taskElements.
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
            // everything within a task element is also a task element
            org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler nestedHandler = new org.apache.ant.core.xml.XMLProjectParser.TaskElementHandler(getXMLReader(), this, getLocator(), attributes, qualifiedName);
            taskElement.addTaskElement(nestedHandler.getTaskElement());
        }

        public void characters(char[] buf, int start, int end) throws org.xml.sax.SAXParseException {
            taskElement.addText(new java.lang.String(buf, start, end));
        }

        /**
         * Get the task element being parsed by this handler.
         *
         * @return the TaskElement being parsed.
         */
        public org.apache.ant.core.xml.TaskElement getTaskElement() {
            return taskElement;
        }
    }

    /**
     * A NoProjectReadException is used to indicate that a project
     * was not read from the particular source. This will happen
     * if the source is empty.
     */
    private class NoProjectReadException extends java.lang.Exception {}
}