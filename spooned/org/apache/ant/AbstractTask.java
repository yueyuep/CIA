package org.apache.ant;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
/**
 * Superclass of all Tasks. All tasks extend from this.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public abstract class AbstractTask {
    // -----------------------------------------------------------------
    // PROTECTED DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     *
     */
    protected org.apache.ant.Project project;

    // -----------------------------------------------------------------
    // ABSTRACT PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     *
     */
    public abstract boolean execute() throws org.apache.ant.AntException;

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Used by the system to set the attributes which then get reflected
     * into the particular implementation class
     */
    public void setAttributes(java.util.Hashtable attributes) {
        java.lang.Class clazz = this.getClass();
        java.beans.BeanInfo bi;
        try {
            bi = java.beans.Introspector.getBeanInfo(clazz);
        } catch (java.beans.IntrospectionException ie) {
            java.lang.System.out.println("Can't reflect on: " + clazz);
            // XXX exception out
            return;
        }
        java.beans.PropertyDescriptor[] pda = bi.getPropertyDescriptors();
        for (int i = 0; i < pda.length; i++) {
            java.beans.PropertyDescriptor pd = pda[i];
            java.lang.String property = pd.getName();
            java.lang.Object o = attributes.get(property);
            if (o != null) {
                java.lang.String value = ((java.lang.String) (o));
                java.lang.reflect.Method setMethod = pd.getWriteMethod();
                if (setMethod != null) {
                    java.lang.Class[] ma = setMethod.getParameterTypes();
                    if (ma.length == 1) {
                        java.lang.Class c = ma[0];
                        if (c.getName().equals("java.lang.String")) {
                            try {
                                setMethod.invoke(this, new java.lang.String[]{ value });
                            } catch (java.lang.Exception e) {
                                // XXX bad bad bad -- narrow to exact exceptions
                                java.lang.System.out.println("OUCH: " + e);
                                // XXX exception out.
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Used by system to set the project.
     */
    public void setProject(Project project) {
        this.project = project;
    }
}