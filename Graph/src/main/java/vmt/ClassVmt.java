package vmt;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import vmt.enity.VMT;
import vmt.enity.VmtM;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:yueyue on 2020/12/19 16:28
 * @Param:
 * @return:
 * @Description: VMT of .java class
 */
public class ClassVmt {

    private CompilationUnit cu = null;
    /*---------------------class head---------------------*/
    private ClassOrInterfaceDeclaration classBody = null;
    private boolean isClass = true;
    private SimpleName className = null;
    //innerClass
    private List<ClassOrInterfaceDeclaration> innnerClassOfInterface = new ArrayList<>();

    /*---------------------class body------------------*/
    //FieldSet
    private List<VMT> fieldList = new ArrayList<>();
    private List<VmtM> MethodList = new ArrayList<>();


    public ClassVmt(CompilationUnit cu) {
        /*todo init all field*/
        this.cu = cu;
        Init();


    }

    /*init*/
    public void Init() {
        /*todo 初始化VMT、VMTF、VMS等相关信息，记录所有的图相关的节点 */
        List<ClassOrInterfaceDeclaration> ci = cu.findAll(ClassOrInterfaceDeclaration.class);
        classBody = ci.stream().
                filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getParentNode().isPresent() && classOrInterfaceDeclaration.getParentNode().get() instanceof CompilationUnit)
                .collect(Collectors.toList()).get(0);
        className = classBody.getName();
        isClass = classBody.isInterface() ? false : true;

        /*init field*/
        init_VmtF();
        /*init method*/
        init_vmtM();
    }

    public void init_VmtF() {
        List<FieldDeclaration> fieldDeclarations = classBody.getFields();
        for (FieldDeclaration f : fieldDeclarations) {
            VMT vmtField = new VMT(f);
            fieldList.add(vmtField);
        }

    }

    public void init_vmtM() {
        List<MethodDeclaration> methodDeclarations = classBody.findAll(MethodDeclaration.class);
        for (MethodDeclaration me : methodDeclarations) {
            VmtM vmtM = VmtM.newInstance(me);
            MethodList.add(vmtM);
        }
    }



    public boolean isClass() {
        return isClass;
    }


    public List<ClassOrInterfaceDeclaration> getInnnerClassOfInterface() {
        return innnerClassOfInterface;
    }

    public List<VMT> getFieldList() {
        return fieldList;
    }

    public List<VmtM> getMethodList() {
        return MethodList;
    }

    public void setClassBody(ClassOrInterfaceDeclaration classBody) {
        this.classBody = classBody;
    }

    public void setClass(boolean aClass) {
        isClass = aClass;
    }

    public void setClassName(SimpleName className) {
        this.className = className;
    }

    public void setInnnerClassOfInterface(List<ClassOrInterfaceDeclaration> innnerClassOfInterface) {
        this.innnerClassOfInterface = innnerClassOfInterface;
    }

    public void setFieldList(List<VMT> fieldList) {
        this.fieldList = fieldList;
    }

    public void setMethodList(List<VmtM> methodList) {
        MethodList = methodList;
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }


}
