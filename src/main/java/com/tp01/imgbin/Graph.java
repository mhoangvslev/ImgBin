/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author minhhoangdang
 */
public class Graph {

    private final HashMap<String, Node> S;
    private final HashMap<String, Arc> A;
    private final HashMap<String, Arc> rA;

    private HashMap<Integer, LinkedList<Node>> ranks;
    private HashMap<Node, Integer> groups;

    public Graph() {
        this.A = new HashMap<>();
        this.S = new HashMap<>();
        this.rA = new HashMap<>();
        this.groups = new HashMap<>();
        this.ranks = new HashMap<>();
    }

    public void addNode(Node n) {
        this.S.put(n.getName(), n);
    }

    public void addArc(String u, String v, double capacity) {
        Arc a = new Arc(u, v, capacity);
        Arc reverse = a.getReverse();

        this.A.put(a.getName(), a);
        this.rA.put(reverse.getName(), reverse);

        this.addNode(a.getU());
        this.addNode(a.getV());
    }

    public void addArc(String u, String v, double capacity, double flow) {
        Arc a = new Arc(u, v, capacity, flow);
        Arc reverse = a.getReverse();

        this.A.put(a.getName(), a);
        this.rA.put(reverse.getName(), reverse);

        this.addNode(a.getU());
        this.addNode(a.getV());
    }

    public Node getNode(String name) {
        return this.S.get(name);
    }

    public Collection<Node> getNodes() {
        return S.values();
    }

    public Collection<Arc> getArcs() {
        HashSet<Arc> res = new HashSet<>();
        res.addAll(A.values());
        res.addAll(rA.values());
        return res;
    }

    public Arc getArc(Node u, Node v) {
        Arc res = this.A.get(u + "-" + v);
        if (res == null) {
            return rA.get(u + "-" + v);
        }
        return res;
    }

    public Collection<Node> Adj(Node n) {
        List<Node> res = new ArrayList<>();

        for (Arc a : this.A.values()) {
            if (a.getU().equals(n)) {
                res.add(a.getV());
            }
        }

        for (Arc a : this.rA.values()) {
            if (a.getU().equals(n)) {
                res.add(a.getV());
            }
        }

        return res;
    }

