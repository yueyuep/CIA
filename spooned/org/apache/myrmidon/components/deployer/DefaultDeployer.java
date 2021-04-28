/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.deployer;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.components.converter.ConverterRegistry;
import org.apache.myrmidon.components.role.RoleManager;
import org.apache.myrmidon.components.type.DefaultTypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.converter.Converter;
/**
 * This class deploys a .tsk file into a registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultDeployer extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.deployer.Deployer , org.apache.avalon.framework.activity.Initializable , org.apache.avalon.framework.component.Composable {
    private static final java.lang.String TYPE_DESCRIPTOR = "META-INF/ant-types.xml";

    private org.apache.myrmidon.components.converter.ConverterRegistry m_converterRegistry;

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    private org.apache.myrmidon.components.role.RoleManager m_roleManager;

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_converterRegistry = ((org.apache.myrmidon.components.converter.ConverterRegistry) (componentManager.lookup(ConverterRegistry.ROLE)));
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        m_roleManager = ((org.apache.myrmidon.components.role.RoleManager) (componentManager.lookup(RoleManager.ROLE)));
    }

    public void initialize() throws java.lang.Exception {
        final javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        final javax.xml.parsers.SAXParser saxParser = saxParserFactory.newSAXParser();
        final org.xml.sax.XMLReader parser = saxParser.getXMLReader();
        // parser.setFeature( "http://xml.org/sax/features/namespace-prefixes", false );
        final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler = new org.apache.avalon.framework.configuration.SAXConfigurationHandler();
        parser.setContentHandler(handler);
        parser.setErrorHandler(handler);
        final java.lang.ClassLoader classLoader = getClass().getClassLoader();
        final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(classLoader);
        final java.util.Enumeration $missing$ = classLoader.getResources(Deployment.DESCRIPTOR_NAME);
    }

    public void deploy(final java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException {
        getLogger().info(("Deploying AntLib file (" + file) + ")");
        checkFile(file);
        final org.apache.myrmidon.components.deployer.Deployment deployment = new org.apache.myrmidon.components.deployer.Deployment(file);
        final org.apache.avalon.framework.configuration.Configuration descriptor = deployment.getDescriptor();
        final java.net.URL[] urls = new java.net.URL[]{ deployment.getURL() };
        final java.net.URLClassLoader classLoader = new java.net.URLClassLoader(urls, java.lang.Thread.currentThread().getContextClassLoader());
        try {
            deployFromDescriptor(descriptor, classLoader, deployment.getURL());
        } catch (final org.apache.myrmidon.components.deployer.DeploymentException de) {
            throw de;
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Error deploying library", e);
        }
    }

    public void deployConverter(final java.lang.String name, final java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException {
        checkFile(file);
        final org.apache.myrmidon.components.deployer.Deployment deployment = new org.apache.myrmidon.components.deployer.Deployment(file);
        final org.apache.avalon.framework.configuration.Configuration descriptor = deployment.getDescriptor();
        final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(deployment.getURL());
        try {
            final org.apache.avalon.framework.configuration.Configuration[] converters = descriptor.getChild("converters").getChildren("converter");
            for (int i = 0; i < converters.length; i++) {
                if (converters[i].getAttribute("classname").equals(name)) {
                    handleConverter(converters[i], factory);
                    break;
                }
            }
        } catch (final org.apache.avalon.framework.configuration.ConfigurationException ce) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Malformed taskdefs.xml", ce);
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Failed to deploy " + name, e);
        }
    }

    public void deployType(final java.lang.String role, final java.lang.String name, final java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException {
        checkFile(file);
        final java.lang.String shorthand = getNameForRole(role);
        final org.apache.myrmidon.components.deployer.Deployment deployment = new org.apache.myrmidon.components.deployer.Deployment(file);
        final org.apache.avalon.framework.configuration.Configuration descriptor = deployment.getDescriptor();
        final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(deployment.getURL());
        try {
            final org.apache.avalon.framework.configuration.Configuration[] datatypes = descriptor.getChild("types").getChildren(shorthand);
            for (int i = 0; i < datatypes.length; i++) {
                if (datatypes[i].getAttribute("name").equals(name)) {
                    handleType(role, datatypes[i], factory);
                    break;
                }
            }
        } catch (final org.apache.avalon.framework.configuration.ConfigurationException ce) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Malformed taskdefs.xml", ce);
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Failed to deploy " + name, e);
        }
    }

    private void deployFromDescriptor(final org.apache.avalon.framework.configuration.Configuration descriptor, final java.lang.ClassLoader classLoader, final java.net.URL url) throws org.apache.myrmidon.components.deployer.DeploymentException, java.lang.Exception {
        try {
            // Have to keep a new factory per role
            // To avoid name clashes (ie a datatype and task with same name)
            final java.util.HashMap factorys = new java.util.HashMap();
            final org.apache.avalon.framework.configuration.Configuration[] types = descriptor.getChild("types").getChildren();
            for (int i = 0; i < types.length; i++) {
                final java.lang.String name = types[i].getName();
                final java.lang.String role = getRoleForName(name);
                final org.apache.myrmidon.components.type.DefaultTypeFactory factory = getFactory(role, classLoader, factorys);
                handleType(role, types[i], factory);
            }
            final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(classLoader);
            final org.apache.avalon.framework.configuration.Configuration[] converters = descriptor.getChild("converters").getChildren();
            for (int i = 0; i < converters.length; i++) {
                final java.lang.String name = converters[i].getName();
                handleConverter(converters[i], factory);
            }
        } catch (final org.apache.myrmidon.components.deployer.DeploymentException de) {
            throw de;
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Error deploying library from " + url, e);
        }
    }

    private org.apache.myrmidon.components.type.DefaultTypeFactory getFactory(final java.lang.String role, final java.lang.ClassLoader classLoader, final java.util.HashMap factorys) {
        org.apache.myrmidon.components.type.DefaultTypeFactory factory = ((org.apache.myrmidon.components.type.DefaultTypeFactory) (factorys.get(role)));
        if (null == factory) {
            factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(classLoader);
            factorys.put(role, factory);
        }
        return factory;
    }

    private java.lang.String getNameForRole(final java.lang.String role) throws org.apache.myrmidon.components.deployer.DeploymentException {
        final java.lang.String name = m_roleManager.getNameForRole(role);
        if (null == name) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("RoleManager does not know name for role " + role);
        }
        return name;
    }

    private java.lang.String getRoleForName(final java.lang.String name) throws org.apache.myrmidon.components.deployer.DeploymentException {
        final java.lang.String role = m_roleManager.getRoleForName(name);
        if (null == role) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("RoleManager does not know role for name " + name);
        }
        return role;
    }

    private void checkFile(final java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException {
        if (!file.exists()) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException("Could not find application archive at " + file);
        }
        if (file.isDirectory()) {
            throw new org.apache.myrmidon.components.deployer.DeploymentException(("Could not find application archive at " + file) + " as it is a directory.");
        }
    }

    private void handleConverter(final org.apache.avalon.framework.configuration.Configuration converter, final org.apache.myrmidon.components.type.DefaultTypeFactory factory) throws java.lang.Exception {
        final java.lang.String name = converter.getAttribute("classname");
        final java.lang.String source = converter.getAttribute("source");
        final java.lang.String destination = converter.getAttribute("destination");
        m_converterRegistry.registerConverter(name, source, destination);
        factory.addNameClassMapping(name, name);
        m_typeManager.registerType(Converter.ROLE, name, factory);
        getLogger().debug((((("Registered converter " + name) + " that converts from ") + source) + " to ") + destination);
    }

    private void handleType(final java.lang.String role, final org.apache.avalon.framework.configuration.Configuration type, final org.apache.myrmidon.components.type.DefaultTypeFactory factory) throws java.lang.Exception {
        final java.lang.String name = type.getAttribute("name");
        final java.lang.String className = type.getAttribute("classname");
        factory.addNameClassMapping(name, className);
        m_typeManager.registerType(role, name, factory);
        getLogger().debug((((("Registered " + role) + "/") + name) + " as ") + className);
    }
}