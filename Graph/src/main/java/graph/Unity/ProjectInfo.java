package graph.Unity;

import java.nio.file.Path;

/**
 * Create by lp on 2020/4/25
 */
public class ProjectInfo {
    /*记录项目在本地的地址*/
    String version;
    String prifxPath;

    public ProjectInfo() {
    }

    public ProjectInfo(Path path) {
        prifxPath = path.toString() + "\\";
        String[] strings = path.toString().split("\\\\");
        this.version = strings[strings.length - 1];

    }

    public String getVersion() {
        return version;
    }

    public String getPrifxPath() {
        return prifxPath;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setPrifxPath(String prifxPath) {
        this.prifxPath = prifxPath;
    }
}
