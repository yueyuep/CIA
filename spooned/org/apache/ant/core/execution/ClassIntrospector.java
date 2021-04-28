/* The Apache Software License, Version 1.1

Copyright (c) 2000 The Apache Software Foundation.  All rights
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
package org.apache.ant.core.execution;
import java.lang.reflect.*;
import java.util.*;
/**
 * Introspects a class and builds a set of objects to assist in intospecting the
 * class.
 *
 * @author Stefan Bodewig <a href="mailto:stefan.bodewig@megabit.net">stefan.bodewig@megabit.net</a>
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class ClassIntrospector {
    /**
     * holds the types of the attributes that could be set.
     */
    private java.util.Hashtable attributeTypes;

    /**
     * holds the attribute setter methods.
     */
    private java.util.Hashtable attributeSetters;

    /**
     * Holds the types of nested elements that could be created.
     */
    private java.util.Hashtable nestedTypes;

    /**
     * Holds methods to create nested elements.
     */
    private java.util.Hashtable nestedCreators;

    /**
     * The method to add PCDATA stuff.
     */
    private java.lang.reflect.Method addText = null;

    /**
     * The Class that's been introspected.
     */
    private java.lang.Class bean;

    /**
     * returns the boolean equivalent of a string, which is considered true
     * if either "on", "true", or "yes" is found, ignoring case.
     */
    public static boolean toBoolean(java.lang.String s) {
        return (s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true")) || s.equalsIgnoreCase("yes");
    }

    public ClassIntrospector(final java.lang.Class bean, java.util.Map converters) {
        attributeTypes = new java.util.Hashtable();
        attributeSetters = new java.util.Hashtable();
        nestedTypes = new java.util.Hashtable();
        nestedCreators = new java.util.Hashtable();
        this.bean = bean;
        java.lang.reflect.Method[] methods = bean.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final java.lang.reflect.Method m = methods[i];
            final java.lang.String name = m.getName();
            java.lang.Class returnType = m.getReturnType();
            java.lang.Class[] args = m.getParameterTypes();
            if ((("addText".equals(name) && java.lang.Void.TYPE.equals(returnType)) && (args.length == 1)) && java.lang.String.class.equals(args[0])) {
                addText = methods[i];
            } else if (((name.startsWith("set") && java.lang.Void.TYPE.equals(returnType)) && (args.length == 1)) && (!args[0].isArray())) {
                java.lang.String propName = getPropertyName(name, "set");
                org.apache.ant.core.execution.ClassIntrospector.AttributeSetter as = createAttributeSetter(m, args[0], converters);
                if (as != null) {
                    attributeTypes.put(propName, args[0]);
                    attributeSetters.put(propName, as);
                }
            } else if (((name.startsWith("create") && (!returnType.isArray())) && (!returnType.isPrimitive())) && (args.length == 0)) {
                java.lang.String propName = getPropertyName(name, "create");
                nestedTypes.put(propName, returnType);
                nestedCreators.put(propName, new org.apache.ant.core.execution.ClassIntrospector.NestedCreator() {
                    public java.lang.Object create(java.lang.Object parent) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                        return m.invoke(parent, new java.lang.Object[]{  });
                    }
                });
            } else if (((((name.startsWith("add") && java.lang.Void.TYPE.equals(returnType)) && (args.length == 1)) && (!java.lang.String.class.equals(args[0]))) && (!args[0].isArray())) && (!args[0].isPrimitive())) {
                try {
                    final java.lang.reflect.Constructor c = args[0].getConstructor(new java.lang.Class[]{  });
                    java.lang.String propName = getPropertyName(name, "add");
                    nestedTypes.put(propName, args[0]);
                    nestedCreators.put(propName, new org.apache.ant.core.execution.ClassIntrospector.NestedCreator() {
                        public java.lang.Object create(java.lang.Object parent) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, java.lang.InstantiationException {
                            java.lang.Object o = c.newInstance(new java.lang.Object[]{  });
                            m.invoke(parent, new java.lang.Object[]{ o });
                            return o;
                        }
                    });
                } catch (java.lang.NoSuchMethodException nse) {
                }
            }
        }
    }

    /**
     * Sets the named attribute.
     */
    public void setAttribute(java.lang.Object element, java.lang.String attributeName, java.lang.String value) throws org.apache.ant.core.execution.ClassIntrospectionException, org.apache.ant.core.execution.ConversionException {
        org.apache.ant.core.execution.ClassIntrospector.AttributeSetter as = ((org.apache.ant.core.execution.ClassIntrospector.AttributeSetter) (attributeSetters.get(attributeName)));
        if (as == null) {
            java.lang.String msg = ((("Class " + element.getClass().getName()) + " doesn\'t support the \"") + attributeName) + "\" attribute";
            throw new ClassIntrospectionException(msg);
        }
        try {
            as.set(element, value);
        } catch (java.lang.IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new ClassIntrospectionException(ie);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            java.lang.Throwable t = ite.getTargetException();
            if (t instanceof ClassIntrospectionException) {
                throw ((ClassIntrospectionException) (t));
            }
            throw new ClassIntrospectionException(t);
        }
    }

    /**
     * Adds PCDATA areas.
     */
    public void addText(java.lang.Object element, java.lang.String text) throws org.apache.ant.core.execution.ClassIntrospectionException {
        if (addText == null) {
            java.lang.String msg = ("Class " + element.getClass().getName()) + " doesn't support nested text elements";
            throw new ClassIntrospectionException(msg);
        }
        try {
            addText.invoke(element, new java.lang.String[]{ text });
        } catch (java.lang.IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new ClassIntrospectionException(ie);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            java.lang.Throwable t = ite.getTargetException();
            if (t instanceof ClassIntrospectionException) {
                throw ((ClassIntrospectionException) (t));
            }
            throw new ClassIntrospectionException(t);
        }
    }

    public boolean supportsNestedElement(java.lang.String elementName) {
        return nestedCreators.containsKey(elementName);
    }

    /**
     * Creates a named nested element.
     */
    public java.lang.Object createElement(java.lang.Object element, java.lang.String elementName) throws org.apache.ant.core.execution.ClassIntrospectionException {
        org.apache.ant.core.execution.ClassIntrospector.NestedCreator nc = ((org.apache.ant.core.execution.ClassIntrospector.NestedCreator) (nestedCreators.get(elementName)));
        if (nc == null) {
            java.lang.String msg = ((("Class " + element.getClass().getName()) + " doesn\'t support the nested \"") + elementName) + "\" element";
            throw new ClassIntrospectionException(msg);
        }
        try {
            return nc.create(element);
        } catch (java.lang.IllegalAccessException ie) {
            // impossible as getMethods should only return public methods
            throw new ClassIntrospectionException(ie);
        } catch (java.lang.InstantiationException ine) {
            // impossible as getMethods should only return public methods
            throw new ClassIntrospectionException(ine);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            java.lang.Throwable t = ite.getTargetException();
            if (t instanceof ClassIntrospectionException) {
                throw ((ClassIntrospectionException) (t));
            }
            throw new ClassIntrospectionException(t);
        }
    }

    /**
     * returns the type of a named nested element.
     */
    public java.lang.Class getElementType(java.lang.String elementName) throws org.apache.ant.core.execution.ClassIntrospectionException {
        java.lang.Class nt = ((java.lang.Class) (nestedTypes.get(elementName)));
        if (nt == null) {
            java.lang.String msg = ((("Class " + bean.getName()) + " doesn\'t support the nested \"") + elementName) + "\" element";
            throw new ClassIntrospectionException(msg);
        }
        return nt;
    }

    /**
     * returns the type of a named attribute.
     */
    public java.lang.Class getAttributeType(java.lang.String attributeName) throws org.apache.ant.core.execution.ClassIntrospectionException {
        java.lang.Class at = ((java.lang.Class) (attributeTypes.get(attributeName)));
        if (at == null) {
            java.lang.String msg = ((("Class " + bean.getName()) + " doesn\'t support the \"") + attributeName) + "\" attribute";
            throw new ClassIntrospectionException(msg);
        }
        return at;
    }

    /**
     * Does the introspected class support PCDATA?
     */
    public boolean supportsCharacters() {
        return addText != null;
    }

    /**
     * Return all attribues supported by the introspected class.
     */
    public java.util.Enumeration getAttributes() {
        return attributeSetters.keys();
    }

    /**
     * Return all nested elements supported by the introspected class.
     */
    public java.util.Enumeration getNestedElements() {
        return nestedTypes.keys();
    }

    /**
     * Create a proper implementation of AttributeSetter for the given
     * attribute type.
     */
    private org.apache.ant.core.execution.ClassIntrospector.AttributeSetter createAttributeSetter(final java.lang.reflect.Method m, final java.lang.Class arg, java.util.Map converters) {
        if ((converters != null) && converters.containsKey(arg)) {
            // we have a converter to use to convert the strign
            // value of into something the set method expects.
            final Converter converter = ((Converter) (converters.get(arg)));
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, org.apache.ant.core.execution.ClassIntrospectionException, org.apache.ant.core.execution.ConversionException {
                    m.invoke(parent, new java.lang.Object[]{ converter.convert(value, arg) });
                }
            };
        } else if (java.lang.String.class.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.String[]{ value });
                }
            };
            // now for the primitive types, use their wrappers
        } else if (java.lang.Character.class.equals(arg) || java.lang.Character.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Character[]{ new java.lang.Character(value.charAt(0)) });
                }
            };
        } else if (java.lang.Byte.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Byte[]{ new java.lang.Byte(value) });
                }
            };
        } else if (java.lang.Short.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Short[]{ new java.lang.Short(value) });
                }
            };
        } else if (java.lang.Integer.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Integer[]{ new java.lang.Integer(value) });
                }
            };
        } else if (java.lang.Long.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Long[]{ new java.lang.Long(value) });
                }
            };
        } else if (java.lang.Float.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Float[]{ new java.lang.Float(value) });
                }
            };
        } else if (java.lang.Double.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Double[]{ new java.lang.Double(value) });
                }
            };
        } else if (java.lang.Boolean.class.equals(arg) || java.lang.Boolean.TYPE.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException {
                    m.invoke(parent, new java.lang.Boolean[]{ new java.lang.Boolean(org.apache.ant.core.execution.ClassIntrospector.toBoolean(value)) });
                }
            };
            // Class doesn't have a String constructor but a decent factory method
        } else if (java.lang.Class.class.equals(arg)) {
            return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, org.apache.ant.core.execution.ClassIntrospectionException {
                    try {
                        m.invoke(parent, new java.lang.Class[]{ java.lang.Class.forName(value) });
                    } catch (java.lang.ClassNotFoundException ce) {
                        throw new ClassIntrospectionException(ce);
                    }
                }
            };
            // worst case. look for a public String constructor and use it
        } else {
            try {
                final java.lang.reflect.Constructor c = arg.getConstructor(new java.lang.Class[]{ java.lang.String.class });
                return new org.apache.ant.core.execution.ClassIntrospector.AttributeSetter() {
                    public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, org.apache.ant.core.execution.ClassIntrospectionException {
                        try {
                            m.invoke(parent, new java.lang.Object[]{ c.newInstance(new java.lang.String[]{ value }) });
                        } catch (java.lang.InstantiationException ie) {
                            throw new ClassIntrospectionException(ie);
                        }
                    }
                };
            } catch (java.lang.NoSuchMethodException nme) {
            }
        }
        return null;
    }

    /**
     * extract the name of a property from a method name - subtracting
     * a given prefix.
     */
    private java.lang.String getPropertyName(java.lang.String methodName, java.lang.String prefix) {
        int start = prefix.length();
        return methodName.substring(start).toLowerCase();
    }

    private interface NestedCreator {
        public java.lang.Object create(java.lang.Object parent) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, java.lang.InstantiationException;
    }

    private interface AttributeSetter {
        public void set(java.lang.Object parent, java.lang.String value) throws java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, org.apache.ant.core.execution.ClassIntrospectionException, org.apache.ant.core.execution.ConversionException;
    }
}