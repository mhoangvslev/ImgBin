
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
    public void testMinCut() throws IOException {

        Graph g1 = new Graph();
        g1.addArc("source", "1", 16);
        g1.addArc("source", "2", 13);
        g1.addArc("1", "2", 10);
        g1.addArc("2", "1", 4);
        g1.addArc("1", "3", 12);
        g1.addArc("3", "2", 9);
        g1.addArc("2", "4", 14);
        g1.addArc("4", "3", 7);
        g1.addArc("3", "sink", 20);
        g1.addArc("4", "sink", 4);
        System.out.println(g1);

        // Run preflow
        Utils utils = new Utils(g1);
        assertEquals(utils.minCut(MaxFlowAlgorithm.PREFLOW, false), utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BFS, false));
    }
}
