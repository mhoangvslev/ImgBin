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

    public static void main(String[] args) throws IOException {
        Graph g = Graph.fromFile(args[0]);
        Utils utils = new Utils(g);

        switch (args[1]) {
            case "ford_fulkerson_bfs":
                utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BFS, true);
                break;
            case "ford_fulkerson_sp":
                utils.minCut(MaxFlowAlgorithm.FORD_FULKERSON_BELLMAN_FORD, true);
                break;
            case "preflow":
                utils.minCut(MaxFlowAlgorithm.PREFLOW, true);
                break;
        }
    }

}
