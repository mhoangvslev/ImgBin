/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author minhhoangdang
 */
public class Utils {

    private final Graph g;

    public Utils(Graph g) {
        this.g = g;
    }

    /**
     * DFS
     *
     * @param subgraph
     * @param start
     */
    private HashMap<Node, Boolean> visite_dfs(Node start) {

        HashMap<Node, Boolean> visited = new HashMap<>();
        for (Node n : g.getNodes()) {
            visited.put(n, false);
        }

        Stack<Node> S = new Stack<>();
        S.push(start);

        while (!S.isEmpty()) {
            Node v = S.pop();
            if (!visited.get(v)) {
                visited.put(v, Boolean.TRUE);
                for (Node w : g.Adj(v)) {
                    if (g.getArc(v, w).getRemaining() > 0 && !visited.get(w)) {
                        S.push(w);
                    }
                }
            }
        }

        return visited;
    }

    private LinkedList<Arc> visite_bfs(Node start) {
        LinkedList<Node> Q = new LinkedList<>();

        HashMap<Node, Node> parent = new HashMap<>();
        HashMap<Node, Boolean> visited = new HashMap<>();
        for (Node n : g.getNodes()) {
            visited.put(n, false);
        }

        visited.put(start, Boolean.TRUE);
        Q.add(start);

        while (!Q.isEmpty()) {
            Node v = Q.poll();
            for (Node w : g.Adj(v)) {
                if (g.getArc(v, w).getRemaining() > 0 && !visited.get(w)) {
                    visited.put(w, Boolean.TRUE);
                    parent.put(w, v);
                    Q.add(w);
                }
            }
        }

        LinkedList<Arc> path = new LinkedList<>();

        Node v = g.getNode("sink");
        while (!v.equals(g.getNode("source"))) {
            Node u = parent.get(v);
            path.addFirst(g.getArc(u, v));
            v = parent.get(v);
            if (v == null) {
                return null;
            }
        }

        return path;

    }

    /**
     *
     */
    private void init(HashMap<Node, Double> height, HashMap<Node, Double> exceedence) {
        Node source = g.getNode("source");
        // Set height and execcedence to 0
        for (Node s : g.getNodes()) {
            height.put(s, 0.);
            exceedence.put(s, 0.);
        }

        // Set height for source
        height.put(source, (double) g.getNodes().size());

        // set flow for each arc
        for (Arc a : g.getArcs()) {
            a.setFlow(0);
            g.getArc(a.getV(), a.getU()).setFlow(0);
        }

        // set flow and exceedence for each adjacent of source
        for (Node v : g.Adj(source)) {
            Arc uv = g.getArc(source, v);
            Arc vu = g.getArc(v, source);

            uv.setFlow(uv.getCapacity());
            vu.setFlow(-uv.getFlow());

            exceedence.put(v, uv.getCapacity());
            exceedence.put(source, exceedence.get(source) - uv.getCapacity());
        }
    }

    /**
     * Push
     *
     * @param u
     * @param v
     * @return
     */
    private void avancer(Node u, Node v, HashMap<Node, Double> exceedence) {
        Arc uv = g.getArc(u, v);

        double df = Double.min(exceedence.get(u), uv.getRemaining());

        uv.setFlow(uv.getFlow() + df);
        g.getArc(v, u).setFlow(-uv.getFlow());

        exceedence.put(u, exceedence.get(u) - df);
        exceedence.put(v, exceedence.get(v) + df);

        System.out.println("push " + uv);

    }

    /**
     * Relabel
     *
     * @param u
     * @return
     */
    private void elever(Node u, HashMap<Node, Double> height) {
        double minHeight = Double.MAX_VALUE;
        for (Node v : g.Adj(u)) {
            if (g.getArc(u, v).getRemaining() > 0) {
                minHeight = Double.min(minHeight, height.get(v));
                height.put(u, minHeight + 1);
                System.out.println("relabel " + u + "|" + minHeight);
            }
        }

    }

    private void discharge(Node u, Queue<Node> Q, HashMap<Node, Double> height, HashMap<Node, Double> exceedence) {
        while (exceedence.get(u) > 0) {
            for (Node v : g.Adj(u)) {
                //System.out.println(u + "|" + v + "|" + height.get(u) + "|" + height.get(v));
                if (g.getArc(u, v).getRemaining() > 0 && height.get(u) > height.get(v)) {
                    avancer(u, v, exceedence);
                    if (!v.getName().equals("source") && !v.getName().equals("sink") && !Q.contains(v)) {
                        Q.add(v);
                    }
                }
            }
            if (exceedence.get(u) > 0) {
                elever(u, height);
            }
        }

    }

