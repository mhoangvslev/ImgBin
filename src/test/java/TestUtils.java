
import com.tp01.imgbin.Graph;
import com.tp01.imgbin.MaxFlowAlgorithm;
import com.tp01.imgbin.Utils;
import java.io.IOException;
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
        g.addArc("source", "1", 16);
        g.addArc("source", "2", 13);
        g.addArc("1", "2", 10);
        g.addArc("2", "1", 4);
        g.addArc("1", "3", 12);
        g.addArc("3", "2", 9);
        g.addArc("2", "4", 14);
        g.addArc("4", "3", 7);
        g.addArc("3", "sink", 20);
        g.addArc("4", "sink", 4);
        System.out.println(g);

        // Run preflow
        Utils utils = new Utils(g);
        assertEquals(utils.minCut(MaxFlowAlgorithm.PREFLOW, true), utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BFS, true));
    }

    @Test
    /**
     * https://en.wikipedia.org/wiki/Max-flow_min-cut_theorem#Example
     */
    public void testMinCut2() throws IOException {

        Graph g = new Graph("test2");
        g.addArc("source", "1", 4);
        g.addArc("source", "2", 4);
        g.addArc("1", "2", 3);
        g.addArc("1", "sink", 4);
        g.addArc("2", "sink", 5);
        System.out.println(g);

        // Run preflow
        Utils utils = new Utils(g);
        assertEquals(utils.minCut(MaxFlowAlgorithm.PREFLOW, true), utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BFS, true));
    }
}
