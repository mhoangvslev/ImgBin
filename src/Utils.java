/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author minhhoangdang
 */
public class Utils {

    private final Graph g;
    private HashSet<Node> tSet;
    private HashSet<Node> sSet;

    public Utils(Graph g) {
        this.g = g;
    }

    /**
     * Find an available path from start to end. A path is possible when the
     * residual capacity along that path is available
     *
     * @param start
     * @return
     */
    private LinkedList<Arc> pth_bfs(Node start, Node end) {
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

            if (v.equals(end)) {
                break;
            }

            for (Node w : g.Adj(v)) {
                if (g.getArc(v, w).getRemaining() > 0 && !visited.get(w)) {
                    visited.put(w, Boolean.TRUE);
                    parent.put(w, v);
                    Q.add(w);
                }
            }
        }

        LinkedList<Arc> path = new LinkedList<>();

        Node v = end;
        while (v != null && !v.equals(g.getNode("source"))) {
            Node u = parent.get(v);
            path.addFirst(g.getArc(u, v));
            v = u;
        }

        return v == null ? null : path;

    }

    /**
     * Push a certain amount down an arc and update the the reversed arc in
     * residual graph
     *
     * @param a
     * @param flow
     */
    private void updateFlow(Arc a, int flow) {
        a.setFlow(flow);

        Arc r = g.getReversedArc(a);
        if (r != null) {
            r.setFlow(-a.getFlow());
        }
    }

    /**
     * Preflow-init
     */
    private void init(HashMap<Node, Integer> height, HashMap<Node, Integer> exceedence) {
        Node source = g.getNode("source");
        // Set height and execcedence to 0
        for (Node s : g.getNodes()) {
            height.put(s, 0);
            exceedence.put(s, 0);
            s.setColour("lightgrey");
        }

        // Set height for source
        height.put(source, (int) g.getNodes().size());

        // set flow for each arc
        for (Arc a : g.getArcs()) {
            updateFlow(a, 0);
            a.setColour("black");
        }

        // set flow and exceedence for each adjacent of source
        for (Node v : g.Adj(source)) {
            Arc uv = g.getArc(source, v);
            updateFlow(uv, uv.getCapacity());

            exceedence.put(v, uv.getCapacity());
            exceedence.put(source, exceedence.get(source) - uv.getCapacity());
        }
    }

    /**
     * preflow push
     *
     * @param u node from
     * @param v node to
     * @param exceedence
     */
    private void push(Node u, Node v, HashMap<Node, Integer> exceedence) {
        Arc uv = g.getArc(u, v);
        uv.setColour("green");
        u.setColour("green");
        v.setColour("green");

        int df = Math.min(exceedence.get(u), uv.getRemaining());

        updateFlow(uv, uv.getFlow() + df);

        exceedence.put(u, exceedence.get(u) - df);
        exceedence.put(v, exceedence.get(v) + df);
    }

    /**
     * preflow - Relabel
     *
     * @param u
     * @return
     */
    private void relabel(Node u, HashMap<Node, Integer> height) {
        u.setColour("blue");
        int minHeight = Integer.MAX_VALUE;
        for (Node v : g.Adj(u)) {
            if (g.getArc(u, v).getRemaining() > 0) {
                v.setColour("yellow");
                minHeight = Math.min(minHeight, height.get(v));
                height.put(u, minHeight + 1);
            }
        }

    }

    /**
     * Apply push or relabel when possible.
     *
     * @param u
     * @param Q
     * @param height
     * @param exceedence
     */
    private void discharge(Node u, Queue<Node> Q, HashMap<Node, Integer> height, HashMap<Node, Integer> exceedence) {
        while (exceedence.get(u) > 0) {
            for (Node v : g.Adj(u)) {
                Arc a = g.getArc(u, v);
                if (a.getRemaining() > 0 && height.get(u) == height.get(v) + 1) {
                    push(u, v, exceedence);
                    if (!v.getName().equals("source") && !v.getName().equals("sink") && !Q.contains(v)) {
                        Q.add(v);
                    }
                }
            }
            if (exceedence.get(u) > 0) {
                relabel(u, height);
            }
        }

    }

    /**
     * Push-relabel algorithm
     *
     * @param withReport
     * @throws java.io.IOException
     */
    public void preflow(boolean withReport) throws IOException {
        int maxFlow = 0;
        int itr = 0;

        HashMap<Node, Integer> height = new HashMap<>();
        HashMap<Node, Integer> exceedence = new HashMap<>();

        init(height, exceedence);

        if (withReport) {
            g.toPng("preflow-" + itr, false);
        }

        Node source = g.getNode("source");
        LinkedList<Node> Q = new LinkedList<>();
        Q.addAll(g.Adj(source));

        while (!Q.isEmpty()) {
            itr++;
            Node u = Q.pop();
            discharge(u, Q, height, exceedence);

            if (withReport) {
                g.toPng("preflow-" + itr, false);
            }
        }

        // Find maxFlow
        for (Node n : g.Adj(source)) {
            maxFlow += g.getArc(source, n).getFlow();
        }

        System.out.println("Max-flow is: " + maxFlow);
    }

    /**
     * Max-flow using ford_fulkerson algorithm
     *
     * @param method pathfinding option
     * @param withReport draw graphviz report for each iteration
     * @throws IOException
     */
    public void ford_fulkerson(boolean withReport) throws IOException {
        // init
        int maxFlow = 0;
        int itr = 0;

        for (Arc a : g.getArcs()) {
            a.setFlow(0);
            a.setColour("black");
            a.getU().setColour("lightgrey");
            a.getV().setColour("lightgrey");

        }

        if (withReport) {
            g.toPng("ford-fulkerson-" + itr, false);
        }

        Node source = g.getNode("source");

        Collection<Arc> p;
        while ((p = pth_bfs(g.getNode("source"), g.getNode("sink"))) != null) {
            itr++;

            int cf = Integer.MAX_VALUE;
            for (Arc a : p) {
                cf = Math.min(cf, a.getRemaining());
            }

            for (Arc a : p) {
                a.setColour("green");
                a.getU().setColour("green");
                a.getV().setColour("green");

                updateFlow(a, a.getFlow() + cf);
            }

            if (withReport) {
                g.toPng("ford-fulkerson-" + itr, false);
            }
        }

        // Find maxFlow
        for (Node n : g.Adj(source)) {
            maxFlow += g.getArc(source, n).getFlow();
        }

        System.out.println("Max-flow is: " + maxFlow);

    }

    /**
     * DFS. Mark arcs that are not saturated
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

    private void CalculFlotMax(MaxFlowAlgorithm method, boolean withReport) throws IOException {
        switch (method) {
            case PREFLOW:
                this.preflow(withReport);
                break;
            default:
                this.ford_fulkerson(withReport);
                break;
        }
    }

    private HashSet<Arc> CalculCoupeMin(boolean withReport) throws IOException {
        System.out.println("Min-cut is: ");

        HashSet<Arc> cuts = new HashSet<>();
        sSet = new HashSet<>();
        tSet = new HashSet<>();

        String sSetClr = "orange";
        String tSetClr = "lightblue";

        Node source = g.getNode("source");
        sSet.add(source);
        source.setColour(sSetClr);

        Node sink = g.getNode("sink");
        tSet.add(sink);
        sink.setColour(tSetClr);

        HashMap<Node, Boolean> visited = visite_dfs(source);

        for (Arc a : g.getArcs()) {
            Node u = a.getU();
            Node v = a.getV();
            if (a.getRemaining() == 0 && visited.get(u) && !visited.get(v)) {
                a.setColour("red");
                cuts.add(a);

                tSet.add(v);
                g.getNode(v.getName()).setColour(tSetClr);

                sSet.add(u);
                g.getNode(u.getName()).setColour(sSetClr);

                System.out.println(a);
            }
        }

        System.out.println("source-set: " + sSet);
        System.out.println("sink-set: " + tSet);

        if (withReport) {
            g.toPng("MinCut", false);
        }

        return cuts;
    }

    public HashSet<Node> gettSet() {
        return tSet;
    }

    public HashSet<Node> getsSet() {
        return sSet;
    }

     /**
     * Find the minimum cut
     *
     * @param method
     * @param withReport
     * @throws IOException
     */
    public void ResoudreBinIm(MaxFlowAlgorithm method, boolean withReport) throws IOException {
        // maxflow
        CalculFlotMax(method, withReport);

        // Min-Cut
        CalculCoupeMin(withReport);

        // Affichage
        System.out.println("Final result: ");
        for (int i : g.layout_getRanks()) {
            for (Node n : g.layout_getNodesFromRank(i)) {
                if (sSet.contains(n)) {
                    System.out.print("A");
                } else {
                    System.out.print("B");
                }
            }
            System.out.print("\n");
        }

    }

}
