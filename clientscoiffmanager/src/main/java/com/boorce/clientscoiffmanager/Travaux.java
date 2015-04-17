package com.boorce.clientscoiffmanager;


public class Travaux {
    private long id;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id=id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    @Override
    public String toString() {
        return description;
    }
}
