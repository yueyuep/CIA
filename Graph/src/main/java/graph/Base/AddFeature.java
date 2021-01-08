package graph.Base;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.*;

/**
 * Create by lp on 2020/2/9
 * 为我们的node添加信息
 */
public class AddFeature {
    private StringBuilder mStringBuilder = new StringBuilder();

    public <T extends Node> String travelNode(T node) {
        String nodeClassPackage = node.getClass().toString();
        String[] nodeClassPackageSplit = node.getClass().toString().split("\\.");
        String nodeClass = nodeClassPackageSplit[nodeClassPackageSplit.length - 1];
        node.removeComment();

        mStringBuilder.setLength(0);
        addStringToBuilder(SplitString.splitUntilUpperCase(nodeClass));

        if (isContain(nodeClassPackage, "Comment")) {//注释类型
        } else if (isContain(nodeClassPackage, new String[]{"VoidType", "UnknownType"})) {//无返回类型、不知道的类型
        } else if (isContain(nodeClassPackage, "WildcardType")) {//泛型
            WildcardType wildcardType = (WildcardType) node;
            wildcardType.getSuperType().ifPresent(c -> {
                addStringToBuilder("SuperType");
            });
            wildcardType.getExtendedType().ifPresent(c -> {
                addStringToBuilder("ExtendedType");
            });
        } else if (isContain(nodeClassPackage, "UnionType")) {
            UnionType unionType = (UnionType) node;

        } else if (isContain(nodeClassPackage, "IntersectionType")) {
            IntersectionType intersectionType = (IntersectionType) node;

        } else if (isContain(nodeClassPackage, "ArrayType")) {
            ArrayType arrayType = (ArrayType) node;

            addStringToBuilder(String.valueOf(arrayType.getArrayLevel()));
        } else if (isContain(nodeClassPackage, "Annotation")) {
            addStringToBuilder(node);
        } else if (isContain(nodeClassPackage, "InitializerDeclaration")) {
            InitializerDeclaration initializerDeclaration = (InitializerDeclaration) node;

        } else if (isContain(nodeClassPackage, "AnnotationMemberDeclaration")) {
            AnnotationMemberDeclaration annotationMemberDeclaration = (AnnotationMemberDeclaration) node;

            annotationMemberDeclaration.getDefaultValue().ifPresent((c) -> addStringToBuilder("default " + c));
        } else if (isContain(nodeClassPackage, "AnnotationDeclaration")) {
            AnnotationDeclaration annotationDeclaration = (AnnotationDeclaration) node;
            addStringToBuilder(annotationDeclaration.getName());
        } else if (isContain(nodeClassPackage, "FieldDeclaration")) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
        } else if (isContain(nodeClassPackage, "ClassOrInterfaceDeclaration")) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
            addStringToBuilder(classOrInterfaceDeclaration.getName());
        } else if (isContain(nodeClassPackage, "EnumDeclaration")) {
            EnumDeclaration enumDeclaration = (EnumDeclaration) node;
            addStringToBuilder(enumDeclaration.getName());
        } else if (isContain(nodeClassPackage, "EnumConstantDeclaration")) {
            EnumConstantDeclaration enumConstantDeclaration = (EnumConstantDeclaration) node;
            addStringToBuilder(enumConstantDeclaration.getName());
        } else if (isContain(nodeClassPackage, "MethodDeclaration")) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) node;
            processThrows(methodDeclaration.getThrownExceptions());
        } else if (isContain(nodeClassPackage, "ConstructorDeclaration")) {
            ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
            processThrows(constructorDeclaration.getThrownExceptions());
        } else if (isContain(nodeClassPackage, "ExplicitConstructorInvocationStmt")) {
            ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = (ExplicitConstructorInvocationStmt) node;
        } else if (isContain(nodeClassPackage, new String[]{"BreakStmt", "ContinueStmt"})) {
        } else if (isContain(nodeClassPackage, "ExpressionStmt")) {
        } else if (isContain(nodeClassPackage, "stmt")) {
        } else if (isContain(nodeClassPackage, "TypeParameter")) {
            TypeParameter parameter = (TypeParameter) node;
            if (parameter.getElementType().isUnknownType()) {
                addStringToBuilder("UnknownType");
                addStringToBuilder(parameter.getNameAsString());
            } else {
                addStringToBuilder(parameter.getNameAsString());
            }
        } else if (isContain(nodeClassPackage, "ParameterCompare")) {
            Parameter parameter = (Parameter) node;
            if (parameter.getType().isUnknownType()) {
                addStringToBuilder(SplitString.splitUntilUpperCase("UnknownType"));
                addStringToBuilder(parameter.getNameAsString());
            } else {
                addStringToBuilder(parameter.getNameAsString());
            }
        } else if (isContain(nodeClassPackage, "ClassOrInterfaceType")) {
            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) node;
            // TODO: If need normalization
            addStringToBuilder(classOrInterfaceType.getName());
        } else if (isContain(nodeClassPackage, "CatchClause")) {
            CatchClause catchClause = (CatchClause) node;

        } else if (isContain(nodeClassPackage, "VariableDeclarationExpr")) {

            VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) node;
            if (!variableDeclarationExpr.getModifiers().isEmpty()) {
                addStringToBuilder(variableDeclarationExpr.getModifiers().toString());
            }
        } else if (isContain(nodeClassPackage, "VariableDeclarator")) {

            VariableDeclarator variableDeclarator = (VariableDeclarator) node;
            addStringToBuilder(variableDeclarator.getType());
            // TODO: If need normalization
            addStringToBuilder(variableDeclarator.getNameAsString());
            variableDeclarator.getInitializer().ifPresent((consumer) -> {
                addStringToBuilder("=");
            });
        } else if (isContain(nodeClassPackage, new String[]{"SimpleName", "NameExpr", "Name"})) {
            // TODO: If need normalization
            addStringToBuilder(node);
        } else if (isContain(nodeClassPackage, "Binary")) {
            BinaryExpr binaryExpr = (BinaryExpr) node;

            addStringToBuilder(binaryExpr.getOperator().toString());
        } else if (isContain(nodeClassPackage, "Unary")) {
            UnaryExpr unaryExpr = (UnaryExpr) node;

            addStringToBuilder(unaryExpr.getOperator().asString());
        } else if (isContain(nodeClassPackage, "CastExpr")) {
            CastExpr castExpr = (CastExpr) node;

        } else if (isContain(nodeClassPackage, "ClassExpr")) {

            addStringToBuilder(node);
        } else if (isContain(nodeClassPackage, "MethodCallExpr")) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) node;

        } else if (isContain(nodeClassPackage, "MethodReferenceExpr")) {
            MethodReferenceExpr methodReferenceExpr = (MethodReferenceExpr) node;

            addStringToBuilder(methodReferenceExpr.getIdentifier());
        } else if (isContain(nodeClassPackage, "TypeExpr")) {

            addStringToBuilder(node);
        } else if (isContain(nodeClassPackage, "LiteralExpr")) {
            processLiteralExpr(node);
        } else if (isContain(nodeClassPackage, "AssignExpr")) {
            AssignExpr assignExpr = (AssignExpr) node;

            addStringToBuilder(assignExpr.getOperator());
        } else if (isContain(nodeClassPackage, "FieldAccessExpr")) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;

        } else if (isContain(nodeClassPackage, "ArrayAccessExpr")) {
            ArrayAccessExpr arrayAccessExpr = (ArrayAccessExpr) node;

            addStringToBuilder(arrayAccessExpr.getIndex());
        } else if (isContain(nodeClassPackage, "ArrayCreationExpr")) {
            ArrayCreationExpr arrayCreationExpr = (ArrayCreationExpr) node;

            addStringToBuilder(arrayCreationExpr.getElementType());
            if (arrayCreationExpr.getLevels().isEmpty()) {
                addStringToBuilder(SplitString.splitUntilUpperCase("ArrayCreationLevel Empty"));
            } else {
                addStringToBuilder(SplitString.splitUntilUpperCase("ArrayCreationLevel NotEmpty"));
            }
            if (arrayCreationExpr.getInitializer().isPresent()) {
                addStringToBuilder(SplitString.splitUntilUpperCase("ArrayInitializerExpr"));
            }
        } else if (isContain(nodeClassPackage, "ArrayInitializerExpr")) {
            ArrayInitializerExpr arrayInitializerExpr = (ArrayInitializerExpr) node;

        } else if (isContain(nodeClassPackage, "ArrayCreationLevel")) {
            ArrayCreationLevel arrayCreationLevel = (ArrayCreationLevel) node;
            if (arrayCreationLevel.getDimension().isPresent()) {
                addStringToBuilder("Dimension NotEmpty");
            } else {
                addStringToBuilder("Dimension Empty");
            }
        } else if (isContain(nodeClassPackage, "ObjectCreationExpr")) {
            ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) node;

            addStringToBuilder(objectCreationExpr.getType());
            addStringToBuilder(objectCreationExpr.getScope().toString());
            addStringToBuilder(objectCreationExpr.getTypeArguments().toString());
        } else if (isContain(nodeClassPackage, "LambdaExpr")) {
            LambdaExpr lambdaExpr = (LambdaExpr) node;

        } else if (isContain(nodeClassPackage, "EnclosedExpr")) {
            EnclosedExpr enclosedExpr = (EnclosedExpr) node;

        } else if (isContain(nodeClassPackage, "InstanceOfExpr")) {
            InstanceOfExpr instanceOfExpr = (InstanceOfExpr) node;

        } else if (isContain(nodeClassPackage, "MemberValuePair")) {
            MemberValuePair memberValuePair = (MemberValuePair) node;

        } else if (isContain(nodeClassPackage, new String[]{"ThisExpr", "SuperExpr"})) {

        } else {
            // TODO: If need normalization
            addStringToBuilder(node);
        }
        return mStringBuilder.toString();
    }


    /*被调用的其他函数*/
    private void addStringToBuilder(Object object) {//添加字符串
        mStringBuilder.append(object.toString());
        mStringBuilder.append(" ");
    }

    private void processThrows(NodeList<ReferenceType> thrownExceptions) {//添加异常
        if (!thrownExceptions.isEmpty()) {
            addStringToBuilder("Throws");
        }
    }


    private void processLiteralExpr(Node node) {
        String nodeClassPackage = node.getClass().toString();
        String[] nodeClassPackageSplit = node.getClass().toString().split("\\.");
        String nodeClass = nodeClassPackageSplit[nodeClassPackageSplit.length - 1];
        if (isContain(nodeClassPackage, new String[]{"BooleanLiteralExpr", "CharLiteralExpr"})) {
            addStringToBuilder(node);
        } else if (isContain(nodeClassPackage, "StringLiteralExpr")) {
            StringLiteralExpr stringLiteralExpr = (StringLiteralExpr) node;
            if (stringLiteralExpr.asString().isEmpty()) {
                addStringToBuilder("Empty ");
            } else {
                addStringToBuilder("Not Empty ");
            }
        } else if (isContain(nodeClassPackage, "DoubleLiteralExpr")) {
            DoubleLiteralExpr doubleLiteralExpr = (DoubleLiteralExpr) node;
            if (doubleLiteralExpr.asDouble() == 0.0) {
                addStringToBuilder("Zero ");
            } else {
                addStringToBuilder("Note Zero ");
            }
        } else if (isContain(nodeClassPackage, "IntegerLiteralExpr")) {
            IntegerLiteralExpr integerLiteralExpr = (IntegerLiteralExpr) node;
            if (integerLiteralExpr.asInt() == 0) {
                addStringToBuilder("Zero ");
            } else {
                addStringToBuilder("Note Zero ");
            }
        } else if (isContain(nodeClassPackage, "LongLiteralExpr")) {
            LongLiteralExpr longLiteralExpr = (LongLiteralExpr) node;
            if (longLiteralExpr.asLong() == 0l) {
                addStringToBuilder("Zero ");
            } else {
                addStringToBuilder("Note Zero ");
            }
        } else if (isContain(nodeClassPackage, "NullLiteralExpr")) {
        }
    }

    private String stringPrint(String string) {
        return string;
    }

    private String nodePrint(Node node) {
        return node.toString();
    }

    public boolean isContain(String master, String sub) {
        return master.contains(sub);
    }

    public boolean isContain(String master, String[] sub) {
        for (String s : sub) {
            if (isContain(master, s)) {
                return true;
            }
        }
        return false;
    }

}
