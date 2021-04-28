package process;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create by yueyue on 2020/12/30
 */
public class MethodProcess extends AbstractProcessor<CtMethod<?>> {
    private HashMap<CtMethod, List<CtVariable>> ctMethodList = new HashMap<>();

    @Override
    public void process(CtMethod<?> element) {
        /*去掉注释*/
        element.setComments(new ArrayList<CtComment>());
        /*去除日志相关*/
        CtBlock ctBlock = element.getBody();
        if (ctBlock == null) {
            /*函数体内容为空*/
            return;
        }
        List<CtStatement> newct = new ArrayList<>();
        for (CtStatement ctStatement : ctBlock.getStatements()) {
            if (ctStatement.toString().contains("System.out")
                    || ctStatement.toString().contains("Logger")
                    || ctStatement.toString().contains("logger")) {
                continue;
            } else {
                newct.add(ctStatement);
            }

        }
        List<CtVariable> ctVariableList = element.getElements(new TypeFilter<>(CtVariable.class));
        ctBlock.setStatements(newct);
        ctMethodList.put(element, ctVariableList);

    }


    public HashMap<CtMethod, List<CtVariable>> getCtMethodList() {
        return ctMethodList;
    }
}
