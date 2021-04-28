/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.builder;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
/**
 * Default implementation to construct project from a build file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ATIProjectBuilder extends org.apache.myrmidon.components.builder.DefaultProjectBuilder implements org.apache.avalon.framework.parameters.Parameterizable {
    private static final java.lang.String PARAM_EXCEPTION = "Malformed PI: expected <?xsl-param name=\"foo\" value=\"bar\"?>";

    private static final java.lang.String PARAMS_EXCEPTION = "Malformed PI: expected <?xsl-params location=\"myparams.properties\"?>";

    private static final java.lang.String STYLE_EXCEPTION = "Malformed PI: expected <?xsl-params href=\"mystylesheet.xsl\"?>";

    private org.apache.avalon.framework.parameters.Parameters m_parameters;

    private java.net.URL m_systemID;

    public void parameterize(final org.apache.avalon.framework.parameters.Parameters parameters) {
        m_parameters = parameters;
    }

    protected void process(final java.net.URL sourceID, final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler) throws java.lang.Exception {
        final javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        final javax.xml.parsers.SAXParser saxParser = saxParserFactory.newSAXParser();
        final org.xml.sax.XMLReader reader = saxParser.getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setErrorHandler(handler);
        final org.apache.myrmidon.components.builder.ReactorPIHandler reactorHandler = new org.apache.myrmidon.components.builder.ReactorPIHandler();
        reader.setContentHandler(reactorHandler);
        try {
            reader.parse(sourceID.toString());
        } catch (final org.apache.myrmidon.components.builder.StopParsingException spe) {
            // Ignore me
        }
        javax.xml.transform.Transformer transformer = null;
        final int size = reactorHandler.getPICount();
        for (int i = 0; i < size; i++) {
            final java.lang.String target = reactorHandler.getTarget(i);
            final java.lang.String data = reactorHandler.getData(i);
            if (target.equals("xsl-param"))
                handleParameter(data);
            else if (target.equals("xsl-params"))
                handleParameters(data, sourceID);
            else if (target.equals("xsl-stylesheet")) {
                if (null != transformer) {
                    throw new org.xml.sax.SAXException("Build file can not contain " + "two xsl-stylesheet PIs");
                }
                final javax.xml.transform.TransformerFactory factory = javax.xml.transform.TransformerFactory.newInstance();
                final java.lang.String stylesheet = getStylesheet(data, sourceID);
                transformer = factory.newTransformer(new javax.xml.transform.stream.StreamSource(stylesheet));
            }
        }
        if (null == transformer) {
            reader.setContentHandler(handler);
            reader.parse(sourceID.toString());
        } else {
            final java.lang.String[] names = m_parameters.getNames();
            for (int i = 0; i < names.length; i++) {
                final java.lang.String name = names[i];
                final java.lang.String value = m_parameters.getParameter(name);
                transformer.setParameter(name, value);
            }
            final javax.xml.transform.sax.SAXResult result = new javax.xml.transform.sax.SAXResult(handler);
            transformer.transform(new javax.xml.transform.stream.StreamSource(sourceID.toString()), result);
            // transformer.transform( new StreamSource( sourceID.toString() ),
            // new StreamResult( System.out ) );
        }
    }

    private void handleParameter(final java.lang.String data) throws org.xml.sax.SAXException {
        int index = data.indexOf('\"');
        if ((-1) == index) {
            throw new org.xml.sax.SAXException(org.apache.myrmidon.components.builder.ATIProjectBuilder.PARAM_EXCEPTION);
        }
        index = data.indexOf('\"', index + 1);
        if ((-1) == index) {
            throw new org.xml.sax.SAXException(org.apache.myrmidon.components.builder.ATIProjectBuilder.PARAM_EXCEPTION);
        }
        // split between two "attributes" occurs on index
        final java.lang.String[] name = parseAttribute(data.substring(0, index + 1));
        final java.lang.String[] value = parseAttribute(data.substring(index + 1).trim());
        if ((!name[0].equals("name")) || (!value[0].equals("value"))) {
            throw new org.xml.sax.SAXException(org.apache.myrmidon.components.builder.ATIProjectBuilder.PARAM_EXCEPTION);
        }
        m_parameters.setParameter(name[1], value[1]);
    }

    private void handleParameters(final java.lang.String data, final java.net.URL baseSource) throws org.xml.sax.SAXException {
        final java.lang.String[] params = parseAttribute(data);
        if (!params[0].equals("location")) {
            throw new org.xml.sax.SAXException(org.apache.myrmidon.components.builder.ATIProjectBuilder.PARAMS_EXCEPTION);
        }
        try {
            final java.util.Properties properties = new java.util.Properties();
            final java.net.URL url = new java.net.URL(baseSource, params[1]);
            final java.io.InputStream input = url.openStream();
            properties.load(input);
            final org.apache.avalon.framework.parameters.Parameters parameters = org.apache.avalon.framework.parameters.Parameters.fromProperties(properties);
            m_parameters.merge(parameters);
        } catch (final java.lang.Exception e) {
            throw new org.xml.sax.SAXException("Error loading parameters: " + e);
        }
    }

    private java.lang.String getStylesheet(final java.lang.String data, final java.net.URL baseSource) throws org.xml.sax.SAXException {
        final java.lang.String[] stylesheet = parseAttribute(data);
        if (!stylesheet[0].equals("href")) {
            throw new org.xml.sax.SAXException(org.apache.myrmidon.components.builder.ATIProjectBuilder.STYLE_EXCEPTION);
        }
        try {
            return new java.net.URL(baseSource, stylesheet[1]).toString();
        } catch (final java.lang.Exception e) {
            throw new org.xml.sax.SAXException((("Error locating stylesheet '" + stylesheet[1]) + "' due to ") + e);
        }
    }

    private java.lang.String[] parseAttribute(final java.lang.String data) throws org.xml.sax.SAXException {
        // name="value"
        int index = data.indexOf('=');
        if ((-1) == index) {
            throw new org.xml.sax.SAXException(("Expecting an attribute but received '" + data) + "'");
        }
        final int size = data.length();
        if ((('\"' != data.charAt(index + 1)) || ('\"' != data.charAt(size - 1))) || ((size - 1) == index)) {
            throw new org.xml.sax.SAXException(("Expecting the value of attribute " + data.substring(0, index)) + " to be enclosed in quotes");
        }
        final java.lang.String[] result = new java.lang.String[2];
        result[0] = data.substring(0, index);
        result[1] = data.substring(index + 2, size - 1);
        return result;
    }
}