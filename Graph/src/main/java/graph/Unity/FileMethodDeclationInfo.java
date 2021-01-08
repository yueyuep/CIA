package graph.Unity;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:lp on 2020/4/29 0:29
 * @Param:
 * @return:
 * @Description:记录单个文件的具体信息(文件路径，函数声明)
 */
public class FileMethodDeclationInfo {
    //我们的Java文件是什么类型的
    CompilationUnit cu;
    TypeDeclaration typeDeclaration;
    //父类对象暂定
    ClassOrInterfaceDeclaration parentClassOrInterfaceDeclaration;
    File file = null;
    List<MethodDeclationInfo> methodDeclationInfoList = new ArrayList<>();

    public FileMethodDeclationInfo(CompilationUnit cu, List<SourceRoot> sourceRoots) {
        this.cu = cu;
        //TODO 需要测试父类对象parentClassOrInterfaceDeclaration‘获取是否正确
        this.typeDeclaration = init_typeDeclation(cu);
        this.parentClassOrInterfaceDeclaration = init_parentClassOrInterfaceDeclaration();
        this.methodDeclationInfoList = init_methodDeclationInfo(this.typeDeclaration);
        this.file = init_file(sourceRoots);
        //NodeList<TypeDeclaration<?>> nodeList1 = cu.getTypes();
    }

    public static FileMethodDeclationInfo newInstance(CompilationUnit cu, List<SourceRoot> sourceRoots) {
        FileMethodDeclationInfo fileMethodDeclationInfo = new FileMethodDeclationInfo(cu, sourceRoots);
        if (fileMethodDeclationInfo.getTypeDeclaration() == null)
            return null;
        else return fileMethodDeclationInfo;

    }

    //初始化文件下的函数声明信息
    private List<MethodDeclationInfo> init_methodDeclationInfo(TypeDeclaration typeDeclaration) {
        List<MethodDeclationInfo> methodDeclationInfos = new ArrayList<>();
        if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
            for (MethodDeclaration methodDeclaration : typeDeclaration.findAll(MethodDeclaration.class)) {
                if (!(methodDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) {
                    //TODO 跳过new情况下的函数声明。将这部分内容作为父函数声明的一部分
                    continue;
                }
                methodDeclationInfos.add(new MethodDeclationInfo(cu, methodDeclaration, parentClassOrInterfaceDeclaration));
            }
        } else if (typeDeclaration instanceof AnnotationDeclaration) {
            //注解类型
            System.out.println("init_methodDeclationInfo->AnnotationDeclaration");

        } else if (typeDeclaration instanceof EnumDeclaration) {
            //枚举类型
            System.out.println("init_methodDeclationInfo->EnumDeclaration");
        } else {
            System.out.println("init_methodDeclationInfo->others");
        }
        //TODO 可能classOrInterfaceDeclaration为空
        return methodDeclationInfos;
    }

    private File init_file(List<SourceRoot> sourceRoots) {
        String joinPath = "";
        try {
            //TODO 通过typeDeclaration来找到函数所在的具体文件路径,内外部类情况的测试
            ResolvedTypeDeclaration resolvedTypeDeclaration = this.typeDeclaration.resolve();
            String qualifiedName = resolvedTypeDeclaration.getQualifiedName();
            joinPath = qualifiedName.replace(".", "//") + ".java";
        } catch (Exception e) {
            System.out.println("init_file->找不到文件的路径");
        }
        return MethodCall.getFullPath(sourceRoots, joinPath);
    }

    private TypeDeclaration init_typeDeclation(CompilationUnit cu) {
        //TODO get(0)
        if (!cu.getTypes().isEmpty()) {
            return cu.getTypes().get(0);
        }
        return null;


    }

    private ClassOrInterfaceDeclaration init_parentClassOrInterfaceDeclaration() {
        //如果是类或者接口声明文件
        if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
            return (ClassOrInterfaceDeclaration) typeDeclaration;
        } else {
            //TODO 注解类型的函数声明
            return null;
        }

    }


    public List<MethodDeclaration> getMethodDeclarationList() {
        List<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        this.methodDeclationInfoList.stream().forEach(meInfo -> methodDeclarationList.add(meInfo.getMethodDeclaration()));
        return methodDeclarationList;
    }

    public ClassOrInterfaceDeclaration getParentClassOrInterfaceDeclaration() {
        return parentClassOrInterfaceDeclaration;
    }

    public File getFile() {
        return file;
    }

    public List<MethodDeclationInfo> getMethodDeclationInfoList() {
        return methodDeclationInfoList;
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }
}
