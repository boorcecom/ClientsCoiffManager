package com.boorce.clientscoiffmanager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class PhotoDataSource {
    private SQLiteDatabase db;
    private CCMSQLiteHelper dbHelper;

    private String[] allColumns = { CCMSQLiteHelper.PHO_COLUMN_UID,
            CCMSQLiteHelper.PHO_COLUMN_RID, CCMSQLiteHelper.PHO_COLUMN_FILE,
            CCMSQLiteHelper.PHO_COLUMN_THUMBNAIL};

    public PhotoDataSource(Context ctx) {
        dbHelper=new CCMSQLiteHelper(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public Photo createPhoto(long rdvId, String filename, byte[] thumbnail) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.PHO_COLUMN_RID, rdvId);
        values.put(CCMSQLiteHelper.PHO_COLUMN_FILE, filename);
        values.put(CCMSQLiteHelper.PHO_COLUMN_THUMBNAIL, thumbnail);
        long insertId=db.insert(CCMSQLiteHelper.TABLE_PHOTOS, null,values);
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_PHOTOS,
                allColumns, CCMSQLiteHelper.PHO_COLUMN_UID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Photo photo = cursorToPhoto(cursor);
        cursor.close();
        CCMSQLiteHelper.setLastModified(db);
        return photo;
    }

    public Photo createPhoto(long rdvId) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.PHO_COLUMN_RID, rdvId);
        long insertId=db.insert(CCMSQLiteHelper.TABLE_PHOTOS, null,values);
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_PHOTOS,
                allColumns, CCMSQLiteHelper.PHO_COLUMN_UID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Photo photo = cursorToPhoto(cursor);
        cursor.close();
        CCMSQLiteHelper.setLastModified(db);
        return photo;
    }


    public void deletePhoto(Photo photo) {
        deletePhoto(photo.getUid());
    }

    public void deletePhoto(long phId) {
        db.delete(CCMSQLiteHelper.TABLE_PHOTOS, CCMSQLiteHelper.PHO_COLUMN_UID
                + " = " + phId , null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public void deletePhotosFromRdv(long rdvId) {
        db.delete(CCMSQLiteHelper.TABLE_PHOTOS, CCMSQLiteHelper.PHO_COLUMN_RID
                + " = " + rdvId , null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public void deleteAllPhoto() {
        db.delete(CCMSQLiteHelper.TABLE_PHOTOS,null,null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public List<Photo> getAllPhoto() {
        List<Photo> allPhoto = new ArrayList<Photo>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_PHOTOS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo unePhoto = cursorToPhoto(cursor);
            allPhoto.add(unePhoto);
            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return allPhoto;
    }

    public List<Photo> getPhotosFromRid(Long getRid) {
        List<Photo> somePhotos = new ArrayList<Photo>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_PHOTOS, allColumns,
                CCMSQLiteHelper.PHO_COLUMN_RID+"="+getRid,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo unePhoto = cursorToPhoto(cursor);
            somePhotos.add(unePhoto);
            cursor.moveToNext();
        }
        cursor.close();
        return somePhotos;
    }


    public Photo getPhotoFromUid(Long uId) {
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_PHOTOS, allColumns,
                CCMSQLiteHelper.PHO_COLUMN_UID+"="+uId ,null,null,null,null);
        cursor.moveToFirst();
        Photo photo = cursorToPhoto(cursor);
        cursor.close();
        return photo;
    }

    private Photo cursorToPhoto(Cursor cursor) {
        Photo photo = new Photo();
        photo.setUid(cursor.getLong(0));
        photo.setRid(cursor.getLong(1));
        photo.setFilename(cursor.getString(2));
        photo.setThumbnail(cursor.getBlob(3));
        return photo;
    }

    // Gestion des timestamps
    public Long getLastModified() {
        return CCMSQLiteHelper.getLastModified(db);
    }

}
