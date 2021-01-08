package graph.Base;

import graph.AST2Graph;
import graph.RangeNode;
import graph.Unity.MethodCall;
import graph.Unity.ProjectInfo;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.google.common.graph.MutableNetwork;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by lp on 2020/2/9
 */
public class HeadAndBodyToJson {
    /*head序列化的字段信息
     * fileName:
     * version:
     * hasMethodName:
     *
     */

    public static class Head {
        @Expose
        @SerializedName(value = "fileName")
        private String fileName;
        @Expose
        @SerializedName(value = "version")
        private String version;
        @Expose
        @SerializedName(value = "hasMethodName")
        private List<String> callMethodName = new ArrayList<>();

        public Head(String fileName, String version, List<String> callMethodName) {
            this.fileName = fileName;
            this.version = version;
            this.callMethodName = callMethodName;
        }
    }

    /*body序列化字段信息
     * fileName:
     * version:
     * methodName:
     * callMethodNameReferTo:
     * num:
     * succs:
     * attribute:*/
    public static class Body {
        private AST2Graph ast2Graph;
        private MethodDeclaration methodDeclaration;
        private HashMap<MethodCallExpr, MethodCall> calledMethod = new HashMap<>();

        @Expose
        @SerializedName(value = "fileName")
        private String fileName;
        @Expose
        @SerializedName(value = "version")
        private String version;
        @Expose
        @SerializedName(value = "methodName")
        private String methodName;
        @Expose
        @SerializedName(value = "callMethodNameReferTo")
        private Map<Integer, String> callMethodNameReferTo = new HashMap<>();
        @Expose
        @SerializedName(value = "num")
        private int nodeNumber;
        @Expose
        @SerializedName(value = "succs")
        private List<List<Integer>> successors = new ArrayList<>();
        @Expose
        @SerializedName(value = "attribute")

        private List<String> nodeAttribute = new ArrayList<>();

        public Body(File file, String fileName, String version, String methodName, MethodDeclaration methodDeclaration,
                    HashMap<MethodCallExpr, MethodCall> calledMethod) {
            this.ast2Graph = AST2Graph.newInstance(file.getPath());
            this.methodDeclaration = methodDeclaration;
            this.calledMethod = calledMethod;
            this.fileName = fileName;
            this.version = version;
            this.methodName = methodName;

        }

        /*填充body字段信息*/
        public void addFeatureMethodOfJson(ProjectInfo projectInfo) {
            this.ast2Graph.initNetwork();
            this.ast2Graph.constructNetwork(this.methodDeclaration);
            MutableNetwork<Object, String> mutableNetwork = this.ast2Graph.getNetwork();
            Map<Object, Integer> vistedMethodCallex = new HashMap<>();
            Map<Object, Integer> nodeMap = new HashMap<>();
            int nodeIndex = 0;
            // 添加函数调用字段
            for (Object node : mutableNetwork.nodes()) {
                nodeMap.put(node, nodeIndex);
                nodeIndex++;
                //+++++++++++++++++++++++++++++++++++++++++构建节点调用函数位置关系++++++++++++++++++++++++++++++++++++++++
                if (!vistedMethodCallex.containsKey(node) && node instanceof RangeNode) {
                    //找到函数调用节点。
                    if (((RangeNode) node).getmNode() instanceof MethodCallExpr) {
                        MethodCallExpr methodCallExpr = ((MethodCallExpr) ((RangeNode) node).getmNode()).asMethodCallExpr();
                        //如果不在我们的函数调用处理当中，则跳过
                        if (!this.calledMethod.containsKey(methodCallExpr)) continue;
                        int index = nodeMap.get(node);
                        vistedMethodCallex.put(methodCallExpr, index);
                        //需要找到函数调用MethodCall 将其转换成字符串。
                        //TODO 这里的hashmap比较，需要确定能够找到
                        //TODO 这里需要测试，相对路径的获取是否正确。
                        String res = MethodCall.mcToString(this.calledMethod.get(methodCallExpr)).replace(projectInfo.getPrifxPath(), "");
                        this.callMethodNameReferTo.put(index, res);
                    }
                }
            }
            // 添加节点总数字段
            this.nodeNumber = nodeIndex;
            // 添加节点关系（后继）字段
            for (Object node : mutableNetwork.nodes()) {
                List<Integer> tempNode = mutableNetwork.successors(node).stream()
                        .map(n -> nodeMap.get(n))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                Integer index = nodeMap.get(node);
                tempNode = tempNode.stream().filter(n -> n != index).collect(Collectors.toList());
                this.successors.add(tempNode);
                addStringAttribute(node, this.nodeAttribute);
            }

        }
        /*添加字符属性*/
        public void addStringAttribute(Object node, List<String> nodeAttribute) {
            if (node instanceof RangeNode) {
                nodeAttribute.add(new AddFeature().travelNode(((RangeNode) node).getmNode()));
            } else if (node instanceof String) {
                nodeAttribute.add(node.toString());
            } else {
                nodeAttribute.add(node.toString());
            }
        }


    }

}
