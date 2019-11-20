/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author minhhoangdang
 */
public class Graph {

    private final HashMap<String, Node> S;
    private final HashMap<String, Arc> A;
    private final HashMap<String, Arc> ext;

    public Graph() {
        this.A = new HashMap<>();
        this.S = new HashMap<>();
        this.ext = new HashMap<>();
    }

    public void addNode(Node n) {
        this.S.put(n.getName(), n);
    }

    public void addArc(Arc a) {
        Arc reverse = a.getReverse();

        this.A.put(a.getName(), a);
        this.ext.put(reverse.getName(), reverse);

        this.addNode(a.getU());
        this.addNode(a.getV());
    }

    public void addArc(String u, String v, int capacity, int flow) {
        Arc a = new Arc(u, v, capacity, flow);
        Arc reverse = a.getReverse();

        this.A.put(a.getName(), a);
        this.ext.put(reverse.getName(), reverse);

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
        res.addAll(ext.values());
        return res;
    }

    public Arc getArc(Node u, Node v) {
        Arc res = this.A.get(u + "-" + v);
        if (res == null) {
            return ext.get(u + "-" + v);
        }
        return res;
    }

    public Collection<Node> Adj(Node n) {
        List<Node> res = new ArrayList<>();

        this.A.values().stream().filter((a) -> (a.getU().equals(n))).forEachOrdered((a) -> {
            res.add(a.getV());
        });

        this.ext.values().stream().filter((a) -> (a.getU().equals(n))).forEachOrdered((a) -> {
            res.add(a.getV());
        });

        return res;
    }

    /**
     * Convert to Graphviz .dot file
     * <a href="https://martin-thoma.com/how-to-draw-a-finite-state-machine/">Source</a>
     *
     * @param arg
     * @throws java.io.IOException
     */
    public void toDot(String fileName) throws IOException {
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

            /* Ecrire les transitions */
            for (Arc a : this.A.values()) {
                String u = a.getU().getName();
                String v = a.getV().getName();
                double f = a.getFlow();
                double c = a.getCapacity();
                double w = a.getWeight();

                fw.write("\t" + u + " -> " + v + " [ label=\"" + f + "/" + c + "/" + w + "\" ];\r\n");
            }

            /* Ecrire les extensions pour le graph résiduel */
            /*for (Arc a : this.ext.values()) {
                String u = a.getU().getName();
                String v = a.getV().getName();
                double f = a.getFlow();
                double c = a.getCapacity();
                double w = a.getWeight();

                fw.write("\t" + u + " -> " + v + " [style = dashed, label=\"" + f + "/" + c + "/" + w + "\" ];\r\n");
            }*/
            fw.write("\t{rank = source; source;}\r\n");
            fw.write("\t{rank = sink; sink;}\r\n");

            fw.write("}\r\n");

            /* Finalisation */
            fw.flush();
            fw.close();
        }
    }

    /**
     * Convert to PNG
     *
     * @param arg
     * @throws IOException
     */
    public void toPng(String fileName) throws IOException {

        /*to dot first*/
        String dir = ".";
        toDot(fileName);
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

    @Override
    public String toString() {
        return "Graph{" + "S=" + S + ", A=" + A + '}';
    }
}
