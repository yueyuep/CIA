import com.github.javaparser.utils.SourceRoot;
import graph.GraphParse;
import graph.Unity.ProjectInfo;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import vmt.BuildGraph;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Create by yueyue on 2020/12/19
 */
public class Runner {
    public static void main(String[] args) throws Exception {
        /*project path*/
        //Path path = Utils.openDirFileChooser().toPath();
        Path path = Paths.get("H:\\CIA-master\\graph\\src\\main\\resources\\Example\\");
        //项目的具体信息
        ProjectInfo projectInfo = new ProjectInfo(path);
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(path);
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots()
                .stream()
                .filter(sourceRoot -> !sourceRoot.toString().contains("test"))
                .collect(Collectors.toList());
        BuildGraph buildGraph = new BuildGraph(sourceRoots, projectInfo);
    }
}
