package vmt.enity;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import java.util.*;

/**
 * @Author:yueyue on 2020/12/22 10:52
 * @Param:
 * @return:
 * @Description: VMT = <var1, var2, MT>
 */
public class VMT {
    /*node instance simpleName*/
    Node statement = null;
    Node assigned = null;
    List<Node> assigner = new LinkedList<>();

    List<MT> msSet = new ArrayList<>();

    private static HashMap<Range, VMT> rangeVMTHashMap = new HashMap<>();
    private static Map<String, VMT> stringVMTMap = new HashMap<>();

    public VMT(Node statement) {
        this.statement = statement;
        /*init assigner and assigned*/
        if (statement instanceof Statement || statement instanceof CompilationUnit) {
            /*
            filter ComplicationUnit、ExpressionSTmt、forStmt、WhileStmt
             */
        } else {
            travelForSimpleName(statement);

        }

    }

    public void travelForSimpleName(Node node) {
        if (node instanceof SimpleName && assigned == null) {
            assigned = node;
            return;
        } else if (node instanceof SimpleName) {
            assigner.add(node);
            return;
        }
        if (!node.getChildNodes().isEmpty()) {
            for (Node child : node.getChildNodes()) {
                travelForSimpleName(child);
            }
        }
    }

    public static VMT newInstance(Node node) {
        String key = String.valueOf(node.hashCode());
        if (node.getRange().isPresent()) {
            if (rangeVMTHashMap.keySet().contains(node.getRange().get())) {
                return rangeVMTHashMap.get(node.getRange().get());


            } else {
                VMT vmt = new VMT(node);
                rangeVMTHashMap.put(node.getRange().get(), vmt);
                return vmt;
            }
        } else {
            if (stringVMTMap.containsKey(key)) {
                return stringVMTMap.get(key);
            } else {
                VMT vmt = new VMT(node);
                stringVMTMap.put(key, vmt);
                return vmt;
            }
        }
    }

    public static void clearNode() {
        stringVMTMap.clear();
        rangeVMTHashMap.clear();
    }

    public Node getStatement() {
        return statement;
    }

    public void setStatement(Node statement) {
        this.statement = statement;
    }

    public List<MT> getMsSet() {
        return msSet;
    }

    public List<Node> getAssigner() {
        return assigner;
    }

    public Node getAssigned() {
        return assigned;
    }
}
