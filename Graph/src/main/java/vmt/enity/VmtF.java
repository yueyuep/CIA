package vmt.enity;
import com.github.javaparser.ast.body.VariableDeclarator;
import vmt.enity.relations.VariableRelation;
import java.util.List;
/**
 * @Author:yueyue on 2020/12/19 12:00
 * @Param:
 * @return:
 * @Description: variable method tree of class Field
 */
/*class Field*/
public class VmtF  {
    private VariableDeclarator val1 = null;
    private VariableDeclarator val2 = null;
    private VariableRelation variableRelation = null;

    public VmtF() {

    }
    public VmtF(VariableDeclarator val1, VariableDeclarator val2, VariableRelation variableRelation) {
        this.val1 = val1;
        this.val2 = val2;
        this.variableRelation = variableRelation;

    }
    /*initRelation*/
    public void init_re() {

    }
}
