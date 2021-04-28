package map;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author:yueyue on 2020/12/18 16:28
 * @Param:
 * @return:
 * @Description: extract java file from project
 */
public class ExtractJfile {
    private File dir = null;
    private List<File> fileList = new ArrayList<>();

    public ExtractJfile(File dir) {
        this.dir = dir;
        try {
            getFileList(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFileList(File dir) throws Exception {

        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException(dir + "is not dir");
        } else {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        fileList.add(file);
                    } else {
                        file.delete();
                    }
                } else {
                    /*skip test direc*/
                    if ("test".equals(file.getName())) {
                        continue;
                    } else {
                        getFileList(file);
                    }
                }
            }
        }
    }

    public File[] getFile() {
        int size = fileList.size();
        return fileList.toArray(new File[size]);
    }
}


