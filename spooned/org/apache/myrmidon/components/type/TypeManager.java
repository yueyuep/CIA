/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.type;
import org.apache.avalon.framework.component.Component;
/**
 * The interface that is used to manage types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface TypeManager extends org.apache.avalon.framework.component.Component {
    java.lang.String ROLE = "org.apache.myrmidon.components.type.TypeManager";

    void registerType(java.lang.String role, java.lang.String shorthandName, org.apache.myrmidon.components.type.TypeFactory factory) throws org.apache.myrmidon.components.type.TypeException;

    org.apache.myrmidon.components.type.TypeFactory getFactory(java.lang.String role) throws org.apache.myrmidon.components.type.TypeException;

    org.apache.myrmidon.components.type.TypeManager createChildTypeManager();
}