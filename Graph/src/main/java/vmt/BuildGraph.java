package vmt;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import graph.Unity.ProjectInfo;
import vmt.enity.VMT;
import vmt.enity.VmtF;
import vmt.enity.VmtM;
import vmt.graph.Ast2Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:yueyue on 2020/12/19 16:42
 * @Param:
 * @return:
 * @Description:
 */
public class BuildGraph {

    private List<VMT> vmtField = new ArrayList<>();
    private List<VmtM> vmtMethod = new ArrayList<>();
    private CompilationUnit cu = null;
    private Ast2Graph ast2Graph = null;

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
        this.vmtField = classVmt.getFieldList();
        this.vmtMethod = classVmt.getMethodList();
        this.cu = classVmt.getCu();
        this.ast2Graph = new Ast2Graph(cu);

    }

    public List<VMT> getVmtField() {
        return vmtField;
    }

    public List<VmtM> getVmtMethod() {
        return vmtMethod;
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public Ast2Graph getAst2Graph() {
        return ast2Graph;
    }
}
