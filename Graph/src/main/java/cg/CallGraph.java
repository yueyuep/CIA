package cg;

import cg.graph.Edge;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import graph.RangeNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by yueyue on 2021/1/8
 */
public class CallGraph implements Serializable {
    private int api = 0;
    private int member_edge_count = 0;
    private int call_edge_count = 0;
    transient private ParseConfig config = null;
    private MutableNetwork<Object, String> network = null;

    public CallGraph(ParseConfig parseConfig) {
        this.config = parseConfig;


    }

    public void initnetwork() {
        network = NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();
    }

    public void build() throws FileNotFoundException {
        List<File> fileList = config.getFileList();
        JavaSymbolSolver javaSymbolSolver = config.getJavaSymbolSolver();
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);
        for (File file : fileList) {
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<ClassOrInterfaceDeclaration> classOrInterfaceDeclaration = cu.findAll(ClassOrInterfaceDeclaration.class);
            List<ClassOrInterfaceDeclaration> target_class = classOrInterfaceDeclaration.stream()
                    .filter(c -> c.getParentNode().get() instanceof CompilationUnit)
                    .collect(Collectors.toList());

            if (target_class.isEmpty()) continue;
            trvaleforCG(target_class.get(0));

        }


    }

    /*add edge*/
    public void trvaleforCG(ClassOrInterfaceDeclaration target_class) {
        List<MethodDeclaration> methodDeclarations = target_class.findAll(MethodDeclaration.class);
        for (MethodDeclaration me : methodDeclarations) {
            putEdge(target_class, me, Edge.MEMBER);
            List<MethodCallExpr> methodCallExprs = me.findAll(MethodCallExpr.class);
            processMethodCall(me, methodCallExprs);

        }
    }

    public void processMethodCall(MethodDeclaration caller, List<MethodCallExpr> methodCallExprs) {
        for (MethodCallExpr mc : methodCallExprs) {

            try {
                ResolvedMethodDeclaration re = mc.resolve();
                if (re instanceof JavaParserMethodDeclaration) {
                    MethodDeclaration called = ((JavaParserMethodDeclaration) re).getWrappedNode();
                    putEdge(caller, called, Edge.CALL);

                } else {
                    continue;
                }

            } catch (Exception e) {

                System.out.println(api++   + "---------------------------api function:" + mc.getNameAsString() + "----------------------------");
                continue;
            }

        }
    }

    public void putEdge(Node pre, Node succ, String type) {

        if (type.equals(Edge.CALL)) {
            network.addEdge(RangeNode.newInstance(pre), RangeNode.newInstance(succ), type + "_" + call_edge_count++);

        } else {
            network.addEdge(RangeNode.newInstance(pre), RangeNode.newInstance(succ), type + "_" + member_edge_count++);

        }

    }

    public void putedgeforAll(Node pre, List<Node> succeList, String type) {
        for (Node succ : succeList) {
            putEdge(pre, succ, type);
        }
    }

    public int getMember_edge_count() {
        return member_edge_count;
    }

    public int getCall_edge_count() {
        return call_edge_count;
    }

    public ParseConfig getConfig() {
        return config;
    }

    public MutableNetwork<Object, String> getNetwork() {
        return network;
    }

    public void setMember_edge_count(int member_edge_count) {
        this.member_edge_count = member_edge_count;
    }

    public void setCall_edge_count(int call_edge_count) {
        this.call_edge_count = call_edge_count;
    }

    public void setConfig(ParseConfig config) {
        this.config = config;
    }

    public void setNetwork(MutableNetwork<Object, String> network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "CallGraph{" +
                "member_edge_count=" + member_edge_count +
                ", call_edge_count=" + call_edge_count +
                ", config=" + config +
                ", network=" + network +
                '}';
    }
}
