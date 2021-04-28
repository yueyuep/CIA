/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.selftest;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
/**
 * Test conversion of all the primitive types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PrimitiveTypesTest extends org.apache.myrmidon.api.AbstractTask {
    public void setInteger(final java.lang.Integer value) {
        getLogger().warn(("setInteger( " + value) + " );");
    }

    public void setInteger2(final int value) {
        getLogger().warn(("setInteger2( " + value) + " );");
    }

    public void setShort(final java.lang.Short value) {
        getLogger().warn(("setShort( " + value) + " );");
    }

    public void setShort2(final short value) {
        getLogger().warn(("setShort2( " + value) + " );");
    }

    public void setByte(final java.lang.Byte value) {
        getLogger().warn(("setByte( " + value) + " );");
    }

    public void setByte2(final byte value) {
        getLogger().warn(("setByte2( " + value) + " );");
    }

    public void setLong(final java.lang.Long value) {
        getLogger().warn(("setLong( " + value) + " );");
    }

    public void setLong2(final long value) {
        getLogger().warn(("setLong2( " + value) + " );");
    }

    public void setFloat(final java.lang.Float value) {
        getLogger().warn(("setFloat( " + value) + " );");
    }

    public void setFloat2(final float value) {
        getLogger().warn(("setFloat2( " + value) + " );");
    }

    public void setDouble(final java.lang.Double value) {
        getLogger().warn(("setDouble( " + value) + " );");
    }

    public void setDouble2(final double value) {
        getLogger().warn(("setDouble2( " + value) + " );");
    }

    public void setString(final java.lang.String value) {
        getLogger().warn(("setString( " + value) + " );");
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
    }
}