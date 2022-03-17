package org.acme;

public class MoviePlayCountData {

    private String name;
    private int count;
    
    public MoviePlayCountData(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

}
