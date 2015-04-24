package com.boorce.clientscoiffmanager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Photo {

    // ajout d'un uid sur RdvTrv
    private long uid;
    private long rid;
    private String filename;
    private byte[] thumbnail;

    public long getUid() { return uid; }
    public void setUid(long uid) {this.uid=uid; }

    public long getRid() { return rid; }
    public void setRid(long rid) {this.rid=rid; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) {this.filename=filename; }

    public byte[] getThumbnail() { return thumbnail; }
    public Bitmap getBitmapThumbnail() { return BitmapFactory.decodeByteArray(this.thumbnail, 0, this.thumbnail.length); }
    public void setThumbnail(byte[] thumbnail) { this.thumbnail=thumbnail; }
    public void setThumbnail(Bitmap thumbnail) {
        ByteArrayOutputStream blob= new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG,0,blob);
        this.thumbnail=blob.toByteArray();
    }

}
