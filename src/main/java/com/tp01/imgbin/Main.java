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
     *
     * @param args
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {

        if (args == null || args.length == 0) {
            System.out.println("sh launch.sh path/to/file.txt METHOD REPORT # Lancer minCut sur file.txt\n"
                    + "    METHOD: --method=preflow|ford_fulkerson_bfs|ford_fulkerson_sp\n"
                    + "    REPORT: --withReport|empty");

            System.out.println("Ex: sh launch.sh examples/4x4.txt --method=preflow --withReport # Préflot, sans graphviz\n"
                    + "sh launch_univ.sh examples/4x4.txt --method=preflow --withReport # Sous le proxy de l'université nantes");
        }

        if (!args[0].endsWith(".txt")) {
            throw new Exception("Input file must be .txt file!");
        }

        Graph g = Graph.ConstructionReseau(args[0]);
        Utils utils = new Utils(g);

        boolean withReport = args.length == 3 && args[2] != null && args[2].equals("--withReport");

        switch (args[1]) {
            case "--method=ford_fulkerson":
                utils.ResoudreBinIm(MaxFlowAlgorithm.FORD_FULKERSON_BFS, withReport);
                break;
            case "--method=preflow":
                utils.ResoudreBinIm(MaxFlowAlgorithm.PREFLOW, withReport);
                break;
        }
    }

}
