package vmt.graph;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.google.common.graph.MutableNetwork;
import graph.RangeNode;
import vmt.enity.VMT;

import java.util.*;

/**
 * base process GraphBase
 */
public class GraphBase implements GraphEdge {
    /*base attribute*/
    public CompilationUnit cu;
    public List<Range> mVisitedNodes = new ArrayList<>();
    public HashMap<String, VMT> mVisitedVariable = new HashMap<>();
    public List<Node> classNodeList = new ArrayList<>();
    public MutableNetwork<Object, String> mNetwork;
    public Node mLastUse;
    public Node mLastWrite;
    public int mEdgeNumber;
    public Set<RangeNode> mDataFlowNodes = new HashSet<>();
    // For CFG
    public ArrayList<Node> mPreNodes = new ArrayList<>();
    public ArrayDeque<Node> mBreakNodes = new ArrayDeque<>();
    public ArrayDeque<Node> mContinueNodes = new ArrayDeque<>();
    public ArrayList<Node> mPreTempNodes = new ArrayList<>();
    // For CG
    public ArrayList<MethodDeclaration> mCalledMethodDecls = new ArrayList<>();

    public GraphBase() {
    }

    public GraphBase(CompilationUnit cu) {
        this.cu = cu;
    }

    /*--------------------------edge、args(CFG)-----------------------*/
    public void addNextExecEdgeForAllPres(Node childNode) {


        for (Node node : mPreNodes) {
            //VMT Node(singleton object)
            VMT pre = VMT.newInstance(node);
            VMT succ = VMT.newInstance(childNode);
            addNextExecEdge(pre, succ);
        }
    }

    public void addNextExecEdge(Node pre, Node succ) {
        putEdge(VMT.newInstance(pre), VMT.newInstance(succ), EDGE_NEXT_EXEC);
    }

    public void addNextExecEdge(VMT pre, VMT succ) {

        putEdge(pre, succ, EDGE_NEXT_EXEC);

    }

    public void addDataFlow(VMT pre, VMT succ) {
        putEdge(pre, succ, EDGE_LAST_USE);
    }


    public void resetPreNodes(Node newPre) {
        mPreNodes.clear();
        mPreNodes.add(newPre);
    }

    /*--------------------------method Call-----------------------*/
    public void addMethodCall(Node called, Node caller) {
        putEdge(RangeNode.newInstance(caller), RangeNode.newInstance(called), EDGE_METHOD_CALL);
        mCalledMethodDecls.add((MethodDeclaration) called);
    }


    public void putEdge(Object nodeU, Object nodeV, String edgeType) {
        mNetwork.addEdge(nodeU, nodeV, edgeType + "_" + mEdgeNumber);
        mEdgeNumber = mEdgeNumber + 1;
    }

    public void putEdge(VMT pre, VMT succ, String edgeType) {
        mNetwork.addEdge(pre, succ, edgeType + "_" + mEdgeNumber);
        mEdgeNumber = mEdgeNumber + 1;

    }

    public void putFlowField(VMT vmt) {
        Node assigned = vmt.getAssigned();
        List<Node> assigner = vmt.getAssigner();
        /*variable use*/
        if (assigner.size() == 0) {
            mVisitedVariable.put(assigned.toString(), vmt);
            return;
        } else {
            for (Node a : assigner) {
                if (mVisitedVariable.containsKey(a.toString())) {
                    VMT pre = mVisitedVariable.get(a.toString());
                    putEdge(pre, vmt, EDGE_LAST_USE);
                    /*add single data-use edge*/
                    mVisitedVariable.put(assigned.toString(), vmt);
                    return;
                }

            }
        }
    }

    /*-------------------------dataFlow------------------------*/
    public void addLastUse(Object nodeU, Object nodeV) {
        putEdge(RangeNode.newInstance((Node) nodeU), RangeNode.newInstance((Node) nodeV), EDGE_LAST_USE);
        System.out.printf("A LAST USE EDGE");
        System.out.printf(nodeU.getClass().toString() + ": " + ((Node) nodeU).toString() + ": " + ((Node) nodeU).getBegin().get().line);
        System.out.printf(nodeV.getClass().toString() + ": " + ((Node) nodeV).toString() + ": " + ((Node) nodeV).getBegin().get().line);
    }

