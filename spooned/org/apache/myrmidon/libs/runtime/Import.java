/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.runtime;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.deployer.Deployer;
import org.apache.myrmidon.components.deployer.DeploymentException;
/**
 * Task to import a tasklib.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Import extends org.apache.myrmidon.api.AbstractTask implements org.apache.avalon.framework.component.Composable {
    private java.io.File m_lib;

    private org.apache.myrmidon.components.deployer.Deployer m_deployer;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_deployer = ((org.apache.myrmidon.components.deployer.Deployer) (componentManager.lookup(Deployer.ROLE)));
    }

    public void setLib(final java.io.File lib) {
        m_lib = lib;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_lib) {
            throw new org.apache.myrmidon.api.TaskException("Must specify lib parameter");
        }
        try {
            m_deployer.deploy(m_lib);
        } catch (final org.apache.myrmidon.components.deployer.DeploymentException de) {
            throw new org.apache.myrmidon.api.TaskException("Error importing tasklib", de);
        }
    }
}