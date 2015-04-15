package com.boorce.clientscoiffmanager;


public class RendezVous {
    private long uid;
    private String cid;
    private String date;
    private String description;

    public long getUid() {
        return uid;
    }

    public void setUid(long id) {
        this.uid=id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid=cid;
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