    /**
     *
     */
    public void preflow() throws IOException {

        HashSet<Node> fg = new HashSet<>();
        HashSet<Node> bg = new HashSet<>();
        HashSet<Arc> minCut = new HashSet<>();

        HashMap<Node, Double> height = new HashMap<>();
        HashMap<Node, Double> exceedence = new HashMap<>();

        init(height, exceedence);
        double maxFlow = 0.;
        int itr = 0;

        Node source = g.getNode("source");
        LinkedList<Node> Q = new LinkedList<>();
        Q.addAll(g.Adj(source));

        while (!Q.isEmpty()) {
            System.out.println(Q);
            g.toPng("preflow-" + itr);
            itr++;
            Node u = Q.pop();
            discharge(u, Q, height, exceedence);
        }

        // Find maxFlow
        for (Node n : g.getNodes()) {
            Arc a = g.getArc(source, n);
            if (a != null) {
                maxFlow += g.getArc(source, n).getFlow();
            }
        }

        //Find the min cut
        HashMap<Node, Boolean> dfs = visite_dfs(source);
        for (Arc a : g.getArcs()) {
            if (a.getRemaining() > 0 && dfs.get(a.getU()) && !dfs.get(a.getV())) {
                fg.add(a.getU());
                bg.add(a.getV());
                minCut.add(a);
            }
        }

        System.out.println("Got " + fg.size() + " in foreground, " + bg.size() + " in background.");
        System.out.println("Min cut is: " + minCut);
        System.out.println("Max-flow is: " + maxFlow);
    }

    public void ford_fulkerson() {
        try {
            // init
            for (Arc a : g.getArcs()) {
                a.setFlow(0);
            }

            double maxFlow = 0;
            int itr = 0;

            g.toPng("ford-fulkerson" + itr);

            Collection<Arc> p;
            //while ((p = pth_bellman_ford(g.getNode("source"), g.getNode("sink"), true)) != null) {
            while ((p = visite_bfs(g.getNode("source"))) != null) {
                itr++;

                ArrayList<Double> cf_arr = new ArrayList<>();
                for (Arc a : p) {
                    cf_arr.add(a.getRemaining());
                }
                double cf = Collections.min((cf_arr));

                System.out.println(p);

                for (Arc a : p) {
                    a.setFlow(a.getFlow() + cf);
                    g.getArc(a.getV(), a.getU()).setFlow(-a.getFlow());
                }

                maxFlow += cf;
                g.toPng("ford-fulkerson" + itr);
            }

            System.out.println("Max-flow is: " + maxFlow);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param src
     * @param dest
     * @param returnPath if true, return path else return cycle
     * @return
     */
    public LinkedList<Arc> pth_bellman_ford(Node src, Node dest, boolean returnPath) {
        // init
        HashMap<Node, Double> dist = new HashMap<>();
        HashMap<Node, Node> predecessor = new HashMap<>();

        LinkedList<Arc> cycle = new LinkedList<>();

        for (Node n : g.getNodes()) {
            dist.put(n, Double.MAX_VALUE);
        }
        dist.put(src, 0.);

        // relax 
        for (int i = 0; i < g.getArcs().size() - 1; i++) {
            for (Arc a : g.getArcs()) {

                if (dist.get(a.getU()) != Double.MAX_VALUE && dist.get(a.getU()) + a.getWeight() < dist.get(a.getV())) {
                    dist.put(a.getV(), dist.get(a.getU()) + a.getWeight());
                    predecessor.put(a.getV(), a.getU());
                }
            }
        }

        // cycle
        for (Arc a : g.getArcs()) {
            if (dist.get(a.getU()) != Double.MAX_VALUE && dist.get(a.getU()) + a.getWeight() < dist.get(a.getV())) {
                // negative weight cycle
                cycle.add(a);
            }
        }

        // Shortest path src to dest
        Node current, pred = dest;
        LinkedList<Arc> p = new LinkedList<>();
        while ((current = pred) != null && (pred = predecessor.get(current)) != null) {
            p.addFirst(g.getArc(pred, current));
        }

        return returnPath ? p : cycle;
    }
}
