package vmt.enity;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @Author:yueyue on 2020/12/19 12:10
 * @Param:
 * @return:
 * @Description:测试vm三元组的生成
 */
public class VmtSTest {

    @Test
    public void Statement() {
        File testcase1 = new File("H:\\CIA-master\\GraphBase\\src\\main\\resources\\testcase\\case1.java");
        try {
            CompilationUnit javaParser = StaticJavaParser.parse(testcase1);
            System.out.printf("test1");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}