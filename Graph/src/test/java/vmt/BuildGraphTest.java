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
import java.util.List;

/**
 * Create by yueyue on 2020/12/24
 */
public class BuildGraphTest {

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
}