/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.launcher;
/**
 * Basic Loader that is responsible for all the hackery to get classloader to work.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class Main {
    /**
     * Magic entry point.
     *
     * @param args
     * 		the CLI arguments
     * @exception Exception
     * 		if an error occurs
     */
    public static final void main(final java.lang.String[] args) throws java.lang.Exception {
        try {
            // actually try to discover the install directory based on where
            // the myrmidon.jar is
            final java.io.File installDirectory = org.apache.myrmidon.launcher.Main.findInstallDir();
            java.lang.System.setProperty("myrmidon.home", installDirectory.toString());
            // setup classloader appropriately for myrmidon jar
            final java.io.File libDir = new java.io.File(installDirectory, "lib");
            final java.net.URL[] urls = org.apache.myrmidon.launcher.Main.buildURLList(libDir);
            final java.net.URLClassLoader classLoader = new java.net.URLClassLoader(urls);
            // load class and retrieve appropriate main method.
            final java.lang.Class clazz = classLoader.loadClass("org.apache.myrmidon.frontends.CLIMain");
            final java.lang.reflect.Method method = clazz.getMethod("main", new java.lang.Class[]{ args.getClass() });
            java.lang.Thread.currentThread().setContextClassLoader(classLoader);
            // kick the tires and light the fires....
            method.invoke(null, new java.lang.Object[]{ args });
        } catch (final java.lang.reflect.InvocationTargetException ite) {
            java.lang.System.err.println("Error: " + ite.getTargetException().getMessage());
            ite.getTargetException().printStackTrace();
        } catch (final java.lang.Throwable throwable) {
            java.lang.System.err.println("Error: " + throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    private static final java.net.URL[] buildURLList(final java.io.File dir) throws java.lang.Exception {
        final java.util.ArrayList urlList = new java.util.ArrayList();
        final java.io.File[] contents = dir.listFiles();
        if (null == contents) {
            return new java.net.URL[0];
        }
        for (int i = 0; i < contents.length; i++) {
            java.io.File file = contents[i];
            if ((!file.isFile()) || (!file.canRead())) {
                // ignore non-files or unreadable files
                continue;
            }
            final java.lang.String name = file.getName();
            if ((!name.endsWith(".jar")) && (!name.endsWith(".zip"))) {
                // Ifnore files in lib dir that are not jars or zips
                continue;
            }
            file = file.getCanonicalFile();
            urlList.add(file.toURL());
        }
        return ((java.net.URL[]) (urlList.toArray(new java.net.URL[0])));
    }

    /**
     * Finds the myrmidon.jar file in the classpath.
     */
    private static final java.io.File findInstallDir() throws java.lang.Exception {
        final java.lang.String classpath = java.lang.System.getProperty("java.class.path");
        final java.lang.String pathSeparator = java.lang.System.getProperty("path.separator");
        final java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(classpath, pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            final java.lang.String element = tokenizer.nextToken();
            if (element.endsWith("ant.jar")) {
                java.io.File file = new java.io.File(element).getAbsoluteFile();
                file = file.getParentFile();
                if (null != file) {
                    file = file.getParentFile();
                }
                return file;
            }
        } 
        throw new java.lang.Exception("Unable to locate ant.jar in classpath");
    }
}