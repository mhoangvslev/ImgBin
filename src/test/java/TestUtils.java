
import com.tp01.imgbin.Graph;
import com.tp01.imgbin.MaxFlowAlgorithm;
import com.tp01.imgbin.Node;
import com.tp01.imgbin.Utils;
import java.io.IOException;
import java.util.HashSet;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author minhhoangdang
 */
public class TestUtils {

    @Test
    public void testMinCut1() throws IOException {

        Graph g = new Graph("test1");
        g.addArc("source", "1", 16, false);
        g.addArc("source", "2", 13, false);
        g.addArc("1", "2", 10, false);
        g.addArc("2", "1", 4, false);
        g.addArc("1", "3", 12, false);
        g.addArc("3", "2", 9, false);
        g.addArc("2", "4", 14, false);
        g.addArc("4", "3", 7, false);
        g.addArc("3", "sink", 20, false);
        g.addArc("4", "sink", 4, false);
        System.out.println(g);

        // Run preflow
        Utils utils = new Utils(g);
        utils.ResoudreBinIm(MaxFlowAlgorithm.PREFLOW, false);
        HashSet<Node> sSet1 = utils.getsSet();
        HashSet<Node> tSet1 = utils.gettSet();

        utils.ResoudreBinIm(MaxFlowAlgorithm.FORD_FULKERSON_BFS, false);
        HashSet<Node> sSet2 = utils.getsSet();
        HashSet<Node> tSet2 = utils.gettSet();

        assertEquals(sSet1, sSet2);
        assertEquals(tSet1, tSet2);

    }

    @Test
    /**
     * https://en.wikipedia.org/wiki/Max-flow_min-cut_theorem#Example
     */
    public void testMinCut2() throws IOException {

        Graph g = new Graph("test2");
        g.addArc("source", "1", 4, false);
        g.addArc("source", "2", 4, false);
        g.addArc("1", "2", 3, false);
        g.addArc("1", "sink", 4, false);
        g.addArc("2", "sink", 5, false);
        System.out.println(g);

        // Run preflow
        Utils utils = new Utils(g);
        utils.ResoudreBinIm(MaxFlowAlgorithm.PREFLOW, false);
        HashSet<Node> sSet1 = utils.getsSet();
        HashSet<Node> tSet1 = utils.gettSet();

        utils.ResoudreBinIm(MaxFlowAlgorithm.FORD_FULKERSON_BFS, false);
        HashSet<Node> sSet2 = utils.getsSet();
        HashSet<Node> tSet2 = utils.gettSet();

        assertEquals(sSet1, sSet2);
        assertEquals(tSet1, tSet2);
    }
}