    public void addLastWrite(Object nodeU, Object nodeV) {
        putEdge(RangeNode.newInstance((Node) nodeU), RangeNode.newInstance((Node) nodeV), EDGE_LAST_WRITE);
        System.out.printf("A LAST WRITE EDGE");
        System.out.printf(nodeU.getClass().toString() + ": " + ((Node) nodeU).toString() + ": " + ((Node) nodeU).getBegin().get().line);
        System.out.printf(nodeV.getClass().toString() + ": " + ((Node) nodeV).toString() + ": " + ((Node) nodeV).getBegin().get().line);
    }

    public void addComputedFrom(Object nodeU, Object nodeV) {
        putEdge(RangeNode.newInstance((Node) nodeU), RangeNode.newInstance((Node) nodeV), EDGE_COMPUTED_FROM);
        System.out.printf("A COMPUTED FROM EDGE");
        System.out.printf(nodeU.getClass().toString() + ": " + ((Node) nodeU).toString() + ": " + ((Node) nodeU).getBegin().get().line);
        System.out.printf(nodeV.getClass().toString() + ": " + ((Node) nodeV).toString() + ": " + ((Node) nodeV).getBegin().get().line);
    }

    public void addLastLexicalUse(Object nodeU, Object nodeV) {
//        putEdge(nodeU, nodeV, EDGE_LAST_LEXICAL_USE);
        putEdge(RangeNode.newInstance((Node) nodeU), RangeNode.newInstance((Node) nodeV), EDGE_LAST_LEXICAL_USE);
        putEdge(RangeNode.newInstance((Node) nodeU), RangeNode.newInstance((Node) nodeV), EDGE_COMPUTED_FROM);
        System.out.printf("A LAST LEXICAL USE EDGE");
        System.out.printf(nodeU.getClass().toString() + ": " + ((Node) nodeU).toString() + ": " + ((Node) nodeU).getBegin().get().line);
        System.out.printf(nodeV.getClass().toString() + ": " + ((Node) nodeV).toString() + ": " + ((Node) nodeV).getBegin().get().line);
    }

    public void addEdgeReturnsTo(ReturnStmt returnStmt, MethodDeclaration methodDeclaration) {
        putEdge(RangeNode.newInstance(returnStmt), RangeNode.newInstance(methodDeclaration), EDGE_RETURNS_TO);
    }

    public void addGuardedBy(NameExpr nameExpr, Node nodeV) {
        putEdge(RangeNode.newInstance(nameExpr), RangeNode.newInstance(nodeV), EDGE_GUARDED_BY);
    }

    public void addGuardedByNegation(NameExpr nameExpr, Node nodeV) {
        putEdge(RangeNode.newInstance(nameExpr), RangeNode.newInstance(nodeV), EDGE_GUARDED_BY_NEGATION);
    }

    public void updateLastUseWriteOfVariables(List<NameExpr> variableFlows) {
        for (NameExpr variableNameExpr : variableFlows) {
            updateLastUseWrite(variableNameExpr);
        }
    }

    public void updateLastUseWrite(NameExpr variableNameExpr) {
        addLastUse(variableNameExpr, mLastUse);
        mLastUse = variableNameExpr;
        addLastWrite(variableNameExpr, mLastWrite);
    }


    public <T extends Node> void addLastWriteList(List<T> nodeUs, Object o) {
        nodeUs.forEach(nodeU -> addLastWrite(nodeU, o));
    }


    public <T extends Node> void addComputedFromList(Object o, List<T> nodes) {
        nodes.forEach(n -> addComputedFrom(o, n));
    }


