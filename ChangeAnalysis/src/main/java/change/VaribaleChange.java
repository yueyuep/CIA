package change;

import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.support.reflect.code.CtStatementImpl;

import java.io.Serializable;

/**
 * Create by yueyue on 2021/1/9
 * variable change set
 */
public class VaribaleChange implements Serializable {
    private String filepath = "";
    private int linenumber = 0;
    private CtMethod ctMethod = null;
    private CtElement variable = null;
    private String methodNmame = "";
    private String variableName = "";

    /*todo srcnode?distnode*/
    public VaribaleChange(CtMethod ctMethod, Operation op) {
        this.filepath = op.getSrcNode().getPosition().getFile().toString();
        this.linenumber = op.getSrcNode().getPosition().getLine();
        /*TODO FIX variableType
         *
         * op.getSrcnode->op.getNode*/
        CtElement ctElement = op.getNode();
        this.ctMethod = ctMethod;
        try {
            this.variable = ctElement;
            this.variableName = variable.prettyprint();
        } catch (Exception e) {
            System.out.println("change.VaribaleChange:" + e.getCause());
        }

        this.methodNmame = ctMethod.getSimpleName();
    }

    public VaribaleChange(CtMethod ctMethod, CtVariable ctVariable) {

        this.filepath = ctVariable.getPosition().getFile().toString();
        this.linenumber = ctVariable.getPosition().getLine();
        this.ctMethod = ctMethod;
        try {
            this.variable = ctVariable;
            this.variableName = variable.prettyprint();
        } catch (Exception e) {
            System.out.println("change.VaribaleChange:" + e.getCause());
        }
        this.methodNmame = ctMethod.getSimpleName();
    }

    public static VaribaleChange newInstance(CtMethod ctMethod, Operation op) {
        /*TODO FIX support multi changetype
         *
         * support multi change type
         * only include changeType：{CtStatementImpl,CtParameter}
         *
         */
        if (op.getSrcNode() instanceof CtStatementImpl || op.getSrcNode() instanceof CtParameter) {
            return new VaribaleChange(ctMethod, op);
        } else {
            return null;
        }
    }

    public CtMethod getCtMethod() {
        return ctMethod;
    }

    public CtElement getVariable() {
        return variable;
    }

    public String getMethodNmame() {
        return methodNmame;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getLinenumber() {
        return linenumber;
    }

    public String getFilepath() {
        return filepath;
    }
}
