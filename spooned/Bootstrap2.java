import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
/**
 * Second stage bootstrap. This is where the majority of the work happens.
 *
 * @author James Duncan Davidson (duncan@apache.org);
 */
public class Bootstrap2 {
    private static java.lang.String base = "../";

    private static java.lang.String crimsonSources = "../../../xml-crimson/src";// relative to base


    private static java.lang.String[] modules = new java.lang.String[]{ "copy", "echo", "jar", "javac", "buildtarget" };

    /**
     * Command line entry point.
     */
    public static void main(java.lang.String[] args) throws java.lang.Exception {
        long startTime = java.lang.System.currentTimeMillis();
        java.lang.System.out.println("Starting Bootstrap2....");
        // ------------------------------------------------------------
        // first create dirs that we need for strapping
        // ------------------------------------------------------------
        Bootstrap2.mkdir(Bootstrap2.base + "bootstrap/temp");
        Bootstrap2.mkdir(Bootstrap2.base + "bootstrap/temp/crimson");
        Bootstrap2.mkdir(Bootstrap2.base + "bootstrap/temp/main");
        Bootstrap2.mkdir(Bootstrap2.base + "bootstrap/temp/tasks");
        Bootstrap2.mkdir(Bootstrap2.base + "bootstrap/temp/taskjars");
        for (int i = 0; i < Bootstrap2.modules.length; i++) {
            Bootstrap2.mkdir((Bootstrap2.base + "bootstrap/temp/tasks/") + Bootstrap2.modules[i]);
        }
        // ------------------------------------------------------------
        // build crimson, but only if it hasn't been built yet since
        // 127 class files takes more seconds than I like to wait.
        // ------------------------------------------------------------
        if (!new java.io.File(Bootstrap2.base + "bootstrap/temp/crimson/javax").exists()) {
            java.util.Vector v1 = Bootstrap2.getSources(Bootstrap2.base + Bootstrap2.crimsonSources);
            Bootstrap2.doCompile(Bootstrap2.base + "bootstrap/temp/crimson", v1);
        }
        // ------------------------------------------------------------
        // build the main thing
        // ------------------------------------------------------------
        java.util.Vector v2 = Bootstrap2.getSources(Bootstrap2.base + "source/main");
        Bootstrap2.doCompile(Bootstrap2.base + "bootstrap/temp/main", v2);
        // ------------------------------------------------------------
        // now build each of the needed peices into their
        // areas within the strapping area
        // ------------------------------------------------------------
        for (int i = 0; i < Bootstrap2.modules.length; i++) {
            Bootstrap2.buildModule(Bootstrap2.modules[i]);
        }
        // ------------------------------------------------------------
        // now, set classpaths and launch an Ant build to
        // have Ant build itself nicely
        // ------------------------------------------------------------
        java.lang.System.out.println();
        java.lang.System.out.println("-------------------------------------------");
        java.lang.System.out.println("STARTING REAL BUILD");
        java.lang.System.out.println("-------------------------------------------");
        java.lang.System.out.println();
        java.lang.String[] cmdarray = new java.lang.String[10];
        cmdarray[0] = "java";
        cmdarray[1] = "-cp";
        cmdarray[2] = (((Bootstrap2.base + "bootstrap/temp/main") + java.io.File.pathSeparator) + Bootstrap2.base) + "bootstrap/temp/crimson";
        cmdarray[3] = "org.apache.ant.cli.Main";
        cmdarray[4] = "-taskpath";
        cmdarray[5] = Bootstrap2.base + "bootstrap/temp/taskjars";
        cmdarray[6] = "-buildfile";
        cmdarray[7] = Bootstrap2.base + "source/main.ant";
        cmdarray[8] = "-target";
        cmdarray[9] = "default";
        Bootstrap.runCommand(cmdarray, args);
        java.lang.System.out.println();
        java.lang.System.out.println("-------------------------------------------");
        java.lang.System.out.println("FINISHED WITH REAL BUILD");
        java.lang.System.out.println("-------------------------------------------");
        java.lang.System.out.println();
        // ------------------------------------------------------------
        // Remove Temporary classes
        // ------------------------------------------------------------
        // delete(tempDirName);
        // ------------------------------------------------------------
        // Print Closer
        // ------------------------------------------------------------
        long endTime = java.lang.System.currentTimeMillis();
        long elapsd = endTime - startTime;
        java.lang.System.out.println(((("Bootstrap Time: " + (elapsd / 1000)) + ".") + (elapsd % 1000)) + " seconds");
    }

    private static void mkdir(java.lang.String arg) {
        java.io.File dir = new java.io.File(arg);
        if (dir.exists() && (!dir.isDirectory())) {
            java.lang.System.out.println((("Oh, horrors! Dir " + arg) + " ") + "doesn't seem to be a dir... Stop!");
            java.lang.System.exit(1);
        }
        if (!dir.exists()) {
            java.lang.System.out.println("Making dir: " + arg);
            dir.mkdir();
        }
    }

