/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tp01.imgbin;

import java.util.Objects;

/**
 *
 * @author minhhoangdang
 */
public class Arc {

    private final Node u;
    private final Node v;

    private double capacity;
    private double flow;
    private double weight;

    public Arc(String u, String v, double capacity, double flow) {
        this.u = new Node(u);
        this.v = new Node(v);
        this.capacity = capacity;
        this.flow = flow;
        this.weight = 0;
    }

    public Arc(String u, String v, double capacity, double flow, double weight) {
        this.u = new Node(u);
        this.v = new Node(v);
        this.capacity = capacity;
        this.flow = flow;
        this.weight = weight;
    }

    public Arc(String u, String v, double capacity) {
        this.u = new Node(u);
        this.v = new Node(v);
        this.capacity = capacity;
        this.flow = 0;
        this.weight = 0;
    }
    
    public Arc(Node u, Node v, double capacity, double flow, double weight) {
        this.u = u;
        this.v = v;
        this.capacity = capacity;
        this.flow = flow;
        this.weight = weight;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getRemaining() {
        return capacity - flow;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public Node getU() {
        return u;
    }

    public Node getV() {
        return v;
    }

    public Arc getReverse() {
        return new Arc(v, u, capacity, -flow, weight);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return u + "-" + v;
    }

    @Override
    public String toString() {
        return "Arc{" + "u=" + u + ", v=" + v + ", capacity=" + capacity + ", flow=" + flow + ", weight=" + weight + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.u);
        hash = 97 * hash + Objects.hashCode(this.v);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Arc other = (Arc) obj;
        if (!Objects.equals(this.u, other.u)) {
            return false;
        }
        if (!Objects.equals(this.v, other.v)) {
            return false;
        }
        return true;
    }

}
