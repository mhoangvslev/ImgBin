/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

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
    
    public Utils(Graph g) {
        this.g = g;
    }
    
    /**
     * Find an available path from start to end. A path is possible when the residual capacity along that path is available
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
     * Push a certain amount down an arc and update the the reversed arc in residual graph
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
     * @param u node from
     * @param v node to
     * @param exceedence 
     */
    private void avancer(Node u, Node v, HashMap<Node, Integer> exceedence) {
        Arc uv = g.getArc(u, v);
        
        int df = Integer.min(exceedence.get(u), uv.getRemaining());
        
        updateFlow(uv, uv.getFlow() + df);
        
        exceedence.put(u, exceedence.get(u) - df);
        exceedence.put(v, exceedence.get(v) + df);
    }

    /**
     * preflow - Relabel
     * @param u
     * @return
     */
    private void elever(Node u, HashMap<Node, Integer> height) {
        int minHeight = Integer.MAX_VALUE;
        for (Node v : g.Adj(u)) {
            if (g.getArc(u, v).getRemaining() > 0) {
                minHeight = Integer.min(minHeight, height.get(v));
                height.put(u, minHeight + 1);
            }
        }
        
    }

    /**
     * Apply push or relabel when possible.
     * @param u
     * @param Q
     * @param height
     * @param exceedence
     */
    private void discharge(Node u, Queue<Node> Q, HashMap<Node, Integer> height, HashMap<Node, Integer> exceedence) {
        while (exceedence.get(u) > 0) {
            for (Node v : g.Adj(u)) {
                Arc a = g.getArc(u, v);
                a.setColour("green");
                a.getU().setColour("green");
                a.getV().setColour("green");
                if (a.getRemaining() > 0 && height.get(u) == height.get(v) + 1) {
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
     * @param method pathfinding option
     * @param withReport draw graphviz report for each iteration
     * @throws IOException 
     */
    public void ford_fulkerson(PathFindingAlgorithm method, boolean withReport) throws IOException {
        // init
        int maxFlow = 0;
        int itr = 0;
        
        for (Arc a : g.getArcs()) {
            a.setFlow(0);
            a.setColour("black");
        }
        
        if (withReport) {
            g.toPng("ford-fulkerson-" + itr, false);
        }
        
        Node source = g.getNode("source");
        
        Collection<Arc> p;
        while ((p = pth_findPath(method)) != null) {
            itr++;
            
            int cf = Integer.MAX_VALUE;
            for (Arc a : p) {
                cf = Integer.min(cf, a.getRemaining());
            }
            
            for (Arc a : p) {
                a.setColour("green");
                a.getU().setColour("green");
                a.getV().setColour("green");
                
                updateFlow(a, a.getFlow() + cf);
            }
            
            if (withReport) {
                g.toPng("ford-fulkerson" + itr, false);
            }
        }

        // Find maxFlow
        for (Node n : g.Adj(source)) {
            maxFlow += g.getArc(source, n).getFlow();
        }
        
        System.out.println("Max-flow is: " + maxFlow);
        
    }
    
    /**
     * Apply a pathfinding algorithm
     * @param method
     * @return 
     */
    private LinkedList<Arc> pth_findPath(PathFindingAlgorithm method) {
        switch (method) {
            case BELLMAN_FORD:
                return pth_bellman_ford(g.getNode("source"), g.getNode("sink"));
            case BFS:
            default:
                return pth_bfs(g.getNode("source"), g.getNode("sink"));
        }
    }

    /**
     * Shortest path using bellman_ford
     * @param src
     * @param dest
     * @return
     */
    private LinkedList<Arc> pth_bellman_ford(Node src, Node dest) {
        // init
        HashMap<Node, Integer> dist = new HashMap<>();
        HashMap<Node, Node> parent = new HashMap<>();
        
        for (Node n : g.getNodes()) {
            dist.put(n, Integer.MAX_VALUE);
        }
        dist.put(src, 0);

        // relax 
        for (int i = 0; i < g.getArcs().size() - 1; i++) {
            for (Arc a : g.getArcs()) {
                
                if (dist.get(a.getU()) != Integer.MAX_VALUE && dist.get(a.getU()) + a.getWeight() < dist.get(a.getV())) {
                    dist.put(a.getV(), dist.get(a.getU()) + a.getWeight());
                    parent.put(a.getV(), a.getU());
                }
            }
        }

        // cycle
        for (Arc a : g.getArcs()) {
            if (dist.get(a.getU()) != Integer.MAX_VALUE && dist.get(a.getU()) + a.getWeight() < dist.get(a.getV())) {
                // negative weight cycle
                //cycle.add(a);
                return null;
            }
        }

        // Shortest path src to dest
        Node current, pred = dest;
        LinkedList<Arc> p = new LinkedList<>();
        while ((current = pred) != null && (pred = parent.get(current)) != null) {
            p.addFirst(g.getArc(pred, current));
        }
        
        return p;
    }

    /**
     * DFS. Mark arcs that are not saturated
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

    /**
     * Find the minimum cut
     * @param method
     * @param withReport
     * @return
     * @throws IOException
     */
    public HashSet<Arc> minCut(MaxFlowAlgorithm method, boolean withReport) throws IOException {
        
        switch (method) {
            case FORD_FULKERSON_BELLMAN_FORD:
                this.ford_fulkerson(PathFindingAlgorithm.BELLMAN_FORD, withReport);
                break;
            case FORD_FULKERSON_BFS:
            default:
                this.ford_fulkerson(PathFindingAlgorithm.BFS, withReport);
                break;
            case PREFLOW:
                this.preflow(withReport);
                break;
        }

        // Min-Cut
        System.out.println("Min-cut is: ");
        
        HashSet<Arc> cuts = new HashSet<>();
        HashSet<Node> sSet = new HashSet();
        HashSet<Node> tSet = new HashSet();
        
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
    
}
