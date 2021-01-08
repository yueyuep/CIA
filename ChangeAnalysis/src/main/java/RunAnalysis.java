import gumtree.spoon.AstComparator;
import map.FileMap;
import map.ProjectInfo;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * @Author:yueyue on 2020/12/18 16:08
 * @Param:
 * @return:
 * @Description: changeLocation
 */
public class RunAnalysis {

    public static void main(String[] args) {
        String sourceDir = "H:\\CIA-master\\data\\oldversion";
        String targetDir = "H:\\CIA-master\\data\\newversion";
        run(sourceDir, targetDir);
    }


    public static void run(String sourth, String target) {
        try {
            File s = new File(sourth);
            File t = new File(target);

            /*ProjectInfo to get filepaire*/
            ProjectInfo projectInfo_s = new ProjectInfo(s);
            ProjectInfo projectInfo_t = new ProjectInfo(t);

            List<String> file_s = projectInfo_s.getFileWithoutDir();
            List<String> file_t = projectInfo_t.getFileWithoutDir();
            /*generate old_new file pair、deletefile、addfile*/
            FileMap fileMap = new FileMap(sourth, target, file_s, file_t);

            /*compare*/
            ASTCompare astCompare = new ASTCompare(fileMap);
            astCompare.compare();


        } catch (Exception e) {
            e.printStackTrace();

        }


    }


}
