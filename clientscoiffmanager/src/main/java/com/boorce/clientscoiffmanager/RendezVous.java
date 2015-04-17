package com.boorce.clientscoiffmanager;


public class RendezVous {
    private long uid;
    private String cname;
    private String date;
    private String description;

    public long getUid() {
        return uid;
    }

    public void setUid(long id) {
        this.uid=id;
    }

    public String getCName() {
        return cname;
    }

    public void setCName(String cName) {
        this.cname=cName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date=date;
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
