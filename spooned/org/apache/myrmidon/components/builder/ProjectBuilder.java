/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.builder;
import java.io.IOException;
import org.apache.avalon.framework.component.Component;
import org.apache.myrmidon.components.model.Project;
/**
 * Interface implemented by components that build projects from sources.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ProjectBuilder extends org.apache.avalon.framework.component.Component {
    java.lang.String ROLE = "org.apache.myrmidon.components.builder.ProjectBuilder";

    /**
     * build a project from source.
     *
     * @param source
     * 		the source
     * @return the constructed Project
     * @exception IOException
     * 		if an error occurs
     * @exception AntException
     * 		if an error occurs
     */
    org.apache.myrmidon.components.model.Project build(java.lang.String source) throws java.lang.Exception;
}