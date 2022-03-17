package org.acme;

public class MoviePlayCount {
    public String name;
    public int count;

    public MoviePlayCount aggregate(String name) {
        this.name = name;
        this.count++;

        return this;
    }

    @Override
    public String toString() {
        return "MoviePlayCount [count=" + count + ", name=" + name + "]";
    }
    
}