    public Set<RangeNode> getRelatedDataFlowNodes(RangeNode node, Set<RangeNode> done) {
        Set<RangeNode> results = new HashSet<>();
        results.add(node);
        for (RangeNode adjNode : getmDataFlowNodes()) {
            if (results.contains(adjNode) || done.contains(adjNode)) {
                continue;
            }
            if ((mNetwork.hasEdgeConnecting(node, adjNode) &&
                    hasConnectingBelongs(node, adjNode, new String[]{EDGE_COMPUTED_FROM, EDGE_LAST_USE,
                            EDGE_LAST_WRITE, EDGE_LAST_LEXICAL_USE, EDGE_FORMAL_ARG_NAME})) ||
                    (mNetwork.hasEdgeConnecting(node, adjNode)
                            && hasConnectingBelongs(adjNode, node, new String[]{EDGE_LAST_USE, //  TODO: EDGE_FORMAL_ARG_NAME 可以看到var所在的方法体被谁调用
                            EDGE_LAST_WRITE, EDGE_LAST_LEXICAL_USE}))) {
                results.add(adjNode);
                results.addAll(getRelatedDataFlowNodes(adjNode, results));
            }
        }
        return results;
    }

    public Set<RangeNode> getRelatedDataFlowNodes(RangeNode node, Set<RangeNode> done, Set<RangeNode> excludes) {
        // TODO: Can't Find all Related Nodes
        Set<RangeNode> results = new HashSet<>(done);
        // TODO: maybe getDataFlowNodes is not contains all
        for (RangeNode adjNode : getmDataFlowNodes()) {
            if (results.contains(adjNode) || excludes.contains(adjNode)) {
                continue;
            }
            if ((mNetwork.hasEdgeConnecting(node, adjNode) &&
                    hasConnectingBelongs(node, adjNode, new String[]{EDGE_COMPUTED_FROM, EDGE_LAST_USE,
                            EDGE_LAST_WRITE, EDGE_LAST_LEXICAL_USE, EDGE_FORMAL_ARG_NAME})) ||
                    (mNetwork.hasEdgeConnecting(node, adjNode)
                            && hasConnectingBelongs(adjNode, node, new String[]{EDGE_LAST_USE, //  TODO: EDGE_FORMAL_ARG_NAME 可以看到var所在的方法体被谁调用
                            EDGE_LAST_WRITE, EDGE_LAST_LEXICAL_USE}))) {
                results.add(adjNode);
                results.addAll(getRelatedDataFlowNodes(adjNode, results, excludes));
                // TODO: maybe add excludes should in here:
                // excludes.add(adjNode);
            } else {
                excludes.add(adjNode); // TODO: Maybe Something is wrong
            }
        }
        return results;
    }

    public boolean hasConnectingBelongs(Object nodeU, Object nodeV, String[] edgeTypes) {
        for (String edgeType : mNetwork.edgesConnecting(nodeU, nodeV)) {
            if (isContain(edgeType, edgeTypes)) {
                return true;
            }
        }
        return false;
    }

    /*-------------------------util------------------------*/
    public static boolean isContain(String master, String sub) {
        return master.contains(sub);
    }

    public static boolean isContain(String master, String[] sub) {
        for (String s : sub) {
            if (isContain(master, s)) {
                return true;
            }
        }
        return false;
    }

    /*-------------------------getter------------------------*/

    public CompilationUnit getCu() {
        return cu;
    }

    public List<Range> getmVisitedNodes() {
        return mVisitedNodes;
    }

    public HashMap<String, VMT> getmVisitedVariable() {
        return mVisitedVariable;
    }

    public List<Node> getClassNodeList() {
        return classNodeList;
    }

    public MutableNetwork<Object, String> getmNetwork() {
        return mNetwork;
    }

    public Set<RangeNode> getmDataFlowNodes() {
        return mDataFlowNodes;
    }

    public ArrayList<Node> getmPreNodes() {
        return mPreNodes;
    }

    public ArrayDeque<Node> getmContinueNodes() {
        return mContinueNodes;
    }

    public ArrayList<MethodDeclaration> getmCalledMethodDecls() {
        return mCalledMethodDecls;
    }
}
