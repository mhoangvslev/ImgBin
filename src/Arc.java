/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Objects;

/**
 *
 * @author minhhoangdang
 */
public class Arc {

    private final Node u;
    private final Node v;

    private int capacity;
    private int flow;
    private int weight;
    private String colour;

    public Arc(Node u, Node v, int capacity, int flow, int weight) {
        this.u = u;
        this.v = v;
        this.capacity = capacity;
        this.flow = flow;
        this.weight = weight;
        this.colour = "black";
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRemaining() {
        return capacity - flow;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public Node getU() {
        return u;
    }

    public Node getV() {
        return v;
    }

    public Arc getReverse() {
        return new Arc(v, u, 0, -flow, -weight);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getName() {
        return u + "-" + v;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
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
