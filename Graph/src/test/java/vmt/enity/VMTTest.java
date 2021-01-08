package vmt.enity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.junit.Test;
import sun.misc.VM;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Create by yueyue on 2020/12/23
 */
public class VMTTest {

    @Test
    public void testVMTConstruct() {
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser
                    .parse(new File("H:\\CIA-master\\Graph\\src\\main\\resources\\testcase\\case2.java"));

            List<FieldDeclaration> fieldDeclarations = cu.findAll(FieldDeclaration.class);
            for (FieldDeclaration f : fieldDeclarations) {
                VMT vmt = new VMT(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}