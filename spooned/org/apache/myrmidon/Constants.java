/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon;
/**
 * Abstract interface to hold constants.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Constants {
    // Constants to indicate the build of Myrmidon
    java.lang.String BUILD_DATE = "@@DATE@@";

    java.lang.String BUILD_VERSION = "@@VERSION@@";

    java.lang.String BUILD_DESCRIPTION = (("Myrmidon " + org.apache.myrmidon.Constants.BUILD_VERSION) + " compiled on ") + org.apache.myrmidon.Constants.BUILD_DATE;
}