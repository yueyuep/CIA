package vmt.graph;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.google.common.graph.NetworkBuilder;
import graph.RangeNode;
import vmt.ClassVmt;
import vmt.enity.VMT;
import vmt.enity.VmtM;
import java.util.*;

/**
 * Create by yueyue on 2020/12/21
 */
public class Ast2Graph extends GraphBase {
    /*VMT(field、method) of class*/
    List<VMT> visitedVmt = new LinkedList<>();
    ClassVmt classVmt = null;

    public Ast2Graph(CompilationUnit cu) {
        super(cu);
        this.classVmt = new ClassVmt(cu);
        init_network();
        intit_variableMethodNet();
    }

    public void init_network() {
        /*init base attribute*/
        RangeNode.nodeCacheClear();
        mVisitedNodes.clear();
        mNetwork = NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();
        mEdgeNumber = 0;
        mDataFlowNodes.clear();
    }

    public void initCFG() {
        mVisitedNodes.clear();
        mNetwork = NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();
        mEdgeNumber = 0;
        mDataFlowNodes.clear();
    }

    /*init field、method、class*/
    public void intit_variableMethodNet() {
        VMT root = VMT.newInstance(this.cu);
        List<VMT> fieldVariable = classVmt.getFieldList();
        List<VmtM> method = classVmt.getMethodList();
        /*classNodeList:set all node of class*/
        method.forEach(vmtM -> {
            classNodeList.addAll(vmtM.getMethodNodelist());
        });

        /*add_edge(DFG、CFG):cu->field*/
        for (VMT vmtF : fieldVariable) {
            putEdge(root, vmtF, EDGE_CHILD_NODE);
            putFlowField(vmtF);
            visitedVmt.add(vmtF);
        }
        /*add_edge(DGF):cu->methodDeclation*/
        for (VmtM vmtM : method) {
            MethodDeclaration methodDeclaration = vmtM.getMethodDeclaration();
            VMT vmt = VMT.newInstance(methodDeclaration);
            putEdge(root, vmt, EDGE_CHILD_NODE);
            visitedVmt.add(vmt);
        }
    }

