package vmt.enity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:yueyue on 2020/12/22 11:00
 * @Param:
 * @return:
 * @Description: set of MS
 */
public class MT {
    Node method = null;
    Node val1 = null;
    List<Node> parmeters = new ArrayList<>();
    List<MT> ms = new ArrayList<>();
}
