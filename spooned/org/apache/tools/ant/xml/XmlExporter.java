package org.apache.tools.ant.xml;
import java.io.*;
import java.util.*;
public class XmlExporter {
    public void exportProject(Project project, java.io.Writer out) throws java.io.IOException {
        out.write(("<project name=\"" + project.getName()) + "\">\n");
        java.util.Iterator itr = project.getTargets().iterator();
        while (itr.hasNext()) {
            Target target = ((Target) (itr.next()));
            writeTarget(target, out);
        } 
        out.write("</project>\n");
    }

    private void writeTarget(Target target, java.io.Writer out) throws java.io.IOException {
        out.write(((("\t<target name=\"" + target.getName()) + "\" depends=\"") + concat(target.getDepends())) + ">\n");
        out.write("\t</target>\n");
    }

    public java.lang.String concat(java.util.List depends) throws java.io.IOException {
        java.lang.StringBuffer buf = new java.lang.StringBuffer();
        java.util.Iterator itr = depends.iterator();
        while (itr.hasNext()) {
            java.lang.String depend = ((java.lang.String) (itr.next()));
            buf.append(depend);
            if (itr.hasNext()) {
                buf.append(" ");
            }
        } 
        return buf.toString();
    }

    public static void main(java.lang.String[] args) throws java.lang.Exception {
        Workspace workspace = new Workspace(new XmlImporter());
        Project project = workspace.importProject("ant");
        java.io.Writer out = new java.io.OutputStreamWriter(java.lang.System.out);
        new org.apache.tools.ant.xml.XmlExporter().exportProject(project, out);
        out.flush();
    }
}