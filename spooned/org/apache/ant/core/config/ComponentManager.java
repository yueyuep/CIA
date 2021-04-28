/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.ant.core.config;
import java.io.*;
import java.util.*;
import org.apache.ant.core.execution.*;
import org.apache.ant.core.support.*;
import org.apache.ant.core.xml.AntLibParser;
/**
 * Manager for Ant components
 *
 * The component manager is responsible for locating and loading the
 * components contained in Ant's lib/task directory.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class ComponentManager {
    /**
     * When Ant is run remotely, we need to look up the list of component
     * libraries from the server.
     */
    public static java.lang.String COMPONENT_INDEX = "taskindex";

    /**
     * Create the component manager. When the component manager is created
     * it will read the component definitions from the  files in
     * Ant's lib/task directory
     */
    public static org.apache.ant.core.config.AntLibrary[] getComponents() throws org.apache.ant.core.config.LocationException, org.apache.ant.core.config.ConfigException {
        try {
            java.net.URL componentsLocation = new java.net.URL(AntLocator.getLibraryURL(), "task/");
            java.net.URL[] componentFiles = null;
            if (componentsLocation.getProtocol().equals("file")) {
                // component directory is local - we determine the
                // component files by scanning the local directory
                java.util.HashSet componentFilesTypes = new java.util.HashSet();
                componentFilesTypes.add(".tsk");
                componentFilesTypes.add(".jar");
                componentFilesTypes.add(".zip");
                java.io.File componentsDirectory = new java.io.File(componentsLocation.getFile());
                componentFiles = AntLocator.getDirectoryURLs(componentsDirectory, componentFilesTypes);
            } else {
                // The component directory is remote - we determine the
                // list of component files by reading a "known"  list file.
                java.net.URL componentListURL = new java.net.URL(componentsLocation, org.apache.ant.core.config.ComponentManager.COMPONENT_INDEX);
                java.io.BufferedReader reader = null;
                java.util.List componentList = new java.util.ArrayList();
                try {
                    reader = new java.io.BufferedReader(new java.io.InputStreamReader(componentListURL.openStream()));
                    java.lang.String line = null;
                    while ((line = reader.readLine()) != null) {
                        componentList.add(new java.net.URL(componentsLocation, line.trim()));
                    } 
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
                componentFiles = ((java.net.URL[]) (componentList.toArray(new java.net.URL[0])));
            }
            org.apache.ant.core.xml.AntLibParser libdefParser = new org.apache.ant.core.xml.AntLibParser();
            java.util.List libraries = new java.util.ArrayList();
            for (int i = 0; i < componentFiles.length; ++i) {
                // We create a classloader for the component library
                java.net.URL[] componentURLs = new java.net.URL[]{ componentFiles[i] };
                AntClassLoader componentClassLoader = new AntClassLoader(componentURLs, org.apache.ant.core.config.ComponentManager.class.getClassLoader(), componentFiles[i].toString());
                java.net.URL libDefinition = componentClassLoader.getResource("ANT-INF/antlib.xml");
                if (libDefinition != null) {
                    AntLibrary library = libdefParser.parseAntLibrary(libDefinition, componentClassLoader);
                    libraries.add(library);
                }
            }
            return ((AntLibrary[]) (libraries.toArray(new AntLibrary[0])));
        } catch (java.io.IOException e) {
            throw new ConfigException(e);
        }
    }
}