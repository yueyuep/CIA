/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.ant.frontend;
import java.io.FileNotFoundException;
import org.apache.ant.antcore.config.AntConfig;
import org.apache.ant.antcore.config.AntConfigHandler;
import org.apache.ant.antcore.xml.ParseContext;
import org.apache.ant.antcore.xml.XMLParseException;
import org.apache.ant.common.util.ConfigException;
import org.apache.ant.init.InitUtils;
/**
 * Frontend Utilities methods and constants.
 *
 * @author Conor MacNeill
 * @created 16 April 2002
 */
public class FrontendUtils {
    /**
     * The default build file name
     */
    public static final java.lang.String DEFAULT_BUILD_FILENAME = "build.ant";

    /**
     * The default build file name
     */
    public static final java.lang.String DEFAULT_ANT1_FILENAME = "build.xml";

    /**
     * Get the AntConfig from the given config area if it is available
     *
     * @param configArea
     * 		the config area from which the config may be read
     * @return the AntConfig instance representing the config info read in
    from the config area. May be null if the AntConfig is not present
     * @exception ConfigException
     * 		if the URL for the config file cannotbe
     * 		formed.
     */
    public static org.apache.ant.antcore.config.AntConfig getAntConfig(java.io.File configArea) throws org.apache.ant.common.util.ConfigException {
        java.io.File configFile = new java.io.File(configArea, "antconfig.xml");
        try {
            return org.apache.ant.frontend.FrontendUtils.getAntConfigFile(configFile);
        } catch (java.io.FileNotFoundException e) {
            // ignore if files are not present
            return null;
        }
    }

    /**
     * Read in a config file
     *
     * @param configFile
     * 		the file containing the XML config
     * @return the parsed config object
     * @exception ConfigException
     * 		if the config cannot be parsed
     * @exception FileNotFoundException
     * 		if the file cannot be found.
     */
    public static org.apache.ant.antcore.config.AntConfig getAntConfigFile(java.io.File configFile) throws org.apache.ant.common.util.ConfigException, java.io.FileNotFoundException {
        try {
            java.net.URL configFileURL = org.apache.ant.init.InitUtils.getFileURL(configFile);
            org.apache.ant.antcore.xml.ParseContext context = new org.apache.ant.antcore.xml.ParseContext();
            org.apache.ant.antcore.config.AntConfigHandler configHandler = new org.apache.ant.antcore.config.AntConfigHandler();
            context.parse(configFileURL, "antconfig", configHandler);
            return configHandler.getAntConfig();
        } catch (java.net.MalformedURLException e) {
            throw new org.apache.ant.common.util.ConfigException("Unable to form URL to read config from " + configFile, e);
        } catch (org.apache.ant.antcore.xml.XMLParseException e) {
            if (e.getCause() instanceof java.io.FileNotFoundException) {
                throw ((java.io.FileNotFoundException) (e.getCause()));
            }
            throw new org.apache.ant.common.util.ConfigException("Unable to parse config file from " + configFile, e);
        }
    }
}