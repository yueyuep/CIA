package process;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

/**
 * Create by yueyue on 2020/12/30
 */
public class ClassProcess extends AbstractProcessor<CtClass<?>> {

    @Override
    public void process(CtClass<?> element) {
      System.err.println("test");

    }
}
