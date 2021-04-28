/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.embeddor;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.myrmidon.components.model.Project;
import org.apache.myrmidon.components.workspace.Workspace;
/**
 * Interface through which you embed Myrmidon into applications.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Embeddor extends org.apache.avalon.framework.component.Component , org.apache.avalon.framework.parameters.Parameterizable , org.apache.avalon.framework.activity.Initializable , org.apache.avalon.framework.activity.Startable , org.apache.avalon.framework.activity.Disposable {
    java.lang.String ROLE = "org.apache.myrmidon.components.embeddor.Embeddor";

    /**
     * Create a project.
     *
     * @return the created Project
     */
    org.apache.myrmidon.components.model.Project createProject(java.lang.String location, java.lang.String type, org.apache.avalon.framework.parameters.Parameters parameters) throws java.lang.Exception;

    /**
     * Create a Workspace for a particular project.
     *
     * @param defines
     * 		the defines in workspace
     * @return the Workspace
     */
    org.apache.myrmidon.components.workspace.Workspace createWorkspace(org.apache.avalon.framework.parameters.Parameters parameters) throws java.lang.Exception;
}