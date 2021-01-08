package graph.Unity;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by lp on 2020/4/29
 */
public class MethodDeclationInfo {
    //我们只记录方法体中的函数声明
    //直接父类（内部类函数的直接父类函数）
    //函数的具体位置（外部类->内部类->new->函数声明）
    String meOfClassName = "";
    List<Node> parentNodeList = new ArrayList<>();
    ClassOrInterfaceDeclaration classOrInterfaceDeclaration;
    MethodDeclaration methodDeclaration;

    public MethodDeclationInfo(CompilationUnit cu, MethodDeclaration me, ClassOrInterfaceDeclaration parentClassOrInterfaceDeclaration) {
        //构造函数
        this.methodDeclaration = me;
        //函数声明中有第三方调用，解析不出来
        String packageName = cu.getPackageDeclaration().get().getNameAsString();
        ResolvedMethodDeclaration resolve = me.resolve();
        //TODO 匿名类对象需要进行字符处理
        String className = "";
        className = resolve.getQualifiedName()
                .replace(packageName + ".", "")
                .replace(me.getNameAsString(), "");


        this.meOfClassName = className.substring(0, className.length() - 1);

        if (me.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
            this.classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) me.getParentNode().get();
            //this.parentNodeList.add(me.getParentNode().get());

        } else if (me.getParentNode().get() instanceof ObjectCreationExpr) {
            //todo 这块创建new中的函数声明 情况需要解决
            this.classOrInterfaceDeclaration = parentClassOrInterfaceDeclaration;
            //this.parentNodeList.add(me.getParentNode().get());
            //this.parentNodeList.add(parentClassOrInterfaceDeclaration);

        } else {
            //TODO 其他情况
            this.classOrInterfaceDeclaration = parentClassOrInterfaceDeclaration;

        }

    }

    public String getMethodParameter() {
        //拿到函数声明的参数列表
        List<String> res = new ArrayList<>();

        for (Parameter parameter : methodDeclaration.getParameters()) {
            Type type = parameter.getType();
            String string = new String();

            if (type.isArrayType()) {
                string = parameter.getType().asArrayType().asString();

            } else if (type.isClassOrInterfaceType()) {
                string = parameter.getType().asClassOrInterfaceType().asString();

            } else if (type.isIntersectionType()) {
                string = parameter.getType().asIntersectionType().asString();

            } else if (type.isPrimitiveType()) {
                string = parameter.getType().asPrimitiveType().asString();

            } else if (type.isReferenceType()) {
                System.out.println("ReferenceType");
                // pass

            } else if (type.isTypeParameter()) {
                string = parameter.getType().asTypeParameter().asString();

            } else if (type.isUnionType()) {
                string = parameter.getType().asUnionType().asString();

            } else if (type.isUnknownType()) {
                string = parameter.getType().asUnknownType().asString();

            } else if (type.isVarType()) {
                string = parameter.getType().asVarType().asString();

            } else if (type.isVoidType()) {
                string = parameter.getType().asVoidType().asString();

            } else if (type.isWildcardType()) {
                string = parameter.getType().asWildcardType().asString();

            } else {
                System.out.println("Wrong!");
                System.exit(0);
            }
            res.add(string);
        }

        return StringUtils.join(res.toArray(), "-");
    }

    public String getClassNameOfMethod(Node methodDeclaration) {
        List<String> allClassName = new ArrayList<>();

        while (methodDeclaration.getParentNode().isPresent() && !(methodDeclaration.getParentNode().get() instanceof CompilationUnit)) {

            if (methodDeclaration.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
                allClassName.add(((ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get()).getName().toString());
            } else if (methodDeclaration.getParentNode().get() instanceof ObjectCreationExpr) {
                //
                // 函数定义在 new 类名(){}中的情况暂不完善
                //
//                allClassName.add(((ObjectCreationExpr)methodDeclaration.getParentNode().get()).getTypeAsString());
                System.out.println("GraphParse Info:new MethodDeclation");

            } else {
                //
                // 第二种情况再往上遍历时，会找到其他类型的节点

//                System.out.println("此情况未考虑");
//                System.exit(0);
                System.out.println("GraphParse Info:Other MethodDeclation");
            }
            methodDeclaration = methodDeclaration.getParentNode().get();
        }

        Collections.reverse(allClassName);
        return StringUtils.join(allClassName.toArray(), ".");
    }

    public ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
        return classOrInterfaceDeclaration;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public String getMeOfClassName() {
        return meOfClassName;
    }
}
