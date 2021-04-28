/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.deployer;
import org.apache.avalon.framework.component.Component;
/**
 * This class deploys a .tsk file into a registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Deployer extends org.apache.avalon.framework.component.Component {
    java.lang.String ROLE = "org.apache.myrmidon.components.deployer.Deployer";

    /**
     * Deploy a library.
     *
     * @param file
     * 		the file deployment
     * @exception DeploymentException
     * 		if an error occurs
     */
    void deploy(java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException;

    void deployConverter(java.lang.String name, java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException;

    void deployType(java.lang.String role, java.lang.String name, java.io.File file) throws org.apache.myrmidon.components.deployer.DeploymentException;
}