package graph;

import graph.Base.FunctionParse;
import graph.Unity.MethodCall;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.utils.SourceRoot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create by lp on 2020/2/5
 * 工具包
 */
public class Utils {
    public static List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> getfileMethodDeclarationMap(File[] files) {
        // 获得所有文件的内部类函数声明和外部类函数声明
        List<HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>>> fileMethodDeclarationMap = new ArrayList<>();
        //<外部类，外部类中所有的方法声明>
        HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>> allOutclassMethods = new HashMap<>();
        //<内部类，内部类中所有的方法声明>
        HashMap<File, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>>> allInnerclassMethods = new HashMap<>();
        for (File file : files) {
            FunctionParse functionParse = new FunctionParse(file);
            functionParse.PareMethod();
            allOutclassMethods.put(file, functionParse.getOutclassMethods());
            allInnerclassMethods.put(file, functionParse.getInnerclassMethods());
        }
        fileMethodDeclarationMap.add(allInnerclassMethods);
        fileMethodDeclarationMap.add(allOutclassMethods);
        return fileMethodDeclarationMap;
    }

    public static boolean containMethod(MethodDeclaration methodDeclaration, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> outclassMethods, HashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> innertclassMethods) {
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : outclassMethods.keySet()) {
            if (outclassMethods.get(classOrInterfaceDeclaration).contains(methodDeclaration)) {
                return true;
            }
        }
        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : innertclassMethods.keySet()) {
            if (innertclassMethods.get(classOrInterfaceDeclaration).contains(methodDeclaration)) {
                return true;
            }
        }
        return false;
    }

    public static HashMap<MethodCallExpr, MethodCall> getcallMethods(File pfile, MethodDeclaration methodDeclaration, List<SourceRoot> sourceRoots) {
        /*
         找到所有函数调用所在的文件名、类名、函数声明,版本号，
         通过javaSampleSolver来解决。
        */
        HashMap<MethodCallExpr, MethodCall> methodCallList = new HashMap<>();
        HashMap<String, HashMap<MethodDeclaration, String>> CalledMethod = new HashMap<>();
        List<MethodCallExpr> methodCallExprList = methodDeclaration.findAll(MethodCallExpr.class);
        for (MethodCallExpr methodCallExpr : methodCallExprList) {
            try {
                //todo fix stackOverflow
                ResolvedMethodDeclaration rmd = methodCallExpr.resolve();
                if (rmd instanceof JavaParserMethodDeclaration) {
                    /*process user methodCall*/
                    methodCallList.put(methodCallExpr, new MethodCall((JavaParserMethodDeclaration) rmd, sourceRoots));

                }

            } catch (Exception e) {
                /*not process System methodCall*/
                continue;
            }
        }
        return methodCallList;
    }

    /*save json to file*/
    public static void saveToJsonFile(Object object, String fileName) {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String jsonString = gson.toJson(object).replace("\\\\", "/");
        saveToFile(jsonString, fileName);
    }

    /*save  text to file*/
    public static void saveToFile(String text, String fileName) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
            out.write(text + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String getClassLastName(String classPackage) {
        String[] classPackageSplit = classPackage.split("\\.");
        return classPackageSplit[classPackageSplit.length - 1];
    }

    public static File openDirFileChooser() {
        File file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        return file;
    }


}
