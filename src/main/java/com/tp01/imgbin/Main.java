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
        // Open file
        // Create nodes and arcs then from each entry then load into graph
        Graph g1 = new Graph();
        g1.addArc(new Arc("source", "1", 16));
        g1.addArc(new Arc("source", "2", 13));
        g1.addArc(new Arc("1", "2", 10));
        //g1.addArc(new Arc("2", "1", 4));
        g1.addArc(new Arc("1", "3", 12));
        g1.addArc(new Arc("3", "2", 9));
        g1.addArc(new Arc("2", "4", 14));
        g1.addArc(new Arc("4", "3", 7));
        g1.addArc(new Arc("3", "sink", 20));
        g1.addArc(new Arc("4", "sink", 4));
        System.out.println(g1);

        // Run preflow
        Utils utils1 = new Utils(g1);
        utils1.preflow();
        utils1.ford_fulkerson();

    }
}
