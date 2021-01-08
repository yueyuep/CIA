package vmt.enity;

import com.github.javaparser.ast.expr.SimpleName;
import vmt.enity.relations.VariableRelation;

import java.util.List;

/**
 * @Author:yueyue on 2020/12/19 12:02
 * @Param:
 * @return:
 * @Description: variable method tree statement of method
 */
/*statement of method*/
public class VmtS {
    private SimpleName val1;
    private SimpleName val2;
    /*todo */
    List<VariableRelation> relations;

    public VmtS(SimpleName val1, SimpleName val2, List<VariableRelation> relations) {
        this.val1 = val1;
        this.val2 = val2;
        this.relations = relations;
    }

    public SimpleName getVal1() {
        return val1;
    }

    public SimpleName getVal2() {
        return val2;
    }

    public List<VariableRelation> getRelations() {
        return relations;
    }

    public void setVal1(SimpleName val1) {
        this.val1 = val1;
    }

    public void setVal2(SimpleName val2) {
        this.val2 = val2;
    }

    public void setMethodCallExprList(List<VariableRelation> relations) {
        this.relations = relations;
    }
}
