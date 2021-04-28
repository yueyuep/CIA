/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.deployer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
/**
 * This class deploys a .tsk file into a registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Deployment {
    public static final java.lang.String DESCRIPTOR_NAME = "META-INF/ant-descriptor.xml";

    private java.io.File m_file;

    private org.apache.avalon.framework.configuration.Configuration m_descriptor;

    public Deployment(final java.io.File file) {
        m_file = file;
    }

    public org.apache.avalon.framework.configuration.Configuration getDescriptor() throws org.apache.myrmidon.components.deployer.DeploymentException {
        if (null == m_descriptor) {
            m_descriptor = buildDescriptor();
        }
        return m_descriptor;
    }

    public java.net.URL getURL() throws org.apache.myrmidon.components.deployer.DeploymentException {
        try {
            return m_file.getCanonicalFile().toURL();
        } catch (final java.io.IOException ioe) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Unable to form url", ioe);
        }
    }

    private org.apache.avalon.framework.configuration.Configuration buildDescriptor() throws org.apache.myrmidon.components.deployer.DeploymentException {
        final java.lang.String systemID = (("jar:" + getURL()) + "!/") + org.apache.myrmidon.components.deployer.Deployment.DESCRIPTOR_NAME;
        try {
            final javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
            final javax.xml.parsers.SAXParser saxParser = saxParserFactory.newSAXParser();
            final org.xml.sax.XMLReader parser = saxParser.getXMLReader();
            // parser.setFeature( "http://xml.org/sax/features/namespace-prefixes", false );
            final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler = new org.apache.avalon.framework.configuration.SAXConfigurationHandler();
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
            parser.parse(systemID);
            return handler.getConfiguration();
        } catch (final org.xml.sax.SAXException se) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Malformed configuration data", se);
        } catch (final javax.xml.parsers.ParserConfigurationException pce) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Error configuring parser", pce);
        } catch (final java.io.IOException ioe) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Error reading configuration", ioe);
        }
    }
}