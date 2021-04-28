import com.github.javaparser.utils.SourceRoot;
import com.google.common.graph.MutableNetwork;
import graph.Unity.ProjectInfo;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import graph.Utils;
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
        //Path path1 = Utils.openDirFileChooser().toPath();

        /*project dir*/
        Path path = Paths.get("H:\\CIA-master\\graph\\src\\main\\resources\\Example\\");
        ProjectInfo projectInfo = new ProjectInfo(path);
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(path);

        /*configure*/
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots()
                .stream()
                .filter(sourceRoot -> !sourceRoot.toString().contains("test"))
                .collect(Collectors.toList());

        /*build vpt*/
        BuildGraph buildGraph = new BuildGraph(sourceRoots, projectInfo);
        MutableNetwork<Object, String> network = buildGraph.getAst2Graph().getmNetwork();

    }
}
