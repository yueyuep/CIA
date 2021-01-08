package vmt.enity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.google.common.graph.MutableNetwork;
import graph.AST2Graph;
import graph.Base.HeadAndBodyToJson;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Create by yueyue on 2020/12/21
 */
public class VmtMTest {


    @Test
    public void initVmts() {
        VmtM vmtM = new VmtM();
        try {
            File file = new File("H:\\CIA-master\\GraphBase\\src\\main\\resources\\testcase\\case1.java");
            AST2Graph ast2Graph = new AST2Graph("H:\\CIA-master\\GraphBase\\src\\main\\resources\\testcase\\case1.java");
            /*from method start constuct graph*/
            ast2Graph.initNetwork();
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                ast2Graph.constructNetwork(methodDeclaration);
                MutableNetwork mutableNetwork = ast2Graph.getNetwork();


                /*serialize to json*/
                HeadAndBodyToJson.Body body = new HeadAndBodyToJson.Body(file, file.getName(),
                        "testVersion",
                        methodDeclaration.getName().toString(),
                        methodDeclaration,
                        null);
                FieldDeclaration fieldDeclaration = null;
                body.addFeatureMethodOfJson(null);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}