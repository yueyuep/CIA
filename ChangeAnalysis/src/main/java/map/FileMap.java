package map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author:yueyue on 2020/12/18 16:33
 * @Param:
 * @return:
 * @Description: <file,file> compareSet
 */
public class FileMap {

    private String sourcedir = "";
    private String targetdir = "";
    HashMap<String, String> file2file = new HashMap<>();
    Set<String> deleteFile = new HashSet<>();
    Set<String> addFile = new HashSet<>();


    public FileMap(String sourcedir, String targetdir, List<String> source, List<String> target) {
        /*oldfile->newfile*/
        setFile2file(source, target);
        this.sourcedir = sourcedir;
        this.targetdir = targetdir;
    }

    private void setFile2file(List<String> source, List<String> target) {
        /*compare the same path*/
        boolean flagdelete = false;
        boolean flagadd = false;
        for (String sourcepath : source) {
            for (String targetpath : target) {
                if (sourcepath.equals(targetpath)) {
                    flagdelete = true;
                    file2file.put(sourcepath, targetpath);
                    break;
                }
            }
            if (!flagdelete) {
                flagdelete = false;
                deleteFile.add(sourcepath);
            }
        }
        for (String targetpath : target) {
            for (String sourcepath : source) {
                if (sourcepath.equals(targetpath)) {
                    flagadd = true;
                    break;
                }
            }
            if (!flagadd) {
                flagadd = false;
                addFile.add(targetpath);
            }
        }


    }

    public HashMap<String, String> getFile2file() {
        return file2file;
    }

    public Set<String> getAddFile() {
        return addFile;
    }

    public Set<String> getDeleteFile() {
        return deleteFile;
    }

    public String getSourcedir() {
        return sourcedir;
    }

    public String getTargetdir() {
        return targetdir;
    }
}