    public void constructNetwork() {
        /*travel node of methodDeclation for CFG*/
        for (VmtM vmtM : classVmt.getMethodList()) {
            MethodDeclaration me = vmtM.getMethodDeclaration();
            travelNodeForCFG(me);
        }
        /*travel allNode of classVmt for DFG*/
        try {
            travelNodeForDF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*todo travle all token node of AST*/
    public <T extends Node> boolean travelNode(T nodeRoot) {
        return false;
    }
    /*todo add more node connection
     *  edge:next_excute
     *  edge:has_Method.
     *  edge:variable_use
     * */

    /*
     * Exception:methodCall not exit
     * */
    public void travelNodeForDF() throws Exception {
        Set<Object> nodes = mNetwork.nodes();
        for (Object o : nodes) {
            VMT vmt = (VMT) o;
            Node statement = vmt.getStatement();
            if (visitedVmt.contains(vmt) || statement instanceof CompilationUnit) {
                continue;
            } else if (statement instanceof Statement) {//if else、for、switch
                /*todo improve*/
                continue;

            } else if (statement instanceof MethodCallExpr) {//method call
                ResolvedMethodDeclaration rm = ((MethodCallExpr) statement).resolve();
                if (rm instanceof JavaParserMethodDeclaration) {//user mathodCall
                    MethodDeclaration calledMe = ((JavaParserMethodDeclaration) rm).getWrappedNode();
                    addMethodCall(statement, calledMe);
                } else {
                    // jdk api or jar
                    continue;
                }
            } else if (statement instanceof MethodDeclaration) {
                processParam((MethodDeclaration) statement);
            } else {
                /*assinged=assigner*/
                List<Node> assignerList = vmt.getAssigner();
                if (assignerList.size() == 0) {
                    continue;
                } else {
                    assignerList.forEach(node -> {
                        if (node instanceof SimpleName
                                && mVisitedVariable.containsKey(((SimpleName) node).getIdentifier())) {
                            VMT pre = mVisitedVariable.get(((SimpleName) node).getIdentifier());
                            putEdge(pre, vmt, EDGE_LAST_USE);
                            mVisitedVariable.put(vmt.getAssigner().toString(), vmt);
                            visitedVmt.add(vmt);
                            return;
                        }
                    });
                }
            }
        }
    }

    public void processParam(MethodDeclaration methodCalled) {
        NodeList<Parameter> parameters = methodCalled.getParameters();
        if (parameters.size() == 0) return;
        List<SimpleName> param_simple_list = new ArrayList<>();
        parameters.forEach(parameter -> param_simple_list.add(parameter.getName()));
        VmtM vmtM = VmtM.newInstance(methodCalled);//get from cache
        List<VMT> vmtList = vmtM.getVmts();
        for (VMT vmt : vmtList) {
            if (param_simple_list.contains((SimpleName) vmt.getAssigned())
                    || withSameSimpleName(param_simple_list, vmt.getAssigner())) {
                /*add dataFlow edge<  param->statement  >*/
                addDataFlow(VMT.newInstance(methodCalled), vmt);
            }
        }
    }

    private boolean withSameSimpleName(List<SimpleName> param_simple_list, List<Node> assigners) {
        for (SimpleName simpleName : param_simple_list) {
            for (Node assigner : assigners) {
                if (assigner instanceof SimpleName && simpleName.getIdentifier().equals(((SimpleName) assigner).getIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void travelNodeForCFG(Node parentNode) {
        if (!parentNode.getChildNodes().isEmpty()) {
            for (Node node : parentNode.getChildNodes()) {
                /*-----------only do statement node-------------*/
                if (!classNodeList.contains(node)) {
                    continue;
                }
                switch (node.getClass().toString().substring("class com.github.javaparser.ast.stmt.".length())) {
                    case "TryStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                        TryStmt tryStmt = (TryStmt) node;
                        travelNodeForCFG(tryStmt.getTryBlock());
                        mPreTempNodes.addAll(mPreNodes);
                        tryStmt.getCatchClauses().forEach(catchClause -> {
                            addNextExecEdge(tryStmt, catchClause.getParameter());
                            resetPreNodes(catchClause.getParameter());
                            travelNodeForCFG(catchClause.getBody());
                            mPreTempNodes.addAll(mPreNodes);
                            resetPreNodes(node);
                        });
                        mPreNodes.addAll(mPreTempNodes);
                        tryStmt.getFinallyBlock().ifPresent(this::travelNodeForCFG);
                    }
                    break;
                    case "ExpressionStmt": {
                        /*travel all childnode of ExpressionStmt*/
                        processExpressionStmt(node);
                    }
                    break;
                    case "ReturnStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                    }
                    break;
                    case "IfStmt": {
                        processIfStmt(node);
                    }
                    break;
                    case "SwitchStmt": {
                        addNextExecEdgeForAllPres(node);
                        SwitchStmt switchStmt = (SwitchStmt) node;
                        Expression selector = switchStmt.getSelector();
                        addNextExecEdge(node, selector);
                        mPreNodes.clear();
                        mBreakNodes.clear();
                        for (SwitchEntry entry : switchStmt.getEntries()) {
                            mPreNodes.add(selector);
                            addNextExecEdgeForAllPres(entry);
                            entry.getLabels().stream().forEach(label -> {
                                addNextExecEdge(entry, label);
                            });
                            resetPreNodes(entry);
                            travelNodeForCFG(entry);
                        }
                        mPreNodes.addAll(mBreakNodes);
                    }
                    break;
                    case "WhileStmt": {
                        WhileStmt whileStmt = (WhileStmt) node;
                        addNextExecEdgeForAllPres(node);
                        addNextExecEdge(node, whileStmt.getCondition());
                        resetPreNodes(whileStmt.getCondition());
                        mBreakNodes.clear();
                        mContinueNodes.clear();
                        travelNodeForCFG(whileStmt.getBody());
                        mPreNodes.addAll(mContinueNodes);
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(whileStmt.getCondition());
                        mPreNodes.addAll(mBreakNodes);
                    }
                    break;
                    case "DoStmt": {
                        DoStmt doStmt = (DoStmt) node;
                        travelNodeForCFG(doStmt.getBody());
                        addNextExecEdgeForAllPres(doStmt.getCondition());
                        resetPreNodes(doStmt.getCondition());
                        travelNodeForCFG(doStmt.getBody());
                        resetPreNodes(doStmt.getCondition());
                    }
                    break;
                    case "ForEachStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                        ForEachStmt foreachStmt = (ForEachStmt) node;
                        addNextExecEdgeForAllPres(foreachStmt.getIterable());
                        resetPreNodes(foreachStmt.getIterable());
                        addNextExecEdgeForAllPres(foreachStmt.getVariable());
                        resetPreNodes(foreachStmt.getVariable());
                        travelNodeForCFG(foreachStmt.getBody());
                        addNextExecEdgeForAllPres(foreachStmt.getIterable());
                        resetPreNodes(foreachStmt.getIterable());
                    }
                    break;
                    case "ForStmt": {
                        addNextExecEdgeForAllPres(node);
                        ForStmt forStmt = (ForStmt) node;
                        resetPreNodes(forStmt);
                        /*int i=0*/
                        forStmt.getInitialization().forEach(init -> {
                            addNextExecEdgeForAllPres(init);
                            resetPreNodes(init);
                        });
                        /*i<10*/
                        forStmt.getCompare().ifPresent(compare -> {
                            addNextExecEdgeForAllPres(compare);
                            resetPreNodes(compare);
                        });
                        mBreakNodes.clear();
                        mContinueNodes.clear();
                        travelNodeForCFG(forStmt.getBody());
                        mPreNodes.addAll(mContinueNodes);
                        /*i++*/
                        forStmt.getUpdate().forEach(update -> {
                            addNextExecEdgeForAllPres(update);
                            resetPreNodes(update);
                        });
                        /*i<10*/
                        forStmt.getCompare().ifPresent(compare -> {
                            addNextExecEdgeForAllPres(compare);
                            resetPreNodes(compare);
                        });
                        mPreNodes.addAll(mBreakNodes);
                    }
                    break;
                    case "ThrowStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                    }
                    break;
                    case "AssertStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                    }
                    break;
                    case "LabeledStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                    }
                    break;
                    case "SynchronizedStmt": {
                        addNextExecEdgeForAllPres(node);
                        resetPreNodes(node);
                        SynchronizedStmt synchronizedStmt = (SynchronizedStmt) node;
                        addNextExecEdgeForAllPres(synchronizedStmt.getExpression());
                        resetPreNodes(synchronizedStmt.getExpression());
                        travelNodeForCFG(synchronizedStmt.getBody());
                    }
                    break;
                    case "BreakStmt": {
                        addNextExecEdgeForAllPres(node);
                        mPreNodes.clear();
                        mBreakNodes.push(node);
                    }
                    break;
                    case "ContinueStmt": {
                        addNextExecEdgeForAllPres(node);
                        mPreNodes.clear();
                        mContinueNodes.push(node);
                    }
                    break;
                    default: {

                        /* filter blockStmt Node*/
                        travelNodeForCFG(node);
                    }
                    break;
                }
            }
        }
    }

    /*--------------------------method Call-----------------------*/
    public void addMethodCall(Node caller, MethodDeclaration called) {
        putEdge(VMT.newInstance(caller), VMT.newInstance(called), EDGE_METHOD_CALL);
        mCalledMethodDecls.add(called);
    }

    /*--------------------------special statement-----------------------*/
    public void processIfStmt(Node childNode) {

        addNextExecEdgeForAllPres(childNode);
        IfStmt ifStmt = (IfStmt) childNode;
        addNextExecEdge(childNode, ifStmt.getCondition());
        resetPreNodes(ifStmt.getCondition());
        travelNodeForCFG(ifStmt.getThenStmt());
        mPreTempNodes.addAll(mPreNodes);
        resetPreNodes(ifStmt.getCondition());
        ifStmt.getElseStmt().ifPresent(elseStmt -> {
            addNextExecEdgeForAllPres(elseStmt);
            if (elseStmt.isIfStmt()) {
                processIfStmt(elseStmt);
            } else {
                resetPreNodes(elseStmt);
                travelNodeForCFG(elseStmt);
            }
        });
        mPreNodes.addAll(mPreTempNodes);
        mPreTempNodes.clear();
    }

    public void processExpressionStmt(Node node) {
        /*todo iprove*/
        /*if in classNodeList */
        if (classNodeList.contains(node)) {
            addNextExecEdgeForAllPres(node);
            resetPreNodes(node);
        }
        List<Node> childNodes = node.getChildNodes();
        for (Node n : childNodes) {
            processExpressionStmt(n);
        }


    }

    /*--------------------------data flow-----------------------*/
    public void addDataFlowEdge(Node node, SimpleName variableName) {
        // TODO: Process every type node, such as If/For/While, add data flow edge in it.
        if (node instanceof AssignExpr) {
            addDataFlowEdgeAssignExpr((AssignExpr) node, variableName);
        } else if (node instanceof IfStmt) {
            addDataFlowEdgeIfStmt((IfStmt) node, variableName);
        } else if (node instanceof WhileStmt) {
            addDataFlowEdgeWhileStmt((WhileStmt) node, variableName);
        } else if (node instanceof DoStmt) {
            addDataFlowEdgeDoStmt((DoStmt) node, variableName);
        } else if (node instanceof ForStmt) {
            addDataFlowEdgeForStmt((ForStmt) node, variableName);
        } else if (node instanceof ForEachStmt) {
            addDataFlowEdgeForEachStmt((ForEachStmt) node, variableName);
        } else if (node instanceof SwitchStmt) {
            addDataFlowEdgeSwitchStmt((SwitchStmt) node, variableName);
        } else if (node instanceof TryStmt) {
            addDataFlowEdgeTryStmt((TryStmt) node, variableName);
        } else {
            // TODO: improve
            //updateBlockDataFlow(node, variableName);
        }
    }

    private void addDataFlowEdgeTryStmt(TryStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeSwitchStmt(SwitchStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeForEachStmt(ForEachStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeForStmt(ForStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeDoStmt(DoStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeWhileStmt(WhileStmt node, SimpleName variableName) {

    }

    private void addDataFlowEdgeIfStmt(IfStmt node, SimpleName variableName) {
    }

    private void addDataFlowEdgeAssignExpr(AssignExpr node, SimpleName variableName) {

    }

    /*getter、setter*/


}
