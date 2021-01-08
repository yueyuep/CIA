package cg;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.graph.MutableNetwork;
import graph.RangeNode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create by yueyue on 2021/1/8
 */
public class Util {

    /*dum graph to file*/
    public static void saveObject(Object object, String dist) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dist);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*get graph from file*/

    public static Object readObject(String dist) {
        Object res = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(dist);
            objectInputStream = new ObjectInputStream(fileInputStream);
            fileInputStream.close();
            objectInputStream.close();
            res = objectInputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;


    }

    public static void shownetwork(MutableNetwork<Object, String> network) {

        System.out.println("\n\n\n");
        System.out.println("-----------------------------Graph--------------------------------");
        String preName = "";
        String suucessName = "";
        Set<Object> nodes = network.nodes();
        for (Object o : nodes) {
            RangeNode preRangeNode = (RangeNode) o;
            Set<Object> succes = network.successors(o);
            for (Object s : succes) {
                RangeNode succesRangeNode = (RangeNode) s;
                Set<String> edgeSet = network.edgesConnecting(preRangeNode, succesRangeNode);
                List<String> edgeList = new ArrayList<>(edgeSet);
                if (edgeSet.isEmpty()) continue;
                if (preRangeNode.getmNode() instanceof ClassOrInterfaceDeclaration) {
                    preName = ((ClassOrInterfaceDeclaration) preRangeNode.getmNode()).getFullyQualifiedName().get();
                } else {
                    preName = ((MethodDeclaration) preRangeNode.getmNode()).getDeclarationAsString();
                }
                if (succesRangeNode.getmNode() instanceof ClassOrInterfaceDeclaration) {
                    suucessName = ((ClassOrInterfaceDeclaration) succesRangeNode.getmNode()).getFullyQualifiedName().get();
                } else {
                    suucessName = ((MethodDeclaration) succesRangeNode.getmNode()).getDeclarationAsString();
                }
                System.out.println(preName + "---------------------------" + "edge_count:" + edgeList.size() + "\t" + "edgeType:" + edgeList.get(0) + "---------------------------" + suucessName);
            }
        }
    }
}
