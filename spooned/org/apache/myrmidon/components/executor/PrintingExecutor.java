/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.executor;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskException;
public class PrintingExecutor extends org.apache.myrmidon.components.executor.AspectAwareExecutor {
    protected void doExecute(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.api.Task task) throws org.apache.myrmidon.api.TaskException {
        final java.lang.StringBuffer sb = new java.lang.StringBuffer();
        printConfiguration(taskModel, 0, sb);
        java.lang.System.out.println(sb.toString());
    }

    private void printConfiguration(final org.apache.avalon.framework.configuration.Configuration taskModel, final int level, final java.lang.StringBuffer sb) {
        for (int i = 0; i < level; i++) {
            sb.append(' ');
        }
        sb.append('<');
        sb.append(taskModel.getName());
        final java.lang.String[] names = taskModel.getAttributeNames();
        for (int i = 0; i < names.length; i++) {
            final java.lang.String name = names[i];
            final java.lang.String value = taskModel.getAttribute(name, null);
            sb.append(' ');
            sb.append(name);
            sb.append("=\"");
            sb.append(value);
            sb.append('\"');
        }
        final org.apache.avalon.framework.configuration.Configuration[] children = taskModel.getChildren();
        if (0 == children.length) {
            sb.append("/>\n");
        } else {
            sb.append(">\n");
            for (int i = 0; i < children.length; i++) {
                printConfiguration(children[i], level + 1, sb);
            }
            for (int i = 0; i < level; i++) {
                sb.append(' ');
            }
            sb.append("</");
            sb.append(taskModel.getName());
            sb.append(">\n");
        }
    }
}