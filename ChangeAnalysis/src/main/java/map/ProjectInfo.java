package map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author:yueyue on 2020/12/18 16:53
 * @Param:
 * @return:
 * @Description: Project Information
 */
public class ProjectInfo {

    File dir = null;
    File[] filepath = null;
    List<String> fileWithoutDir = new ArrayList<>();

    public ProjectInfo(File dir) {
        this.dir = dir;
        init_filepath();
    }

    private void init_filepath() {
        ExtractJfile extractJfile = new ExtractJfile(dir);
        filepath = extractJfile.getFile();
        deleteDir();

    }

    private void deleteDir() {
        for (File file : filepath) {
            fileWithoutDir.add(file.getPath().replace(dir.getPath(), ""));
        }
    }

    public File[] getFilepath() {
        return filepath;
    }

    public List<String> getFileWithoutDir() {
        return fileWithoutDir;
    }

    public File getDir() {
        return dir;
    }
}
