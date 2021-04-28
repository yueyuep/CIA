package commons;

import change.VaribaleChange;

import java.util.List;

/**
 * Create by yueyue on 2021/4/27
 */
public class Utils {

    public static void varibaleChangePrintForList(List<VaribaleChange> varibaleChanges) {
        for (VaribaleChange vbc : varibaleChanges) {
            varibaleChangePrint(vbc);
        }

    }

    public static void varibaleChangePrint(VaribaleChange vbc) {

        String split = "********************************\n";
        String path = "path:" + vbc.getFilepath() + "\n";
        String lineNo = "lineNo:" + vbc.getLinenumber() + "\n";
        String expression = "expression:" + vbc.getVariableName() + "\n";
        String methodName = "methodName:" + vbc.getMethodNmame() + "\n";

        System.out.println(split + path + lineNo + expression + methodName);


    }

}
