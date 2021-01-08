package change;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;

import java.util.HashMap;
import java.util.List;

/**
 * Create by yueyue on 2020/12/18
 */
public class MethodChange {
    String classpath = "";
    private String methodName = "";
    private CtMethod ctMethod = null;
    HashMap<String, CtVariable> stringCtParameterHashMap = new HashMap<>();


    public MethodChange(CtMethod ctMethod, List<CtVariable> variables) {
        this.ctMethod = ctMethod;
        this.methodName = ctMethod.getSimpleName();
        variables.forEach(ctVariable -> {
            stringCtParameterHashMap.put(ctVariable.getSimpleName(), ctVariable);
        });
        this.classpath = ctMethod.getParent(CtClass.class).getSimpleName();

    }

    public String getClasspath() {
        return classpath;
    }

    public String getMethodName() {
        return methodName;
    }

    public CtMethod getCtMethod() {
        return ctMethod;
    }

    public HashMap<String, CtVariable> getStringCtParameterHashMap() {
        return stringCtParameterHashMap;
    }

}
