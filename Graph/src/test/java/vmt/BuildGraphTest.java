package vmt;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Test;
import vmt.graph.Ast2Graph;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Queue;

/**
 * Create by yueyue on 2020/12/24
 */
public class BuildGraphTest {

    static void function(Queue queue) {

        for (int i = 0; i < 10; i++) {

            //reove the head of the queue
            queue.remove();
            //insert queue
            queue.add(i);

        }
    }

    @Test
    public void f() {
        String s = "";
        if (s == null) {//用于控制流程的条件表达式"s==null"恒为假

        }
    }

    @Test
    public void build() {
        File file = new File("H:\\CIA-master\\Graph\\src\\main\\resources\\Example\\src\\main\\java\\case1.java");
        String path = "H:\\CIA-master\\Graph\\src\\main\\resources\\Example\\src\\main\\java";
        JavaParserTypeSolver javaParserTypeSolver = new JavaParserTypeSolver(path);
        ReflectionTypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(javaParserTypeSolver);
        combinedTypeSolver.add(reflectionTypeSolver);
        JavaSymbolSolver javaSymbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            ClassVmt classVmt = new ClassVmt(cu);
            Ast2Graph ast2Graph = new Ast2Graph(classVmt.getCu());
            ast2Graph.constructNetwork();
            System.out.printf("test");


        } catch (Exception e) {
            /*methodCall not exist*/
            e.printStackTrace();
        }


    }

    @Test
    public int casecode() {

        for (int i = 0; i < 10; i++) {
            System.out.println("i:" + i);
        }
        double r = 2.0;
        double area = Math.pow(r, 2) * Math.PI;
        int a = 0;
        if (a > 0) {
            return a;
        } else {
            return Math.abs(a);
        }


    }



}