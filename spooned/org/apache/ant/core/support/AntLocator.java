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
package org.apache.ant.core.support;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * The Ant Locator is used to find various Ant components without
 * requiring the user to maintain environment properties.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class AntLocator {
    private AntLocator() {
    }

    /**
     * Get the URL for the given class's load location.
     *
     * @param theClass
     * 		the class whose loadURL is desired.
     * @return a URL which identifies the component from which this class was loaded.
     * @throws LocationException
     * 		if the class' URL cannot be constructed.
     */
    public static java.net.URL getClassLocationURL(java.lang.Class theClass) throws org.apache.ant.core.support.LocationException {
        java.lang.String className = theClass.getName().replace('.', '/') + ".class";
        java.net.URL classRawURL = theClass.getClassLoader().getResource(className);
        try {
            java.lang.String fileComponent = classRawURL.getFile();
            if (classRawURL.getProtocol().equals("file")) {
                // Class comes from a directory of class files rather than
                // from a jar.
                int classFileIndex = fileComponent.lastIndexOf(className);
                if (classFileIndex != (-1)) {
                    fileComponent = fileComponent.substring(0, classFileIndex);
                }
                return new java.net.URL("file:" + fileComponent);
            } else if (classRawURL.getProtocol().equals("jar")) {
                // Class is coming from a jar. The file component of the URL
                // is actually the URL of the jar file
                int classSeparatorIndex = fileComponent.lastIndexOf("!");
                if (classSeparatorIndex != (-1)) {
                    fileComponent = fileComponent.substring(0, classSeparatorIndex);
                }
                return new java.net.URL(fileComponent);
            } else {
                // its running out of something besides a jar. We just return the Raw
                // URL as a best guess
                return classRawURL;
            }
        } catch (java.net.MalformedURLException e) {
            throw new LocationException(e);
        }
    }

    /**
     * Get the location of AntHome
     *
     * @return the URL containing AntHome.
     * @throws LocationException
     * 		if Ant's home cannot be determined.
     */
    public static java.net.URL getAntHome() throws org.apache.ant.core.support.LocationException {
        try {
            java.net.URL libraryURL = org.apache.ant.core.support.AntLocator.getLibraryURL();
            if (libraryURL != null) {
                return new java.net.URL(libraryURL, "..");
            } else {
                return null;
            }
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a URL to the Ant core jar. Other jars can be located
     * from this as relative URLs
     *
     * @return a URL containing the Ant core or null if the core cannot be determined.
     * @throws LocationException
     * 		if the URL of the core.jar cannot be determined.
     */
    public static java.net.URL getCoreURL() throws org.apache.ant.core.support.LocationException {
        return org.apache.ant.core.support.AntLocator.getClassLocationURL(org.apache.ant.core.support.AntLocator.class);
    }

    /**
     * Get a URL to the Ant Library directory.
     *
     * @throws LocationException
     * 		if the location of the Ant library directory cannot
     * 		be determined
     */
    public static java.net.URL getLibraryURL() throws org.apache.ant.core.support.LocationException {
        java.net.URL coreURL = org.apache.ant.core.support.AntLocator.getCoreURL();
        try {
            if (coreURL.getProtocol().equals("file") && coreURL.getFile().endsWith("/")) {
                // we are running from a set of classes. This should only happen
                // in an Ant build situation. We use some embedded knowledge to
                // locate the lib directory
                java.io.File coreClassDirectory = new java.io.File(coreURL.getFile());
                java.io.File libDirectory = coreClassDirectory.getParentFile().getParentFile();
                if (!libDirectory.exists()) {
                    throw new LocationException(("Ant library directory " + libDirectory) + " does not exist");
                }
                return new java.io.File(libDirectory, "lib").toURL();
            } else {
                java.lang.String coreURLString = coreURL.toString();
                int index = coreURLString.lastIndexOf("/");
                if (index != (-1)) {
                    coreURLString = coreURLString.substring(0, index + 1);
                }
                return new java.net.URL(coreURLString);
            }
        } catch (java.net.MalformedURLException e) {
            throw new LocationException(e);
        }
    }

    /**
     * Get a classloader with which to load the SAX parser
     *
     * @return the classloader to use to load Ant's XML parser
     * @throws LocationException
     * 		if the location of the parser jars
     * 		could not be determined.
     */
    public static java.lang.ClassLoader getParserClassLoader(java.util.Properties properties) throws org.apache.ant.core.support.LocationException {
        // we look for the parser directory based on a system property first
        java.lang.String parserURLString = properties.getProperty(Constants.PropertyNames.PARSER_URL);
        java.net.URL parserURL = null;
        if (parserURLString != null) {
            try {
                parserURL = new java.net.URL(parserURLString);
            } catch (java.net.MalformedURLException e) {
                throw new LocationException(("XML Parser URL " + parserURLString) + " is malformed.", e);
            }
        } else {
            try {
                parserURL = new java.net.URL(org.apache.ant.core.support.AntLocator.getLibraryURL(), "parser/");
            } catch (java.lang.Exception e) {
                // ignore - we will just use the default class loader.
            }
        }
        if (parserURL != null) {
            try {
                java.net.URL[] parserURLs = null;
                if (parserURL.getProtocol().equals("file")) {
                    // build up the URLs for each jar file in the
                    // parser directory
                    parserURLs = org.apache.ant.core.support.AntLocator.getDirectoryJarURLs(new java.io.File(parserURL.getFile()));
                } else {
                    // we can't search the URL so we look for a known parser relative to
                    // that URL
                    java.lang.String defaultParser = properties.getProperty(Constants.PropertyNames.DEFAULT_PARSER);
                    if (defaultParser == null) {
                        defaultParser = Constants.Defaults.DEFAULT_PARSER;
                    }
                    parserURLs = new java.net.URL[1];
                    parserURLs[0] = new java.net.URL(parserURL, defaultParser);
                }
                return new AntClassLoader(parserURLs, "parser");
            } catch (java.net.MalformedURLException e) {
                throw new LocationException(e);
            }
        }
        return org.apache.ant.core.support.AntLocator.class.getClassLoader();
    }

    /**
     * Get an array of URLs for each file matching the given set of extensions
     *
     * @param directory
     * 		the local directory
     * @param extensions
     * 		the set of extensions to be returned
     * @return an array of URLs for the file found in the directory.
     */
    public static java.net.URL[] getDirectoryURLs(java.io.File directory, final java.util.Set extensions) {
        java.net.URL[] urls = new java.net.URL[0];
        if (!directory.exists()) {
            return urls;
        }
        java.io.File[] jars = directory.listFiles(new java.io.FilenameFilter() {
            public boolean accept(java.io.File dir, java.lang.String name) {
                int extensionIndex = name.lastIndexOf(".");
                if (extensionIndex == (-1)) {
                    return false;
                }
                java.lang.String extension = name.substring(extensionIndex);
                return extensions.contains(extension);
            }
        });
        urls = new java.net.URL[jars.length];
        for (int i = 0; i < jars.length; ++i) {
            try {
                urls[i] = jars[i].toURL();
            } catch (java.net.MalformedURLException e) {
                // just ignore
            }
        }
        return urls;
    }

    /**
     * Get an array of URLs for each jar file in a local directory.
     *
     * @param directory
     * 		the local directory
     * @return an array of URLs for the jars found in the directory.
     */
    private static java.net.URL[] getDirectoryJarURLs(java.io.File directory) {
        java.util.HashSet extensions = new java.util.HashSet();
        extensions.add(".jar");
        return org.apache.ant.core.support.AntLocator.getDirectoryURLs(directory, extensions);
    }

    /**
     * Get the Core Class Loader. The core requires a SAX parser which must come from the
     * given classloader
     *
     * @throws LocationException
     * 		if the location of the core ant classes could
     * 		not be determined
     */
    public static org.apache.ant.core.support.AntClassLoader getCoreClassLoader(java.util.Properties properties) throws org.apache.ant.core.support.LocationException {
        java.net.URL[] coreURL = new java.net.URL[1];
        coreURL[0] = org.apache.ant.core.support.AntLocator.getCoreURL();
        AntClassLoader coreLoader = new AntClassLoader(coreURL, org.apache.ant.core.support.AntLocator.getParserClassLoader(properties), "core");
        java.net.URL libraryURL = org.apache.ant.core.support.AntLocator.getLibraryURL();
        if ((libraryURL != null) && libraryURL.getProtocol().equals("file")) {
            // we can search this
            java.net.URL[] optionalURLs = org.apache.ant.core.support.AntLocator.getDirectoryJarURLs(new java.io.File(libraryURL.getFile(), "optional"));
            for (int i = 0; i < optionalURLs.length; ++i) {
                coreLoader.addURL(optionalURLs[i]);
            }
        }
        return coreLoader;
    }
}