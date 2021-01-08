package cg;

import com.google.common.graph.MutableNetwork;

/**
 * parse java source code to get call graph
 *
 * @author yueyue
 * @version 1.0
 */
public class Runnner {
    public static void main(String[] args) {
        //String src = "H:\\CIA-master\\Graph\\src\\main\\resources\\Example\\src";
        String src = "F:\\CodeScanning\\dataset\\source\\0.9.22";
        String dist = "graph.txt";

        try {
            ParseConfig parseConfig = new ParseConfig(src, dist);
            CallGraph callGraph = new CallGraph(parseConfig);
            callGraph.initnetwork();
            callGraph.build();
            MutableNetwork<Object, String> network = callGraph.getNetwork();

            Util.shownetwork(network);

            //Util.saveObject(callGraph, dist);
            System.out.printf("done");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
