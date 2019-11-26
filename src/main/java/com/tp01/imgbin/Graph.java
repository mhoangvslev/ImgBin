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

    /**
     * Graph name
     */
    private final String name;

    /**
     * List of nodes
     */
    private final HashMap<String, Node> S;
    
    /**
     * List of arcs
     */
    private final HashMap<String, Arc> A;
    
    /**
     * List of reversed arcs in A for residual graph
     */
    private final HashMap<String, Arc> rA;

    /**
     * GraphViz layout
     */
    private final HashMap<Integer, LinkedList<Node>> ranks;
    private final HashMap<Node, Integer> groups;

    /**
     * Constructor
     * @param name graph name
     */
    public Graph(String name) {
        this.name = name;
        this.A = new HashMap<>();
        this.S = new HashMap<>();
        this.rA = new HashMap<>();
        this.groups = new HashMap<>();
        this.ranks = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    private void addNode(Node n) {
        this.S.put(n.getName(), n);
    }

    /**
     * Add an arc from node u to v. The nodes will be created if they are not in S
     * @param u u node name
     * @param v v node name
     * @param capacity arc capacity
     */
    public void addArc(String u, String v, int capacity) {

        Node from = this.getNode(u) != null ? this.getNode(u) : new Node(u);
        Node to = this.getNode(v) != null ? this.getNode(v) : new Node(v);

        Arc a = new Arc(from, to, capacity, 0, 0);

        this.A.put(a.getName(), a);

        Arc reverse = a.getReverse();
        this.rA.put(reverse.getName(), reverse);

        this.addNode(from);
        this.addNode(to);
    }

    /**
     * Add an arc from node u to v. The nodes will be created if they are not in S
     * @param u u node name
     * @param v v node name
     * @param capacity arc capacity
     * @param flow arc flow
     */
    public void addArc(String u, String v, int capacity, int flow) {
        Node from = this.getNode(u) != null ? this.getNode(u) : new Node(u);
        Node to = this.getNode(v) != null ? this.getNode(v) : new Node(v);

        Arc a = new Arc(from, to, capacity, flow, 0);
        this.A.put(a.getName(), a);

        Arc reverse = a.getReverse();
        this.rA.put(reverse.getName(), reverse);

        this.addNode(from);
        this.addNode(to);
    }

    /**
     * Retrieve node by name
     * @param name node name
     * @return 
     */
    public Node getNode(String name) {
        return this.S.get(name);
    }

    /**
     * Retrieve all nodes in S
     * @return 
     */
    public Collection<Node> getNodes() {
        return S.values();
    }

    /**
     * Retrieve all nodes in residual graph (A + rA)
     * @return 
     */
    public Collection<Arc> getArcs() {
        HashSet<Arc> res = new HashSet<>();
        res.addAll(A.values());
        res.addAll(rA.values());
        return res;
    }

    /**
     * Retrieve an arc in residual graph
     * @param u from node
     * @param v to node
     * @return 
     */
    public Arc getArc(Node u, Node v) {
        Arc res = this.A.get(u + "-" + v);
        return (res != null) ? res : this.rA.get(u + "-" + v);
    }

    /**
     * Retrieve the reversed arc in the residual graph
     * @param a an arc
     * @return 
     */
    public Arc getReversedArc(Arc a) {
        return this.getArc(a.getV(), a.getU());
    }

    /**
     * Get children nodes of a node
     * @param n a parent node
     * @return 
     */
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
        String dotFile = dir + "/graphviz/" + this.name + "_" + fileName + ".dot";
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
                fw.write("\t\"" + n + "\" [ style=\"filled\", fillcolor=\"" + n.getColour() + "\" ];\r\n");
            }

            /* Ecrire les arc */
            for (Arc a : this.A.values()) {
                String u = a.getU().getName();
                String v = a.getV().getName();
                int f = a.getFlow();
                int c = a.getCapacity();
                int w = a.getWeight();

                fw.write("\t\"" + u + "\" -> \"" + v + "\" [ label=\"" + f + "/" + c + "/" + w + "\", color=\"" + a.getColour() + "\" ];\r\n");
            }

            /* Ecrire les extensions pour le graph résiduel */
            if (withReverse) {
                for (Arc a : this.rA.values()) {
                    String u = a.getU().getName();
                    String v = a.getV().getName();
                    int f = a.getFlow();
                    int c = a.getCapacity();
                    int w = a.getWeight();

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
        String pngFile = dir + "/graphviz/" + this.name + "_" + fileName + ".png";

        Process p;
        p = new ProcessBuilder("dot", "-Tpng", dir + "/graphviz/" + this.name + "_" + fileName + ".dot", "-o", pngFile).start();
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
        Graph g = new Graph("file");

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
                        g.addArc("source", i + "," + j, Integer.parseInt(st.nextToken()));
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
                        g.addArc(i + "," + j, "sink", Integer.parseInt(st.nextToken()));
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
                        int capacity = Integer.parseInt(st.nextToken());
                        if (capacity > 0) {
                            g.addArc(i + "," + j, k + "," + l, capacity);
                        }
                        j++;
                    }
                    j = 0;
                    i++;
                    if (i == n) {
                        i = 0;
                        j = 0;
                    }
                    continue;

                // p_ijkl en colonne
                case 4:
                    while (st.hasMoreTokens()) {
                        k = i + 1;
                        l = j;
                        int capacity = Integer.parseInt(st.nextToken());
                        if (capacity > 0) {
                            g.addArc(i + "," + j, k + "," + l, capacity);
                        }
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
