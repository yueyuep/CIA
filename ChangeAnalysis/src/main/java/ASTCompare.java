import change.MethodChange;
import change.VaribaleChange;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import map.FileMap;
import map.MethodMap;
import process.MethodProcess;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author:yueyue on 2020/12/18 17:17
 * @Param:
 * @return:
 * @Description: use Gumtree AST to compare
 */
public class ASTCompare {
    private FileMap fileMap = null;
    private List<VaribaleChange> allvarchanges = new ArrayList<>();


    public ASTCompare(FileMap fileMap) {
        this.fileMap = fileMap;


    }

    public void compare() {
        try {

            /*todo process field*/
            processfield();
            processmodify();
            procesDelete();
            processAdd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processfield() {
        /*todo */
    }

    private void processmodify() throws Exception {
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

    private void filterVariableChangeSet(CtMethod ctMethod, List<Operation> operations) {
        for (Operation op : operations) {
            VaribaleChange vc = VaribaleChange.newInstance(ctMethod, op);
            if (vc == null) {
                continue;
            } else {
                allvarchanges.add(vc);
            }

        }
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


        /*changetype、method2method、addmethod、 deletemethod
         * */
        HashMap<MethodChange, MethodChange> method2method = methodMap.getMethod2method();
        List<MethodChange> addmethod = methodMap.getAddmethod();
        List<MethodChange> deletemethod = methodMap.getDeletemethod();

        /*compare pairmethod*/
        for (MethodChange oldpair : method2method.keySet()) {
            MethodChange newpair = method2method.get(oldpair);
            AstComparator astComparator = new AstComparator();
            Diff pairResult = astComparator.compare(oldpair.getCtMethod(), newpair.getCtMethod());
            if (pairResult.getAllOperations().size() != 0) {
                List<Operation> diff = pairResult.getAllOperations();
                filterVariableChangeSet(newpair.getCtMethod(), diff);

            } else {
                System.out.printf("NO AST Change");

            }


        }
        /*compare addmethod*/
        for (MethodChange newadd : addmethod) {
            /*todo only get add variable*/
            CtMethod addm = newadd.getCtMethod();
            HashMap<String, List<CtVariable>> methodvariableList = newadd.getStringCtParameterHashMap();
            List<CtVariable> addvariablechange = methodvariableList.get(newadd.getMethodName());
            addvariablechange.forEach(va -> {
                VaribaleChange varibaleChange = new VaribaleChange(addm, va);
                allvarchanges.add(varibaleChange);
            });

        }

        /*compare deletemethod*/

        for (MethodChange olddelete : deletemethod) {
            /*todo only get delete variable*/
            CtMethod addm = olddelete.getCtMethod();
            HashMap<String, List<CtVariable>> methodvariableList = olddelete.getStringCtParameterHashMap();
            List<CtVariable> addvariablechange = methodvariableList.get(olddelete.getMethodName());
            addvariablechange.forEach(va -> {
                VaribaleChange varibaleChange = new VaribaleChange(addm, va);
                allvarchanges.add(varibaleChange);
            });

        }


    }

    /*process delete file*/

    private void procesDelete() {
        Set<String> delFile = fileMap.getDeleteFile();
        for (String del : delFile) {
            MethodProcess methodProcess_old = (MethodProcess) parse(del + fileMap.getSourcedir(), new MethodProcess());
            HashMap<CtMethod, List<CtVariable>> methodListHashMap = methodProcess_old.getCtMethodList();
            singleMethodCompare(methodListHashMap);
        }
    }

    /*process add file*/
    private void processAdd() {
        Set<String> addFile = fileMap.getAddFile();
        for (String add : addFile) {
            MethodProcess methodProcess_new = (MethodProcess) parse(add + fileMap.getSourcedir(), new MethodProcess());
            HashMap<CtMethod, List<CtVariable>> methodListHashMap = methodProcess_new.getCtMethodList();
            singleMethodCompare(methodListHashMap);
        }


    }
    /*process single file(add or delete)*/

    private void singleMethodCompare(HashMap<CtMethod, List<CtVariable>> ctMethodListHashMap) {
        for (CtMethod ctMethod : ctMethodListHashMap.keySet()) {
            List<CtVariable> variables = ctMethodListHashMap.get(ctMethod);
            variables.forEach(ctVariable -> {
                VaribaleChange varibaleChange = new VaribaleChange(ctMethod, ctVariable);
                if (varibaleChange != null) {
                    allvarchanges.add(varibaleChange);
                }
            });

        }
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

    public FileMap getFileMap() {
        return fileMap;
    }

    public List<VaribaleChange> getAllvarchanges() {
        return allvarchanges;
    }
}
