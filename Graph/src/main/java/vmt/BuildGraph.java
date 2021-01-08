package vmt;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import graph.Unity.ProjectInfo;
import vmt.enity.VMT;
import vmt.enity.VmtF;
import vmt.enity.VmtM;
import vmt.graph.Ast2Graph;

import java.util.List;

/**
 * @Author:yueyue on 2020/12/19 16:42
 * @Param:
 * @return:
 * @Description:
 */
public class BuildGraph {
    public BuildGraph(List<SourceRoot> sourceRoots, ProjectInfo projectInfo) throws Exception {
        for (SourceRoot sourceRoot : sourceRoots) {
            sourceRoot.getParserConfiguration().setAttributeComments(false);
            for (ParseResult<CompilationUnit> r : sourceRoot.tryToParse()) {
                r.getResult().ifPresent(compilationUnit -> {
                    ClassVmt classVmt = new ClassVmt(compilationUnit);

                    /*TODO ConstractGraph*/
                    build(classVmt);


                });
            }
        }
    }

    public void build(ClassVmt classVmt) {
        /*get information from classvMT*/
        List<VMT> vmtField = classVmt.getFieldList();
        List<VmtM> vmtMethod = classVmt.getMethodList();
        CompilationUnit cu = classVmt.getCu();
        Ast2Graph ast2Graph = new Ast2Graph(cu);


    }

}
