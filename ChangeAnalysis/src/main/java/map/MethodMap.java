package map;

import change.MethodChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create by yueyue on 2021/1/8
 */
public class MethodMap {
    private HashMap<MethodChange, MethodChange> method2method = new HashMap<>();
    private List<MethodChange> addmethod = new ArrayList<>();
    private List<MethodChange> deletemethod = new ArrayList<>();

    public MethodMap(HashMap<String, MethodChange> oldmethod, HashMap<String, MethodChange> newmethod) {

        setMethod2method(oldmethod, newmethod);

    }

    /*todo need to use parame*/
    private void setMethod2method(HashMap<String, MethodChange> oldmethod, HashMap<String, MethodChange> newmethod) {
        List<String> oldmethodName = new ArrayList<>(oldmethod.keySet());
        List<String> newmethodName = new ArrayList<>(newmethod.keySet());
        for (String oldname : oldmethodName) {
            if (newmethodName.contains(oldname)) {
                method2method.put(oldmethod.get(oldname), newmethod.get(oldname));
            } else {
                deletemethod.add(oldmethod.get(oldname));
            }
        }
        for (String newname : newmethodName) {
            if (oldmethodName.contains(newname)) {
                method2method.put(oldmethod.get(newname), newmethod.get(newname));
            } else {
                addmethod.add(newmethod.get(newname));
            }
        }


    }

    public HashMap<MethodChange, MethodChange> getMethod2method() {
        return method2method;
    }

    public List<MethodChange> getAddmethod() {
        return addmethod;
    }

    public List<MethodChange> getDeletemethod() {
        return deletemethod;
    }
}
