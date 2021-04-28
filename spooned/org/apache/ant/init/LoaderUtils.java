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
package org.apache.ant.init;
import java.net.MalformedURLException;
/**
 * LoaderUtils is a utility class with methods for configuring a class
 * loader from a URL.
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class LoaderUtils {
    /**
     * This is the file that is consulted on remote systems to specify
     * available jars
     */
    public static final java.lang.String LIST_FILE = "file.list";

    /**
     * Get the URLs of a set of libraries in the given location
     *
     * @param location
     * 		the location to be searched
     * @param defaultFile
     * 		default file if none can be found
     * @return an array of URLs for the relevant jars
     * @exception MalformedURLException
     * 		the URLs cannot be created
     */
    public static java.net.URL[] getLocationURLs(java.net.URL location, java.lang.String defaultFile) throws java.net.MalformedURLException {
        return org.apache.ant.init.LoaderUtils.getLocationURLs(location, defaultFile, new java.lang.String[]{ ".jar" });
    }

    /**
     * Get the URLs of a set of libraries in the given location
     *
     * @param location
     * 		the location to be searched
     * @param extensions
     * 		array of allowable file extensions
     * @param defaultFile
     * 		default file if none can be found
     * @return an array of URLs for the relevant jars
     * @exception MalformedURLException
     * 		if the URL to the jars could not be
     * 		formed
     */
    public static java.net.URL[] getLocationURLs(java.net.URL location, java.lang.String defaultFile, java.lang.String[] extensions) throws java.net.MalformedURLException {
        java.net.URL[] urls = null;
        if (location.getProtocol().equals("file")) {
            // URL is local filesystem.
            urls = org.apache.ant.init.LoaderUtils.getLocalURLs(new java.io.File(location.getFile()), extensions);
        } else {
            // URL is remote - try to read a file with the list of jars
            java.net.URL jarListURL = new java.net.URL(location, org.apache.ant.init.LoaderUtils.LIST_FILE);
            java.io.BufferedReader reader = null;
            java.util.List jarList = new java.util.ArrayList();
            try {
                java.io.InputStreamReader isr = new java.io.InputStreamReader(jarListURL.openStream());
                reader = new java.io.BufferedReader(isr);
                java.lang.String line = null;
                while ((line = reader.readLine().trim()) != null) {
                    for (int i = 0; i < extensions.length; ++i) {
                        if (line.endsWith(extensions[i])) {
                            jarList.add(new java.net.URL(location, line));
                            break;
                        }
                    }
                } 
                urls = ((java.net.URL[]) (jarList.toArray(new java.net.URL[0])));
            } catch (java.io.IOException e) {
                // use the default location
                if (defaultFile != null) {
                    urls = new java.net.URL[]{ new java.net.URL(location, defaultFile) };
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (java.io.IOException e) {
                    }
                }
            }
        }
        return urls;
    }

    /**
     * Get the classpath from a classloader. This can only extract path
     * components from the loaders which are instances of URLClassLoaders
     *
     * @param loader
     * 		the loader whose path is required
     * @return the loader's configuration expressed as a classpath
     */
    public static java.lang.String getClasspath(java.lang.ClassLoader loader) {
        java.lang.StringBuffer pathBuffer = null;
        if (loader instanceof java.net.URLClassLoader) {
            java.net.URLClassLoader urlLoader = ((java.net.URLClassLoader) (loader));
            java.net.URL[] urls = urlLoader.getURLs();
            for (int i = 0; i < urls.length; ++i) {
                if (!urls[i].getProtocol().equals("file")) {
                    continue;
                }
                java.lang.String pathElement = urls[i].getFile();
                if (pathBuffer == null) {
                    pathBuffer = new java.lang.StringBuffer(pathElement);
                } else {
                    pathBuffer.append(java.io.File.pathSeparatorChar);
                    pathBuffer.append(pathElement);
                }
            }
        }
        java.lang.String path = (pathBuffer == null) ? "" : pathBuffer.toString();
        java.lang.ClassLoader parentLoader = loader.getParent();
        if (parentLoader != null) {
            java.lang.String parentPath = org.apache.ant.init.LoaderUtils.getClasspath(parentLoader);
            if (parentPath.length() != 0) {
                path = (parentPath + java.io.File.pathSeparator) + path;
            }
        }
        return path;
    }

    /**
     * Debug method to dump a class loader hierarchy to a PrintStream
     * URLClassLoaders dump their URLs
     *
     * @param loader
     * 		the class loaders whose configuration is dumped
     * @param ps
     * 		PrintStream to which info is sent
     */
    public static void dumpLoader(java.io.PrintStream ps, java.lang.ClassLoader loader) {
        if (loader instanceof java.net.URLClassLoader) {
            java.net.URLClassLoader urlLoader = ((java.net.URLClassLoader) (loader));
            java.net.URL[] urls = urlLoader.getURLs();
            if (urls.length == 0) {
                ps.println("   No URLs");
            } else {
                for (int i = 0; i < urls.length; ++i) {
                    ps.println("   URL: " + urls[i]);
                }
            }
        } else {
            ps.println("Class Loader: " + loader.getClass().getName());
        }
        ps.println();
        java.lang.ClassLoader parentLoader = loader.getParent();
        if (parentLoader != null) {
            ps.println("Parent Loader:");
            org.apache.ant.init.LoaderUtils.dumpLoader(ps, parentLoader);
        }
    }

    /**
     * Get an array of URLs for each file in the filesystem. If the given
     * location is a directory, it is searched for files of the given
     * extension. If it is a file, it is returned as a URL if it matches the
     * given extension list.
     *
     * @param location
     * 		the location within the local filesystem to be
     * 		searched
     * @param extensions
     * 		an array of file extensions to be considered in the
     * 		search
     * @return an array of URLs for the file found in the directory.
     * @exception MalformedURLException
     * 		if the URLs to the jars cannot be
     * 		formed
     */
    private static java.net.URL[] getLocalURLs(java.io.File location, final java.lang.String[] extensions) throws java.net.MalformedURLException {
        java.net.URL[] urls = new java.net.URL[0];
        if (!location.exists()) {
            return urls;
        }
        if (!location.isDirectory()) {
            urls = new java.net.URL[1];
            java.lang.String path = location.getPath();
            for (int i = 0; i < extensions.length; ++i) {
                if (path.endsWith(extensions[i])) {
                    urls[0] = org.apache.ant.init.InitUtils.getFileURL(location);
                    break;
                }
            }
            return urls;
        }
        java.io.File[] jars = location.listFiles(new java.io.FilenameFilter() {
            public boolean accept(java.io.File dir, java.lang.String name) {
                for (int i = 0; i < extensions.length; ++i) {
                    if (name.endsWith(extensions[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
        urls = new java.net.URL[jars.length];
        for (int i = 0; i < jars.length; ++i) {
            urls[i] = org.apache.ant.init.InitUtils.getFileURL(jars[i]);
        }
        return urls;
    }

    /**
     * Set the context loader of the current thread and returns the existing
     * classloader
     *
     * @param newLoader
     * 		the new context loader
     * @return the old context loader
     */
    public static java.lang.ClassLoader setContextLoader(java.lang.ClassLoader newLoader) {
        java.lang.Thread thread = java.lang.Thread.currentThread();
        java.lang.ClassLoader currentLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(newLoader);
        return currentLoader;
    }
}