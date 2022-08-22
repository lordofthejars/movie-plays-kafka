package org.acme;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Movie extends PanacheEntity {
    
    // No worries Quarkus will change them 
    // to private and auto-generate getters/setters at compilation time
    public String name;
    public String director;
    public String genre;

}
