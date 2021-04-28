/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.selftest;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
/**
 * Test sub-elements addition.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SubElementTest extends org.apache.myrmidon.api.AbstractTask {
    public static final class Beep {
        public void setMessage(final java.lang.String string) {
            java.lang.System.out.println(string);
        }
    }

    public org.apache.myrmidon.libs.selftest.SubElementTest.Beep createCreateBeep() {
        java.lang.System.out.println("createCreateBeep()");
        return new org.apache.myrmidon.libs.selftest.SubElementTest.Beep();
    }

    public void addAddBeep(final org.apache.myrmidon.libs.selftest.SubElementTest.Beep beep) {
        java.lang.System.out.println(("addBeeper(" + beep) + ");");
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
    }
}