package org.apache.ant.engine;
import java.util.*;
public class HierarchicalHashtable extends java.util.Hashtable {
    private org.apache.ant.engine.HierarchicalHashtable parent;

    public HierarchicalHashtable() {
        this(null);
    }

    public HierarchicalHashtable(org.apache.ant.engine.HierarchicalHashtable parent) {
        super();
        this.parent = parent;
    }

    public org.apache.ant.engine.HierarchicalHashtable getParent() {
        return parent;
    }

    public void setParent(org.apache.ant.engine.HierarchicalHashtable parent) {
        this.parent = parent;
    }

    public java.util.List getPropertyNames() {
        java.util.ArrayList list = new java.util.ArrayList();
        java.util.Enumeration e = keys();
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        } 
        if (getParent() != null) {
            list.addAll(getParent().getPropertyNames());
        }
        return list;
    }

    public java.lang.Object getPropertyValue(java.lang.String name) {
        java.lang.Object value = get(name);
        if ((value == null) && (getParent() != null)) {
            return getParent().getPropertyValue(name);
        }
        return value;
    }

    public void setPropertyValue(java.lang.String name, java.lang.Object value) {
        put(name, value);
    }

    public void removePropertyValue(java.lang.String name) {
        java.lang.Object value = get(name);
        if ((value == null) && (getParent() != null)) {
            getParent().removePropertyValue(name);
        }
        if (value != null) {
            remove(name);
        }
    }
}