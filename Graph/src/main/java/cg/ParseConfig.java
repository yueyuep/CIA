package cg;

import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by yueyue on 2021/1/8
 */
public class ParseConfig implements Serializable {

    private JavaSymbolSolver javaSymbolSolver = null;
    /*java source code path*/
    private String src = null;
    private String dist = null;
    private List<File> fileList = new ArrayList<>();

    /**
     * @throws FileNotFoundException
     */
    public ParseConfig(String src, String dist) throws FileNotFoundException {
        this.src = src;
        this.dist = dist;
        extractALlFiles(new File(src));
        JavaParserTypeSolver javaParserTypeSolver = new JavaParserTypeSolver(src);
        ReflectionTypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(javaParserTypeSolver);
        combinedTypeSolver.add(reflectionTypeSolver);
        this.javaSymbolSolver = new JavaSymbolSolver(combinedTypeSolver);
    }

    /*withdraw all file from source code path(skip test)*/
    public void extractALlFiles(File rootpath) throws FileNotFoundException {

        for (File file : rootpath.listFiles()) {
            /*
             *fixme:use keyword "test" to filter test dic?
             * */
            if (file.getPath().contains("test")) {
                continue;
            }
            if (file.isFile()) {
                this.fileList.add(file);

            } else {
                extractALlFiles(file);
            }
        }

    }

    public JavaSymbolSolver getJavaSymbolSolver() {
        return javaSymbolSolver;
    }

    public String getSrc() {
        return src;
    }

    public String getDist() {
        return dist;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setJavaSymbolSolver(JavaSymbolSolver javaSymbolSolver) {
        this.javaSymbolSolver = javaSymbolSolver;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
