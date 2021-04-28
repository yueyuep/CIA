package vmt.enity;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * @author :yueyue on 2020/12/19 12:01
 * @param :
 * @return:
 * @Description: variable of method
 */
/*class method*/
public class VmtM {
    /*methodbody*/
    private MethodDeclaration methodDeclaration = null;
    private SimpleName methodName = null;
    private NodeList<Parameter> parameters = new NodeList<>();
    /*class path of method*/
    private List<ClassOrInterfaceDeclaration> classPath = new ArrayList<>();
    /*method Vmt*/
    List<VMT> vmts = new ArrayList<>();
    /*method node*/
    List<Node> methodNodelist = new ArrayList<>();


    private static HashMap<Range, VmtM> rangeVmtMHashMap = new HashMap<>();


    public VmtM() {

    }
    public VmtM(MethodDeclaration methodDeclaration) {
        /*todo init all field */

        this.methodDeclaration = methodDeclaration;
        this.methodName = methodDeclaration.getName();
        this.parameters = methodDeclaration.getParameters();
        initClassPath(methodDeclaration);
        try {
            initVmts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static VmtM newInstance(MethodDeclaration method) {
        if (rangeVmtMHashMap.keySet().contains(method.getRange().get())) {
            return rangeVmtMHashMap.get(method.getRange().get());
        } else {
            VmtM vmtM = new VmtM(method);
            rangeVmtMHashMap.put(method.getRange().get(), vmtM);
            return vmtM;
        }
    }

    public static void clearCache() {
        rangeVmtMHashMap.clear();
    }

    /*dp*/
    private void initClassPath(Node method) {
        Node ci = method.getParentNode().get();
        if (ci != null && ci instanceof ClassOrInterfaceDeclaration) {
            classPath.add((ClassOrInterfaceDeclaration) ci);
            initClassPath(ci);
        }
    }

    /*todo: init vmts and keep the relations betwen node and node*/
    /*Exception :FileNotFoundException*/
    public void initVmts() throws Exception {
        /*-------------------CFG-statement------------------*/

        methodNodelist.add(methodDeclaration);
        vmts.add(new VMT(methodDeclaration));

        List<Statement> statementList = methodDeclaration.findAll(Statement.class);
        methodNodelist.addAll(statementList);
        /*build VMT*/
        statementList.forEach(statement -> {
            VMT vmt = new VMT(statement);
            vmts.add(vmt);
        });
        /*-------------------CFG-Expression------------------*/
        List<Expression> statements = methodDeclaration.findAll(Expression.class);
        filterExp(statements);
        methodNodelist.addAll(statements);
        /*build VMT*/
        for (Expression exp : statements) {
            List<SimpleName> varlist = exp.findAll(SimpleName.class);
            if (exp instanceof MethodCallExpr) {
                /*todo methodCall*/
            } else {
                VMT vmtExpr = new VMT(exp);
                vmts.add(vmtExpr);
            }
        }

    }

    /*split Exp*/
    public void filterExp(List<Expression> expressionList) {
        Iterator<Expression> expressionIterator = expressionList.iterator();
        while (expressionIterator.hasNext()) {
            Expression exp = expressionIterator.next();
            if (exp instanceof NameExpr
                    || exp instanceof IntegerLiteralExpr
                    || exp instanceof StringLiteralExpr) {
                expressionIterator.remove();
            }
        }

    }

    public SimpleName getMethodName() {
        return methodName;
    }

    public NodeList<Parameter> getParameters() {
        return parameters;
    }

    public List<VMT> getVmts() {
        return vmts;
    }

    public void setMethodName(SimpleName methodName) {
        this.methodName = methodName;
    }

    public void setParameters(NodeList<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setVmts(List<VMT> vmts) {
        this.vmts = vmts;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public List<Node> getMethodNodelist() {
        return methodNodelist;
    }
}
