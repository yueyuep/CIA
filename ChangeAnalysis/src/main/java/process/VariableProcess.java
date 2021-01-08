package process;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by yueyue on 2020/12/30
 */
public class VariableProcess extends AbstractProcessor<CtVariable<?>> {

    List<CtVariable> ctVariableList = new ArrayList<>();

    @Override
    public void process(CtVariable<?> element) {
        ctVariableList.add(element);

    }

    public List<CtVariable> getCtVariableList() {
        return ctVariableList;
    }
}
