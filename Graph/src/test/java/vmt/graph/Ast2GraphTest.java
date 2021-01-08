package vmt.graph;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.MutableValueGraph;
import graph.RangeNode;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

/**
 * Create by yueyue on 2020/12/21
 */
public class Ast2GraphTest implements GraphEdge {

    MutableValueGraph<Object, String> mGraph = null;//多值网络
    public MutableNetwork<Object, String> mNetwork = null;

    public void printEndpointPair(EndpointPair endpointPair) {
        System.out.printf(endpointPair.nodeU().getClass() + ": " + ((RangeNode) endpointPair.nodeU()).getmNode().toString()
                + ": " + ((RangeNode) endpointPair.nodeU()).getmNode().getBegin().get().line);
        System.out.printf(endpointPair.nodeV().getClass() + ": " + ((RangeNode) endpointPair.nodeV()).getmNode().toString()
                + ": " + ((RangeNode) endpointPair.nodeV()).getmNode().getBegin().get().line);
    }

    public int getEdgeType(String edge) {
        if (edge.contains(EDGE_LAST_USE)) {
            return 0;
        } else if (edge.contains(EDGE_LAST_WRITE)) {
            return 1;
        } else if (edge.contains(EDGE_COMPUTED_FROM)) {
            return 2;
        } else if (edge.contains(EDGE_GUARDED_BY_NEGATION)) {
            return 4;
        } else if (edge.contains(EDGE_GUARDED_BY)) {
            return 3;
        } else if (edge.contains(EDGE_RETURNS_TO)) {
            return 5;
        }
        return -1;
    }

    @Test
    public void showGraph() {

        for (EndpointPair<Object> endpointPair : mGraph.edges()) {
            Optional<String> edgeValue = mGraph.edgeValue(endpointPair.nodeU(), endpointPair.nodeV());
            if (edgeValue.isPresent()) {
                String value = edgeValue.get();
                System.out.printf("A Edge");
                System.out.printf(value);
                if ("child_node".equals(value)) {
                    System.out.printf(endpointPair.nodeU().getClass().toString());
                    System.out.printf(endpointPair.nodeV().getClass().toString());
                    System.out.printf(endpointPair.nodeV().toString());
                } else if ("child_token".equals(value)) {
                    System.out.printf(endpointPair.nodeU().getClass().toString());
                    System.out.printf(endpointPair.nodeV().getClass().toString());
                    System.out.printf(endpointPair.nodeV().toString());
                } else if ("next_token".equals(value)) {
                    System.out.printf(endpointPair.nodeU().toString());
                    System.out.printf(endpointPair.nodeV().getClass().toString());
                    System.out.printf(endpointPair.nodeV().toString());
                }
            }
        }
    }

    @Test
    public void showDataFlowGraph() {
        for (EndpointPair<Object> endpointPair : mGraph.edges()) {
            Optional<String> edgeValue = mGraph.edgeValue(endpointPair.nodeU(), endpointPair.nodeV());
            if (edgeValue.isPresent()) {
                String value = edgeValue.get();
//                logInfo(value);
                if ("last_use".equals(value)) {
                    System.out.printf("A LAST USE EDGE");
                    printEndpointPair(endpointPair);
                } else if ("last_write".equals(value)) {
                    System.out.printf("A LAST WRITE EDGE");
                    printEndpointPair(endpointPair);
                } else if ("computed_from".equals(value)) {
                    System.out.printf("A COMPUTED FROM EDGE");
                    printEndpointPair(endpointPair);
                } else if ("guarded_by".equals(value)) {
                    System.out.printf("A GUARDED BY EDGE");
                    printEndpointPair(endpointPair);
                } else if ("guarded_by_negation".equals(value)) {
                    System.out.printf("A GUARDED BY NEGATION EDGE");
                    printEndpointPair(endpointPair);
                } else if ("reutrns_to".equals(value)) {
                    System.out.printf("A RETURNS TO EDGE");
                    printEndpointPair(endpointPair);
                }
            }
        }
    }

    @Test
    public void showDataFlowNetwork() {
        for (String edge : mNetwork.edges()) {
            EndpointPair<Object> endpointPair = mNetwork.incidentNodes(edge);
            switch (getEdgeType(edge)) {
                case 0:
                    System.out.printf("A LAST USE EDGE");
                    printEndpointPair(endpointPair);
                    break;
                case 1:
                    System.out.printf("A LAST WRITE EDGE");
                    printEndpointPair(endpointPair);
                    break;
                case 2:
                    System.out.printf("A COMPUTED FROM EDGE");
                    printEndpointPair(endpointPair);
                    break;
                case 3:
                    System.out.printf("A GUARDED BY EDGE");
                    printEndpointPair(endpointPair);
                    break;
                case 4:
                    System.out.printf("A GUARDED BY NEGATION EDGE");
                    printEndpointPair(endpointPair);
                    break;
                case 5:
                    System.out.printf("A RETURNS TO EDGE");
                    printEndpointPair(endpointPair);
                    break;
                default:
                    break;

            }
        }
    }

    @Test
    public void intit_variableMethodNet() {
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(new File("H:\\CIA-master\\Graph\\src\\main\\resources\\testcase\\case3.java"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Ast2Graph ast2Graph = new Ast2Graph(cu);

        System.out.printf("init Done!");
    }
}