    /**
     * Convert to Graphviz .dot file
     * <a href="https://martin-thoma.com/how-to-draw-a-finite-state-machine/">Source</a>
     *
     * @param fileName
     * @param withReverse
     * @throws java.io.IOException
     */
    public void toDot(String fileName, boolean withReverse) throws IOException {
        String dir = ".";
        String dotFile = dir + "/graphviz/" + fileName + ".dot";
        //System.out.println(dir);

        /* Initialisation */
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(dotFile))) {
            /* Initialisation */
            fw.write("digraph G {\r\n");
            //fw.write("\trankdir=LR\r\n");

            /* Les formes pour le puits */
            fw.write("\tnode [shape = doublecircle]; sink;\r\n");

            /* Les formes pour la source */
            fw.write("\tnode [shape = circle, penwidth = 3]; source;\r\n"
                    + "\tnode [shape = circle, penwidth = 1];\r\n");

            /* Ecrire les noeuds */
            for (Node n : this.getNodes()) {
                fw.write("\t\"" + n + "\" [ style=\"filled, dashed\", fillcolor=\"" + n.getColour() + "\" ];\r\n");
            }

            /* Ecrire les arc */
            for (Arc a : this.A.values()) {
                String u = a.getU().getName();
                String v = a.getV().getName();
                double f = a.getFlow();
                double c = a.getCapacity();
                double w = a.getWeight();

                fw.write("\t\"" + u + "\" -> \"" + v + "\" [ label=\"" + f + "/" + c + "/" + w + "\", color=\"" + a.getColour() + "\" ];\r\n");
            }

            /* Ecrire les extensions pour le graph résiduel */
            if (withReverse) {
                for (Arc a : this.rA.values()) {
                    String u = a.getU().getName();
                    String v = a.getV().getName();
                    double f = a.getFlow();
                    double c = a.getCapacity();
                    double w = a.getWeight();

                    fw.write("\t\"" + u + "\" -> \"" + v + "\" [style = dashed, label=\"" + f + "/" + c + "/" + w + "\" ];\r\n");
                }
            }

            // Layout
            fw.write("\t{rank = source; source;}\r\n");
            fw.write("\t{rank = sink; sink;}\r\n");

            for (int rank : this.ranks.keySet()) {
                fw.write("\t{rank = same; ");
                for (Node n : this.ranks.get(rank)) {
                    fw.write("\"" + n.getName() + "\"" + "[group = g" + this.groups.get(n) + "]; ");
                }
                fw.write("}\r\n");
            }

            // END
            fw.write("}\r\n");

            /* Finalisation */
            fw.flush();
            fw.close();
        }
    }

    /**
     * Convert to PNG
     *
     * @param fileName
     * @param withReverse
     * @throws IOException
     */
    public void toPng(String fileName, boolean withReverse) throws IOException {

        /*to dot first*/
        String dir = ".";
        toDot(fileName, withReverse);
        String pngFile = dir + "/graphviz/" + fileName + ".png";

        Process p;
        p = new ProcessBuilder("dot", "-Tpng", dir + "/graphviz/" + fileName + ".dot", "-o", pngFile).start();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        String s = null;
        //System.out.println(">> Converting to png \n");
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        //System.out.println(">> Error of the command (if any):\n");
        while ((s = err.readLine()) != null) {
            System.out.println(s);
        }

        p.destroy();
    }

    public void layout_sameRank(int rank, LinkedList<Node> nds) {
        this.ranks.put(rank, nds);
    }

    public void layout_sameGroup(Node node, int group) {
        this.groups.put(node, group);
    }

    @Override
    public String toString() {
        return "Graph{" + "S=" + S + ", A=" + A + '}';
    }

    public static Graph fromFile(String fileName) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String ln;
        int itr = 0;
        Graph g = new Graph();

        int m = 0, n = 0;
        int i = 0, j = 0;
        int k = 0, l = 0;

        while ((ln = br.readLine()) != null) {
            if (ln.startsWith("#")) {
                continue;
            }

            if (ln.length() == 0) {
                itr++;
                continue;
            }

            StringTokenizer st = new StringTokenizer(ln);
            switch (itr) {
                // dim nxm
                case 0:
                    n = Integer.parseInt(st.nextToken());
                    m = Integer.parseInt(st.nextToken());
                    continue;
                // a_ij    
                case 1:
                    while (st.hasMoreTokens()) {
                        g.addArc("source", i + "," + j, Double.parseDouble(st.nextToken()));
                        j++;
                    }
                    j = 0;
                    i++;

                    if (i == n) {
                        i = 0;
                        j = 0;
                    }
                    continue;
                // b_ij    
                case 2:
                    while (st.hasMoreTokens()) {
                        g.addArc(i + "," + j, "sink", Double.parseDouble(st.nextToken()));
                        j++;
                    }
                    j = 0;
                    i++;
                    if (i == n) {
                        i = 0;
                        j = 0;
                    }
                    continue;

                // p_ijkl en ligne
                case 3:
                    while (st.hasMoreTokens()) {
                        k = i;
                        l = j + 1;
                        g.addArc(i + "," + j, k + "," + l, Double.parseDouble(st.nextToken()));
                        j++;
                    }
                    j = 0;
                    i++;
                    if (i == n) {
                        i = 0;
                        j = 0;
                    }
                    continue;

                // p_ijkl en ligne
                case 4:
                    while (st.hasMoreTokens()) {
                        k = i + 1;
                        l = j;
                        g.addArc(i + "," + j, k + "," + l, Double.parseDouble(st.nextToken()));
                        j++;
                    }
                    j = 0;
                    i++;
                    if (i == n) {
                        i = 0;
                        j = 0;
                    }
                    continue;
            }
            itr++;
        }

        for (i = 0; i < n; i++) {
            LinkedList<Node> nodes = new LinkedList<>();
            for (j = 0; j < m; j++) {
                nodes.add(g.getNode(i + "," + j));
                g.layout_sameGroup(g.getNode(i + "," + j), j);
            }
            g.layout_sameRank(i, nodes);

        }

        g.toPng("G", false);
        return g;
    }
}
