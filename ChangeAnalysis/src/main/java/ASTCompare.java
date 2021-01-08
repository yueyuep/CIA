import change.MethodChange;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import map.FileMap;
import map.MethodMap;
import process.MethodProcess;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @Author:yueyue on 2020/12/18 17:17
 * @Param:
 * @return:
 * @Description: use Gumtree AST to compare
 */
public class ASTCompare {
    AstComparator astComparator = null;
    FileMap fileMap = null;


    public ASTCompare(FileMap fileMap) {
        this.fileMap = fileMap;
        astComparator = new AstComparator();

    }

    public void compare() {
        try {
            process();
            procesDelete();
            processAdd();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void process() throws Exception {
        HashMap<String, String> pairFile = fileMap.getFile2file();
        for (String src : pairFile.keySet()) {
            MethodProcess methodProcess_old = (MethodProcess) parse(fileMap.getSourcedir() + src, new MethodProcess());
            MethodProcess methodProcess_new = (MethodProcess) parse(fileMap.getTargetdir() + pairFile.get(src), new MethodProcess());
            HashMap<CtMethod, List<CtVariable>> ctMethodList_old = methodProcess_old.getCtMethodList();
            HashMap<CtMethod, List<CtVariable>> ctMethodList_new = methodProcess_new.getCtMethodList();
            methodLevelCompre(ctMethodList_new, ctMethodList_old);
        }
    }

    private AbstractProcessor parse(String src, AbstractProcessor abstractProcessor) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(src);
        launcher.run();
        Factory factory = launcher.getFactory();
        ProcessingManager processingManager = new QueueProcessingManager(factory);
        processingManager.addProcessor(abstractProcessor);
        processingManager.process(factory.Class().getAll());
        return abstractProcessor;
    }

    private List<CtVariable> filterParameters(List<Operation> operations) {
        List<CtVariable> ctVariableList1 = new ArrayList<>();
        for (Operation op : operations) {
            CtElement src = op.getSrcNode();
            CtElement dist = op.getDstNode();
            List<CtVariable> srcVar = src.getElements(new TypeFilter<>(CtVariable.class));
            List<CtVariable> distVal = dist.getElements(new TypeFilter<>(CtVariable.class));
            ctVariableList1.addAll(srcVar);
            ctVariableList1.addAll(distVal);
        }
        return ctVariableList1;
    }

    /*compare Ctmethod(inclue Ctvariable)*/
    public void methodLevelCompre(HashMap<CtMethod, List<CtVariable>> ctMethodList_old, HashMap<CtMethod, List<CtVariable>> ctMethodList_new) {
        HashMap<String, MethodChange> oldMethod = new HashMap<>();
        HashMap<String, MethodChange> newMethod = new HashMap<>();
        /*process oldversion*/
        for (CtMethod ctMethod : ctMethodList_old.keySet()) {
            MethodChange methodChange = new MethodChange(ctMethod, ctMethodList_old.get(ctMethod));
            oldMethod.put(methodChange.getMethodName(), methodChange);

        }
        /*process newversion*/
        for (CtMethod ctMethod : ctMethodList_new.keySet()) {
            MethodChange methodChange = new MethodChange(ctMethod, ctMethodList_new.get(ctMethod));
            newMethod.put(methodChange.getMethodName(), methodChange);

        }
        MethodMap methodMap = new MethodMap(oldMethod, newMethod);
        /*changetype
         * method2method
         * addmethod
         * deletemethod
         *
         * */
        HashMap<MethodChange, MethodChange> method2method = methodMap.getMethod2method();
        List<MethodChange> addmethod = methodMap.getAddmethod();
        List<MethodChange> deletemethod = methodMap.getDeletemethod();

        /*process pair*/
        for (MethodChange oldpair : method2method.keySet()) {
            MethodChange newpair = method2method.get(oldpair);
            AstComparator astComparator = new AstComparator();
            Diff pairResult = astComparator.compare(oldpair.getCtMethod(), newpair.getCtMethod());


        }
        /*procedd add*/
        for (MethodChange newadd : addmethod) {
            /*todo only get add variable*/

        }

        /*process delete*/

        for (MethodChange olddelete : deletemethod) {
            /*todo only get delete variable*/

        }


    }

    private void procesDelete() {
    }

    private void processAdd() {
    }

    /*test case*/
    public static void main(String[] args) throws Exception {
        File oldFile = new File("H:\\CIA-master\\ChangeAnalysis\\src\\main\\resources\\case1\\Test1.java");
        File newFile = new File("H:\\CIA-master\\ChangeAnalysis\\src\\main\\resources\\case1\\Test2.java");
        AstComparator astComparator = new AstComparator();
        Diff result = astComparator.compare(oldFile, newFile);
        /*diff*/
        if (result.getRootOperations().size() != 0) {
            List<Operation> operations = result.getRootOperations();
            System.out.printf("diff");
        }
    }
}
