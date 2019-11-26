/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

import java.io.IOException;

/**
 *
 * @author minhhoangdang
 */
public class Main {

    /**
     * Read a file with specific commands
     * @param args
     * @throws IOException
     * @throws Exception 
     */
    public static void main(String[] args) throws IOException, Exception {
        if (!args[0].endsWith(".txt")) {
            throw new Exception("Input file must be .txt file!");
        }

        Graph g = Graph.fromFile(args[0]);
        Utils utils = new Utils(g);

        boolean withReport = args[2] != null && args[2].equals("--withReport");

        switch (args[1]) {
            case "--method=ford_fulkerson_bfs":
                utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BFS, withReport);
                break;
            case "--method=ford_fulkerson_sp":
                utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BELLMAN_FORD, withReport);
                break;
            case "--method=preflow":
                utils.minCut(MaxFlowAlgorithm.PREFLOW, withReport);
                break;
        }
    }

}
