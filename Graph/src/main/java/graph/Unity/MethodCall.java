package graph.Unity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.utils.SourceRoot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lp on 2020/4/23
 */

public class MethodCall {
    private String path;
    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;
    private MethodDeclaration methodDeclaration;
    private List<String> args = new ArrayList<>();

    public MethodCall(JavaParserMethodDeclaration javaParserMethodDeclaration, List<SourceRoot> sourceRoots) {

        /*
        函数调用的全路径问题,需要.java.加上源码路径，然后判断这个位置的文件是否存在
        如果存在，找到了函数调用的具体位置
         */
        this.methodDeclaration = javaParserMethodDeclaration.getWrappedNode();
        this.classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) methodDeclaration.getParentNode().get();
        for (int i = 0; i < javaParserMethodDeclaration.getNumberOfParams(); i++) {
            JavaParserParameterDeclaration javaParserParameterDeclaration = (JavaParserParameterDeclaration) javaParserMethodDeclaration.getParam(i);
            this.args.add(javaParserParameterDeclaration.getWrappedNode().getTypeAsString());


        }
        String joinPath = javaParserMethodDeclaration
                .getQualifiedName()
                .replace(".", "//")
                .replace("//" + methodDeclaration.getNameAsString(), "")
                + ".java";

        //setpath，函数调用的全路径
        this.path = getFullPath(sourceRoots, joinPath).getPath();

    }

    public String getPath() {
        return path;
    }

    public ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
        return classOrInterfaceDeclaration;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public List<String> getArgs() {
        return args;
    }

    public static String mcToString(MethodCall methodCall) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(methodCall.getPath() + "?");
        stringBuffer.append(methodCall.getMethodDeclaration().getNameAsString() + "-");
        stringBuffer.append(methodCall.getClassOrInterfaceDeclaration().getNameAsString());
        for (String arg : methodCall.getArgs()) {
            stringBuffer.append("-" + arg);
        }
        return stringBuffer.toString();
    }

    public static File getFullPath(List<SourceRoot> sourceRoots, String joinPath) {
        for (SourceRoot sourceRoot : sourceRoots) {
            File file = new File(sourceRoot.getRoot().toAbsolutePath() + "//" + joinPath);
            //我们需要获得相对地址。确保地址存在
            if (file.exists()) {
                return file;
            }
        }
        return null;

    }
}
