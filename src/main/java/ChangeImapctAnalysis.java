//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.stmt.ExpressionStmt;
//import com.github.javaparser.ast.stmt.Statement;
//import graph.AST2Graph;
/**
 * Create by yueyue on 2021/1/9
 * ChangeImpactAnalysis:include two module:ChangeAnalysis、Graph
 * <p>
 * module1:
 * ChangeAnalysis is aim to detect the change variables between adjacent project version.
 * firstly,construct the call graph,then use spoon-gumtree to compare method、field to get all change variables
 * <p>
 * module2:
 * Graph is aim to analysis the impact change set through the change variable. This process need to refer to the call graph
 */
public class ChangeImapctAnalysis {

    public static void main(String[] args) {

        //String src = "H:\\CIA-master\\Graph\\src\\main\\resources\\Example\\src";
        String src = "F:\\CodeScanning\\dataset\\source\\0.9.22";
        String dist = "F:\\CodeScanning\\dataset\\source\\0.9.23";

//        AST2Graph ast2Graph = AST2Graph.newInstance("H:\\CIA-master\\src\\main\\resources\\test.java");
//        CompilationUnit compilationUnit = ast2Graph.getCompilationUnit();

        System.out.println("done");

    }


}
