/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.aspect;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.aspects.AspectHandler;
/**
 * Manage and propogate Aspects.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface AspectManager extends org.apache.avalon.framework.component.Component , org.apache.myrmidon.aspects.AspectHandler {
    java.lang.String ROLE = "org.apache.myrmidon.components.aspect.AspectManager";

    java.lang.String[] getNames();

    void dispatchAspectSettings(java.lang.String name, org.apache.avalon.framework.parameters.Parameters parameters, org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException;

    void addAspectHandler(java.lang.String name, org.apache.myrmidon.aspects.AspectHandler handler) throws org.apache.myrmidon.api.TaskException;

    void removeAspectHandler(java.lang.String name, org.apache.myrmidon.aspects.AspectHandler handler) throws org.apache.myrmidon.api.TaskException;
}