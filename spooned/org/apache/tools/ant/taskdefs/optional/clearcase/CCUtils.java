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
package org.apache.tools.ant.taskdefs.optional.clearcase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;
/**
 * Helper methods related to clearcase commands.
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public final class CCUtils {
    public static final java.lang.String DEFAULT_COMMENT = "\"Automatic operation from Jakarta Ant\"";

    private static final org.apache.tools.ant.util.regexp.RegexpMatcherFactory __reFactory = new org.apache.tools.ant.util.regexp.RegexpMatcherFactory();

    /**
     * the matchers cache: pattern/matcher
     */
    private static final java.util.Hashtable matchers = new java.util.Hashtable();

    private org.apache.tools.ant.Task task;

    public CCUtils(org.apache.tools.ant.Task task) {
        this.task = task;
    }

    /**
     * return a group of matches of a given RE in a string.
     *
     * @param pattern
     * 		the pattern to match in the input data.
     * @param input
     * 		the data where to look for the pattern.
     * @return the group of matches if any, 0 being the full match
    and the rest being parenthesized expressions. <tt>null</tt>
    if there are no matches.
     */
    public java.util.Vector matches(java.lang.String pattern, java.lang.String input) {
        org.apache.tools.ant.util.regexp.RegexpMatcher matcher = ((org.apache.tools.ant.util.regexp.RegexpMatcher) (org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.matchers.get(pattern)));
        if (matcher == null) {
            matcher = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.__reFactory.newRegexpMatcher();
            matcher.setPattern(pattern);
            org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.matchers.put(pattern, matcher);
        }
        return matcher.getGroups(input);
    }

    /**
     * Try to resolve a symbolic link if it is one.
     *
     * @param toresolve
     * 		the symbolic link to resolve.
     * @return the resolved link if it is a symbolic link, otherwise
    return the original link.
     */
    public java.io.File resolveSymbolicLink(java.io.File toresolve) throws java.lang.Exception {
        java.lang.String[] args = new java.lang.String[]{ "ls", "-l", toresolve.getAbsolutePath() };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
        java.util.Vector groups = matches("symbolic link(.*)-->(.*)", res.getStdout());
        if (groups == null) {
            return toresolve;// or null ?

        }
        java.lang.String path = ((java.lang.String) (groups.elementAt(2)));
        path = path.trim();
        java.io.File resolved = new java.io.File(path);
        if (!resolved.isAbsolute()) {
            resolved = new java.io.File(toresolve.getParent(), path);
        }
        return resolved;
    }

    /**
     * Move a file to another. (ie rename)
     */
    public void move(java.io.File from, java.io.File to) throws java.lang.Exception {
        java.lang.String[] args = new java.lang.String[]{ "move", "-nc", from.getPath(), to.getPath() };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
    }

    /**
     * return the list of checkedout files in a given viewpath.
     *
     * @param viewpath
     * 		the path to the view/directory to look for
     * 		checkedout files.
     * @param recurse
     * 		<tt>true</tt> to look for files recursively,
     * 		otherwise <tt>false</tt>
     * @return the list of checkedout files in the view (full pathname).
     */
    public java.util.Hashtable lsco(java.io.File viewpath, boolean recurse) {
        java.lang.String recurseParam = (recurse) ? "-r" : "";
        java.lang.String fullpath = viewpath.getAbsolutePath();
        // @fixme is -cvi conflicting with -r ?
        java.lang.String[] args = new java.lang.String[]{ "lsco", recurseParam, "-cvi", "-s", "-me", fullpath };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
        java.util.Vector lines = res.getStdoutLines();
        java.util.Hashtable map = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.toFiles(lines);
        return map;
    }

    /**
     * Transform a set of paths into canonical paths.
     * Typically this should be used to transform a set of
     * output lines by cleartool representing file paths.
     */
    public static java.util.Hashtable toFiles(java.util.Vector paths) {
        java.util.Hashtable map = new java.util.Hashtable();
        for (int i = 0; i < paths.size(); i++) {
            java.lang.String path = ((java.lang.String) (paths.elementAt(i)));
            try {
                // the path is normally the full path, we normally
                // not need to do a new File(viewpath, path)
                java.io.File f = new java.io.File(path);
                path = f.getCanonicalPath();
                map.put(path, path);
            } catch (java.io.IOException e) {
                // assume it's not a file...
            }
        }
        return map;
    }

    /**
     * Returns the list of files that are *not* checked out.
     *
     * @see #lsco(File, boolean)
     */
    public java.util.Hashtable lsnco(java.io.File viewpath) {
        java.lang.String[] args = new java.lang.String[]{ "find", viewpath.getAbsolutePath(), "-type", "f", "-cvi", "-nxn", "-print" };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        java.util.Vector lines = res.getStdoutLines();
        java.util.Hashtable all = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.toFiles(lines);
        java.util.Hashtable co = lsco(viewpath, true);
        // remove the co files
        java.util.Enumeration keys = co.keys();
        while (keys.hasMoreElements()) {
            java.lang.Object path = keys.nextElement();
            java.lang.Object o = all.remove(path);
            if (o == null) {
                // oops how come a co file is not found by find ?
            }
        } 
        return all;
    }

    /**
     * returns the list of private files in the view
     */
    public java.util.Hashtable lsprivate(java.io.File viewpath) {
        // for a snapshot view, we must use ls -r -view_only
        return null;
    }

    public void checkin(java.io.File file) {
        java.lang.String[] args = new java.lang.String[]{ "ci", "-nc", "-identical", file.getAbsolutePath() };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
    }

    public void checkout(java.io.File file) {
        java.lang.String[] args = new java.lang.String[]{ "co", "-nc", "-unreserved", file.getAbsolutePath() };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
    }

    public void uncheckout(java.io.File file) {
        java.lang.String[] args = new java.lang.String[]{ "unco", "-rm", file.getAbsolutePath() };
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
    }

    /**
     * Helper method to execute a given cleartool command.
     *
     * @param args
     * 		the parameters used to execute cleartool.
     * @return the result of the command.
     */
    public static org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult cleartool(java.lang.String[] args) {
        java.lang.String[] nargs = new java.lang.String[args.length + 1];
        nargs[0] = "cleartool";
        java.lang.System.arraycopy(args, 0, nargs, 1, args.length);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.ByteArrayOutputStream err = new java.io.ByteArrayOutputStream();
        org.apache.tools.ant.taskdefs.ExecuteStreamHandler handler = new org.apache.tools.ant.taskdefs.PumpStreamHandler(out, err);
        org.apache.tools.ant.taskdefs.Execute exe = new org.apache.tools.ant.taskdefs.Execute(handler);
        exe.setCommandline(nargs);
        try {
            int retcode = exe.execute();
            return new org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult(retcode, out.toString(), err.toString());
        } catch (java.io.IOException e) {
            throw new org.apache.tools.ant.BuildException(e);
        }
    }

    /**
     * Create the comment file used by cleartool commands.
     */
    public static java.io.File createCommentFile(java.lang.String comment) {
        org.apache.tools.ant.util.FileUtils futils = org.apache.tools.ant.util.FileUtils.newFileUtils();
        java.io.File f = futils.createTempFile("ant_cc", ".tmp", new java.io.File("."));
        java.io.Writer writer = null;
        try {
            writer = new java.io.BufferedWriter(new java.io.FileWriter(f));
            writer.write(comment);
            writer.flush();
        } catch (java.io.IOException e) {
            throw new org.apache.tools.ant.BuildException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (java.io.IOException e) {
                }
            }
        }
        return f;
    }
}