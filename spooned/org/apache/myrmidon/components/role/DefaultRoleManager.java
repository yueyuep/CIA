/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included  with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.role;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
/**
 * Interface to manage roles and mapping to names.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version CVS $Revision$ $Date$
 */
public class DefaultRoleManager implements org.apache.myrmidon.components.role.RoleManager , org.apache.avalon.framework.activity.Initializable {
    private static final java.lang.String ROLE_DESCRIPTOR = "META-INF/ant-roles.xml";

    /**
     * Parent <code>RoleManager</code> for nested resolution
     */
    private final org.apache.myrmidon.components.role.RoleManager m_parent;

    /**
     * Map for name to role mapping
     */
    private final java.util.HashMap m_names = new java.util.HashMap();

    /**
     * Map for role to name mapping
     */
    private final java.util.HashMap m_roles = new java.util.HashMap();

    /**
     * constructor--this RoleManager has no parent.
     */
    public DefaultRoleManager() {
        this(null);
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent.
     *
     * @param parent
     * 		The parent <code>RoleManager</code>.
     */
    public DefaultRoleManager(final org.apache.myrmidon.components.role.RoleManager parent) {
        m_parent = parent;
    }

    /**
     * initialize the RoleManager.
     * This involves reading all Role descriptors in common classloader.
     *
     * @exception Exception
     * 		if an error occurs
     */
    public void initialize() throws java.lang.Exception {
        final javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        final javax.xml.parsers.SAXParser saxParser = saxParserFactory.newSAXParser();
        final org.xml.sax.XMLReader parser = saxParser.getXMLReader();
        // parser.setFeature( "http://xml.org/sax/features/namespace-prefixes", false );
        final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler = new org.apache.avalon.framework.configuration.SAXConfigurationHandler();
        parser.setContentHandler(handler);
        parser.setErrorHandler(handler);
        final java.util.Enumeration $missing$ = getClass().getClassLoader().getResources(org.apache.myrmidon.components.role.DefaultRoleManager.ROLE_DESCRIPTOR);
    }

    /**
     * Configure RoleManager based on contents of single descriptor.
     *
     * @param descriptor
     * 		the descriptor
     * @exception ConfigurationException
     * 		if an error occurs
     */
    private void handleDescriptor(final org.apache.avalon.framework.configuration.Configuration descriptor) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final org.apache.avalon.framework.configuration.Configuration[] types = descriptor.getChildren("role");
        for (int i = 0; i < types.length; i++) {
            final java.lang.String name = types[i].getAttribute("shorthand");
            final java.lang.String role = types[i].getAttribute("name");
            addNameRoleMapping(name, role);
        }
    }

    /**
     * Find Role name based on shorthand name.
     *
     * @param name
     * 		the shorthand name
     * @return the role
     */
    public java.lang.String getRoleForName(final java.lang.String name) {
        final java.lang.String role = ((java.lang.String) (m_names.get(name)));
        if ((null == role) && (null != m_parent)) {
            return m_parent.getRoleForName(name);
        }
        return role;
    }

    /**
     * Find name based on role.
     *
     * @param role
     * 		the role
     * @return the name
     */
    public java.lang.String getNameForRole(final java.lang.String role) {
        final java.lang.String name = ((java.lang.String) (m_roles.get(role)));
        if ((null == name) && (null != m_parent)) {
            return m_parent.getNameForRole(name);
        }
        return name;
    }

    /**
     * Add a mapping between name and role
     *
     * @param name
     * 		the shorthand name
     * @param role
     * 		the role
     * @exception IllegalArgumentException
     * 		if an name is already mapped to a different role
     */
    public void addNameRoleMapping(final java.lang.String name, final java.lang.String role) throws java.lang.IllegalArgumentException {
        final java.lang.String oldRole = ((java.lang.String) (m_names.get(name)));
        if ((null != oldRole) && oldRole.equals(role)) {
            throw new java.lang.IllegalArgumentException(("Name already mapped to another role (" + oldRole) + ")");
        }
        final java.lang.String oldName = ((java.lang.String) (m_roles.get(role)));
        if ((null != oldName) && oldName.equals(name)) {
            throw new java.lang.IllegalArgumentException(("Role already mapped to another name (" + oldName) + ")");
        }
        m_names.put(name, role);
        m_roles.put(role, name);
    }
}