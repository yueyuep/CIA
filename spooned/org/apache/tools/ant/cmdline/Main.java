/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant.cmdline;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Runs the command line version of ant. Takes a list of
 *  fully qualified targets and builds them.
 *  Any jars in the "tasks" directory will be automatically added
 *  to the project path.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class Main {
    /**
     * Builds the specified targets.
     */
    public static void main(java.lang.String[] args) {
        java.io.File installDir = org.apache.tools.ant.cmdline.Main.findInstallDir();
        org.apache.tools.ant.cmdline.Main.setupProjectPath(installDir);
        Importer importer = org.apache.tools.ant.cmdline.Main.loadImporter(installDir);
        Workspace workspace = new Workspace(importer);
        workspace.addBuildListener(new DefaultLogger(java.lang.System.out));
        java.util.List targets = java.util.Arrays.asList(args);
        try {
            if (workspace.build(targets)) {
                java.lang.System.exit(0);
            } else {
                java.lang.System.exit(1);
            }
        } catch (java.lang.Exception exc) {
            exc.printStackTrace();
            java.lang.System.exit(2);
        }
    }

    /**
     * Finds the ant.jar file in the classpath.
     */
    private static java.io.File findInstallDir() {
        java.util.StringTokenizer itr = new java.util.StringTokenizer(java.lang.System.getProperty("java.class.path"), java.lang.System.getProperty("path.separator"));
        while (itr.hasMoreTokens()) {
            java.io.File file = new java.io.File(itr.nextToken());
            if (file.getName().equals("ant.jar")) {
                // Found it
                java.io.File dir = file.getParentFile();
                if (dir == null) {
                    dir = new java.io.File(".");
                }
                return dir;
            }
        } 
        java.lang.System.err.println("Unable to locate ant.jar");
        java.lang.System.exit(1);
        return null;
    }

    /**
     * Locates the "tasks" directory relative to the ant.jar file.
     */
    private static void setupProjectPath(java.io.File installDir) {
        java.lang.StringBuffer path = new java.lang.StringBuffer(java.lang.System.getProperty("ant.project.path", "."));
        java.io.File taskDir = new java.io.File(installDir, "tasks");
        if (taskDir.exists()) {
            java.io.File[] taskjars = taskDir.listFiles();
            for (int i = 0; i < taskjars.length; i++) {
                path.append(java.lang.System.getProperty("path.separator"));
                path.append(taskjars[i].getPath());
            }
        }
        java.lang.System.setProperty("ant.project.path", path.toString());
        java.lang.System.out.println(path.toString());
    }

    /**
     * Creates a class loader using the jars from the "xml" directory, and
     *  loads the XmlImporter class.
     */
    private static org.apache.tools.ant.cmdline.Importer loadImporter(java.io.File installDir) {
        java.io.File xmlDir = new java.io.File(installDir, "xml");
        if (xmlDir.exists()) {
            java.io.File[] xmlJars = xmlDir.listFiles();
            java.net.URL[] urls = new java.net.URL[xmlJars.length];
            for (int i = 0; i < xmlJars.length; i++) {
                try {
                    urls[i] = xmlJars[i].toURL();
                } catch (java.net.MalformedURLException exc) {
                    exc.printStackTrace();
                }
            }
            try {
                java.net.URLClassLoader loader = new java.net.URLClassLoader(urls);
                return ((Importer) (loader.loadClass("org.apache.tools.ant.xml.XmlImporter").newInstance()));
            } catch (java.lang.Exception exc) {
                exc.printStackTrace();
                java.lang.System.exit(1);
            }
        } else {
            java.lang.System.err.println("Unable to find xml directory");
            java.lang.System.exit(1);
        }
        return null;
    }
}