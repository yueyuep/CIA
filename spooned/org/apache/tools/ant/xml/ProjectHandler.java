/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.xml;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.*;
/**
 * This class populates a Project object via SAX events.
 */
/* implements LexicalHandler */
public class ProjectHandler extends org.xml.sax.helpers.DefaultHandler {
    private org.apache.tools.ant.xml.Workspace workspace;

    private org.apache.tools.ant.xml.Project project;

    private org.xml.sax.Locator locator;

    /**
     * The top of this stack represents the "current" event handler.
     */
    private java.util.Stack handlers;

    /**
     * Constructs a SAX handler for the specified project.
     */
    public ProjectHandler(Project project) {
        this.project = project;
        this.workspace = project.getWorkspace();
        this.handlers = new java.util.Stack();
        this.handlers.push(new org.apache.tools.ant.xml.ProjectHandler.RootHandler());
    }

    public org.apache.tools.ant.xml.Project getProject() {
        return project;
    }

    public void setDocumentLocator(org.xml.sax.Locator locator) {
        this.locator = locator;
    }

    protected java.lang.String getLocation() {
        return (locator.getPublicId() + ":") + locator.getLineNumber();
    }

    public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
        // Delegate to the current handler
        ((org.xml.sax.ContentHandler) (handlers.peek())).startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
        // Delegate to the current handler
        ((org.xml.sax.ContentHandler) (handlers.peek())).endElement(namespaceURI, localName, qName);
    }

    public void characters(char[] ch, int start, int length) {
        // XXX need to implement text content
    }

    public void processingInstruction(java.lang.String target, java.lang.String data) {
        java.lang.System.out.println(((("@" + target) + "@") + data) + "@");
    }

    /* public void comment(char[] ch, int start, int length) {)
    public void endCDATA() {}
    public void endDTD() {}
    public void endEntity(java.lang.String name) {}
    public void startCDATA() {}
    public void startDTD(String name, String publicId, String systemId) {}
    public void startEntity(java.lang.String name)  {}
     */
    /**
     * This class handles any top level SAX events.
     */
    private class RootHandler extends org.xml.sax.helpers.DefaultHandler {
        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            if (org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(namespaceURI) && localName.equals("project")) {
                handlers.push(new org.apache.tools.ant.xml.ProjectHandler.ProjectElemHandler(qName, atts));
            } else {
                throw new org.xml.sax.SAXParseException(("Unexpected element \"" + qName) + "\"", locator);
            }
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            handlers.pop();
        }
    }

    /**
     * This class handles events that occur with a "project" element.
     */
    private class ProjectElemHandler extends org.xml.sax.helpers.DefaultHandler {
        public ProjectElemHandler(java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            java.lang.String projectName = null;
            for (int i = 0; i < atts.getLength(); i++) {
                if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(atts.getURI(i))) {
                    continue;
                }
                java.lang.String name = atts.getQName(i);
                java.lang.String value = atts.getValue(i);
                if (name.equals("name")) {
                    projectName = value;
                } else {
                    throw new org.xml.sax.SAXParseException(("Unexpected attribute \"" + name) + "\"", locator);
                }
            }
            if (projectName == null) {
                throw new org.xml.sax.SAXParseException("Missing attribute \"name\"", locator);
            }
            if (!projectName.equals(project.getName())) {
                throw new org.xml.sax.SAXParseException(((("A project named \"" + projectName) + "\" must be located in a file called \"") + projectName) + ".ant\"", locator);
            }
        }

        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            if (org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(namespaceURI) && localName.equals("target")) {
                handlers.push(new org.apache.tools.ant.xml.ProjectHandler.TargetElemHandler(project, qName, atts));
            } else if (org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(namespaceURI) && localName.equals("import")) {
                handlers.push(new org.apache.tools.ant.xml.ProjectHandler.ImportElemHandler(project, qName, atts));
            } else {
                throw new org.xml.sax.SAXParseException(("Unexpected element \"" + qName) + "\"", locator);
            }
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            handlers.pop();
        }
    }

    /**
     * This class handles events that occur with a "target" element.
     */
    private class TargetElemHandler extends org.xml.sax.helpers.DefaultHandler {
        private org.apache.tools.ant.xml.Target target;

        public TargetElemHandler(Project project, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            java.lang.String targetName = null;
            java.lang.String dependencies = "";
            for (int i = 0; i < atts.getLength(); i++) {
                if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(atts.getURI(i))) {
                    continue;
                }
                java.lang.String name = atts.getQName(i);
                java.lang.String value = atts.getValue(i);
                if (name.equals("name")) {
                    targetName = value;
                } else if (name.equals("depends")) {
                    dependencies = value;
                } else {
                    throw new org.xml.sax.SAXParseException(("Unexpected attribute \"" + name) + "\"", locator);
                }
            }
            if (targetName == null) {
                throw new org.xml.sax.SAXParseException("Missing attribute \"name\"", locator);
            }
            try {
                target = project.createTarget(targetName);
                target.setLocation(getLocation());
                parseDepends(dependencies);
            } catch (BuildException exc) {
                throw new org.xml.sax.SAXException(exc);
            }
        }

        /**
         * Parses the list of space-separated project names.
         */
        private void parseDepends(java.lang.String depends) {
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(depends);
            while (tokenizer.hasMoreTokens()) {
                java.lang.String targetName = tokenizer.nextToken();
                target.addDepend(targetName);
            } 
        }

        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(namespaceURI)) {
                throw new org.xml.sax.SAXParseException(("Unexpected attribute \"" + qName) + "\"", locator);
            }
            TaskProxy proxy = target.createTaskProxy(qName);
            proxy.setLocation(getLocation());
            handlers.push(new org.apache.tools.ant.xml.ProjectHandler.TaskElemHandler(proxy.getData(), qName, atts));
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            handlers.pop();
        }
    }

    /**
     * This class handles events that occur with a "import" element.
     */
    private class ImportElemHandler extends org.xml.sax.helpers.DefaultHandler {
        public ImportElemHandler(Project project, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            java.lang.String importName = null;
            for (int i = 0; i < atts.getLength(); i++) {
                if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(atts.getURI(i))) {
                    continue;
                }
                java.lang.String name = atts.getQName(i);
                java.lang.String value = atts.getValue(i);
                if (name.equals("name")) {
                    importName = value;
                } else {
                    throw new org.xml.sax.SAXParseException(("Unexpected attribute \"" + name) + "\"", locator);
                }
            }
            if (importName == null) {
                throw new org.xml.sax.SAXParseException("Missing attribute \"name\"", locator);
            }
            Import imp = project.createImport(importName);
            imp.setLocation(getLocation());
        }

        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            throw new org.xml.sax.SAXParseException(("Unexpected element \"" + qName) + "\"", locator);
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            handlers.pop();
        }
    }

    /**
     * This class handles events that occur with a task element.
     */
    private class TaskElemHandler extends org.xml.sax.helpers.DefaultHandler {
        private org.apache.tools.ant.xml.TaskData data;

        public TaskElemHandler(TaskData data, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            this.data = data;
            for (int i = 0; i < atts.getLength(); i++) {
                if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(atts.getURI(i))) {
                    continue;
                }
                java.lang.String name = atts.getQName(i);
                java.lang.String value = atts.getValue(i);
                TaskData child = data.addProperty(name);
                child.setLocation(getLocation());
                child.setText(value);
            }
        }

        public void startElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
            if (!org.apache.tools.ant.xml.ProjectHandler.isAntNamespace(namespaceURI)) {
                throw new org.xml.sax.SAXParseException(("Unexpected element \"" + qName) + "\"", locator);
            }
            TaskData child = data.addProperty(qName);
            child.setLocation(getLocation());
            handlers.push(new org.apache.tools.ant.xml.ProjectHandler.TaskElemHandler(child, qName, atts));
        }

        public void endElement(java.lang.String namespaceURI, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            handlers.pop();
        }
    }

    private static boolean isAntNamespace(java.lang.String uri) {
        return uri == null ? false : uri.equals("");
    }
}