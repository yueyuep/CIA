/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.aspects;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Logger;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskException;
/**
 * AspectHandler is the interface through which aspects are handled.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface AspectHandler {
    java.lang.String ROLE = "org.apache.myrmidon.aspects.AspectHandler";

    org.apache.avalon.framework.configuration.Configuration preCreate(org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException;

    void aspectSettings(org.apache.avalon.framework.parameters.Parameters parameters, org.apache.avalon.framework.configuration.Configuration[] children) throws org.apache.myrmidon.api.TaskException;

    void postCreate(org.apache.myrmidon.api.Task task) throws org.apache.myrmidon.api.TaskException;

    void preLoggable(org.apache.log.Logger logger) throws org.apache.myrmidon.api.TaskException;

    void preConfigure(org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException;

    void preExecute() throws org.apache.myrmidon.api.TaskException;

    void preDestroy() throws org.apache.myrmidon.api.TaskException;

    boolean error(org.apache.myrmidon.api.TaskException te) throws org.apache.myrmidon.api.TaskException;
}