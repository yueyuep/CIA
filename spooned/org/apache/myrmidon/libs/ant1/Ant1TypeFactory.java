/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.ant1;
import org.apache.myrmidon.components.type.DefaultTypeFactory;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.tools.ant.Task;
/**
 * Factory used to create adaptors for Ant1 tasks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Ant1TypeFactory extends org.apache.myrmidon.components.type.DefaultTypeFactory {
    public Ant1TypeFactory(final java.net.URL url) {
        super(url);
    }

    public Ant1TypeFactory(final java.net.URL[] urls) {
        super(urls);
    }

    public Ant1TypeFactory(final java.net.URL[] urls, final java.lang.ClassLoader parent) {
        super(urls, parent);
    }

    public Ant1TypeFactory(final java.lang.ClassLoader classLoader) {
        super(classLoader);
    }

    public java.lang.Object create(final java.lang.String name) throws org.apache.myrmidon.components.type.TypeException {
        final java.lang.Object object = super.create(name);
        if (!(object instanceof org.apache.tools.ant.Task)) {
            throw new org.apache.myrmidon.components.type.TypeException(("Expected an Ant1 task but received an " + "object of type : ") + object.getClass().getName());
        }
        return new org.apache.myrmidon.libs.ant1.TaskAdapter(((org.apache.tools.ant.Task) (object)));
    }
}