    private static void buildModule(java.lang.String arg) {
        java.lang.System.out.println("Building " + arg);
        // get all sources and hand them off to the compiler to
        // build over into destination
        java.util.Vector v = Bootstrap2.getSources((Bootstrap2.base + "source/coretasks/") + arg);
        if (v.size() > 0) {
            Bootstrap2.doCompile((Bootstrap2.base + "bootstrap/temp/tasks/") + arg, v);
        }
        // move taskdef.properties for the module
        Bootstrap2.copyfile(((Bootstrap2.base + "source/coretasks/") + arg) + "/taskdef.properties", ((Bootstrap2.base + "bootstrap/temp/tasks/") + arg) + "/taskdef.properties");
        // jar up tasks
        try {
            Bootstrap2.jarDir(new java.io.File((Bootstrap2.base + "bootstrap/temp/tasks/") + arg), new java.io.File(((Bootstrap2.base + "bootstrap/temp/taskjars/") + arg) + ".jar"));
        } catch (java.io.IOException ioe) {
            java.lang.System.out.println("problem jar'ing: " + arg);
        }
    }

    private static java.util.Vector getSources(java.lang.String arg) {
        java.io.File sourceDir = new java.io.File(arg);
        java.util.Vector v = new java.util.Vector();
        Bootstrap2.scanDir(sourceDir, v, ".java");
        return v;
    }

    private static void jarDir(java.io.File dir, java.io.File jarfile) throws java.io.IOException {
        java.lang.String[] files = dir.list();
        if (files.length > 0) {
            java.lang.System.out.println("Jaring: " + jarfile);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(jarfile);
            java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(fos, new java.util.jar.Manifest());
            Bootstrap2.jarDir(dir, "", jos);
            jos.close();
        }
    }

    private static void jarDir(java.io.File dir, java.lang.String prefix, java.util.jar.JarOutputStream jos) throws java.io.IOException {
        java.lang.String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            java.io.File f = new java.io.File(dir, files[i]);
            if (f.isDirectory()) {
                java.lang.String zipEntryName;
                if (!prefix.equals("")) {
                    zipEntryName = (prefix + "/") + files[i];
                } else {
                    zipEntryName = files[i];
                }
                java.util.zip.ZipEntry ze = new java.util.zip.ZipEntry(zipEntryName);
                jos.putNextEntry(ze);
                Bootstrap2.jarDir(f, zipEntryName, jos);
            } else {
                java.lang.String zipEntryName;
                if (!prefix.equals("")) {
                    zipEntryName = (prefix + "/") + files[i];
                } else {
                    zipEntryName = files[i];
                }
                java.util.zip.ZipEntry ze = new java.util.zip.ZipEntry(zipEntryName);
                jos.putNextEntry(ze);
                java.io.FileInputStream fis = new java.io.FileInputStream(f);
                int count = 0;
                byte[] buf = new byte[8 * 1024];
                count = fis.read(buf, 0, buf.length);
                while (count != (-1)) {
                    jos.write(buf, 0, count);
                    count = fis.read(buf, 0, buf.length);
                } 
                fis.close();
            }
        }
    }

    private static void scanDir(java.io.File dir, java.util.Vector v, java.lang.String endsWith) {
        java.lang.String[] files = dir.list();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            java.io.File f = new java.io.File(dir, files[i]);
            if (f.isDirectory()) {
                Bootstrap2.scanDir(f, v, endsWith);
            } else if (files[i].endsWith(endsWith)) {
                v.addElement(f);
            }
        }
    }

    private static void doCompile(java.lang.String dest, java.util.Vector sources) {
        java.lang.System.out.println((("   Compiling " + sources.size()) + " files to ") + dest);
        // XXX This should be more forgiving about compiling wherever
        // under whatever compiler, but this works so...
        sun.tools.javac.Main compiler = new sun.tools.javac.Main(java.lang.System.out, "javac");
        java.lang.String[] args = new java.lang.String[sources.size() + 4];
        args[0] = "-classpath";
        args[1] = (((Bootstrap2.base + "bootstrap/temp/main") + java.io.File.pathSeparator) + Bootstrap2.base) + "bootstrap/temp/crimson";
        args[2] = "-d";
        args[3] = dest;
        for (int i = 0; i < sources.size(); i++) {
            args[4 + i] = ((java.io.File) (sources.elementAt(i))).toString();
        }
        // System.out.print("javac ");
        // for (int i = 0; i < args.length; i++) {
        // System.out.print(args[i] + " ");
        // }
        // System.out.println();
        compiler.compile(args);
    }

    private static void copyfile(java.lang.String from, java.lang.String dest) {
        java.io.File fromF = new java.io.File(from);
        java.io.File destF = new java.io.File(dest);
        if (fromF.exists()) {
            java.lang.System.out.println("   Copying " + from);
            try {
                java.io.FileInputStream in = new java.io.FileInputStream(fromF);
                java.io.FileOutputStream out = new java.io.FileOutputStream(destF);
                byte[] buf = new byte[1024 * 16];
                int count = 0;
                count = in.read(buf, 0, buf.length);
                if (count != (-1)) {
                    out.write(buf, 0, count);
                    count = in.read(buf, 0, buf.length);
                }
                in.close();
                out.close();
            } catch (java.io.IOException ioe) {
                java.lang.System.out.println("OUCH: " + from);
                java.lang.System.out.println(ioe);
            }
        }
    }
}