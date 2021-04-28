/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.executor;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.log.Logger;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.components.type.TypeManager;
/**
 * Frames in which tasks are executed.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ExecutionFrame {
    org.apache.myrmidon.components.type.TypeManager getTypeManager();

    org.apache.log.Logger getLogger();

    org.apache.myrmidon.api.TaskContext getContext();

    org.apache.avalon.framework.component.ComponentManager getComponentManager();
}