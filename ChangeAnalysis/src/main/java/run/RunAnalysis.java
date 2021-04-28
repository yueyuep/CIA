package run;
import change.VaribaleChange;
import commons.Utils;
import map.FileMap;
import map.ProjectInfo;
import java.io.File;
import java.util.List;
/**
 * @author :yueyue on 2020/12/18 16:08
 * @return:
 * @Description: changeVariableLocation
 */
public class RunAnalysis {
    public static void main(String[] args) {
        String source = "H:\\CIA-master\\data\\oldversion\\ant-rel-1.4";
        String target = "H:\\CIA-master\\data\\newversion\\ant-rel-1.5";
        try {
            run(source, target);
        }catch (Exception e){
            /**/
        }
    }
    public static void run(String source, String target) throws Exception {
        try {
            File s = new File(source);
            File t = new File(target);
            /*ProjectInfo to get filepaire*/
            ProjectInfo projectInfo_s = new ProjectInfo(s);
            ProjectInfo projectInfo_t = new ProjectInfo(t);
            List<String> file_s = projectInfo_s.getFileWithoutDir();
            List<String> file_t = projectInfo_t.getFileWithoutDir();
            /*generate old_new file pair、deletefile、addfile*/
            FileMap fileMap = new FileMap(source, target, file_s, file_t);
            /*compare*/
            ASTCompare astCompare = new ASTCompare(fileMap);
            astCompare.compare();
            List<VaribaleChange> varibaleChanges = astCompare.getAllvarchanges();

            System.out.printf("changeSet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}