/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.xml;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
/**
 * This class knows how to locate xml project files
 *  and import them into the workspace.
 */
public class XmlImporter implements Importer {
    private java.net.URL[] path;

    /**
     * Constructs an importer for a workspace.
     */
    public XmlImporter() {
        this.path = org.apache.tools.ant.xml.XmlImporter.getProjectPath();
    }

    /**
     * Imports the project with the specified name.
     */
    public void importProject(Project project) throws org.apache.tools.ant.xml.BuildException {
        // Locate the project file
        java.net.URLConnection conn = findProjectFile(project);
        // Parse the xml
        parseProjectFile(project, conn);
    }

    /**
     * Find the .ant file for this project. Searches each directory and
     *  jar in the project path.
     */
    private java.net.URLConnection findProjectFile(Project project) throws org.apache.tools.ant.xml.BuildException {
        java.lang.String fileName = project.getName() + ".ant";
        for (int i = 0; i < path.length; i++) {
            try {
                java.net.URL url = new java.net.URL(path[i], fileName);
                java.net.URLConnection conn = url.openConnection();
                conn.connect();
                project.setBase(path[i]);
                project.setLocation(url.toString());
                return conn;
            } catch (java.io.FileNotFoundException exc) {
                // The file ins't in this directory/jar, keep looking
            } catch (java.io.IOException exc) {
                // Not sure what to do here...
                exc.printStackTrace();
            }
        }
        throw new BuildException(("Project \"" + project.getName()) + "\" not found");
    }

    /**
     * Parse the xml file.
     */
    private void parseProjectFile(Project project, java.net.URLConnection conn) throws org.apache.tools.ant.xml.BuildException {
        ProjectHandler handler = new ProjectHandler(project);
        try {
            org.xml.sax.InputSource source = new org.xml.sax.InputSource(conn.getInputStream());
            source.setPublicId(conn.getURL().toString());
            javax.xml.parsers.SAXParser parser = org.apache.tools.ant.xml.XmlImporter.parserFactory.newSAXParser();
            /* parser.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", handler); */
            parser.parse(source, handler);
        } catch (org.xml.sax.SAXParseException exc) {
            if (exc.getException() instanceof BuildException) {
                throw ((BuildException) (exc.getException()));
            }
            throw new BuildException(exc.getMessage(), (exc.getPublicId() + ":") + exc.getLineNumber());
        } catch (org.xml.sax.SAXException exc) {
            if (exc.getException() instanceof BuildException) {
                throw ((BuildException) (exc.getException()));
            } else {
                throw new AntException("Parse error", exc);
            }
        } catch (javax.xml.parsers.ParserConfigurationException exc) {
            throw new AntException("Parser configuration error", exc);
        } catch (java.io.FileNotFoundException exc) {
            // This should never happen, since conn.connect()
            // has already been called successfully
            throw new AntException("Project file not found", exc);
        } catch (java.io.IOException exc) {
            throw new AntException("Error reading project file", exc);
        }
        return;
    }

    /**
     * Parses the project path (specified using the "ant.project.path"
     *  system propertyinto URL objects.
     */
    private static java.net.URL[] getProjectPath() {
        java.lang.String s = java.lang.System.getProperty("ant.project.path", ".");
        java.util.StringTokenizer tokens = new java.util.StringTokenizer(s, java.lang.System.getProperty("path.separator"));
        int i = 0;
        java.net.URL[] path = new java.net.URL[tokens.countTokens()];
        while (tokens.hasMoreTokens()) {
            java.lang.String token = tokens.nextToken();
            try {
                if (token.endsWith(".jar")) {
                    path[i] = new java.net.URL(("jar:file:" + token) + "!/");
                } else if (token.endsWith("/")) {
                    path[i] = new java.net.URL("file:" + token);
                } else {
                    path[i] = new java.net.URL(("file:" + token) + "/");
                }
            } catch (java.net.MalformedURLException exc) {
                exc.printStackTrace();
            }
            i++;
        } 
        return path;
    }

    /**
     * JAXP stuff.
     */
    private static javax.xml.parsers.SAXParserFactory parserFactory;

    static {
        parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(true);
    }
}