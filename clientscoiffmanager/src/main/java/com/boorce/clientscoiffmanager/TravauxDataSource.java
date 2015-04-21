package com.boorce.clientscoiffmanager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TravauxDataSource {

    private SQLiteDatabase db;
    private CCMSQLiteHelper dbHelper;

    private String[] allColumns = { CCMSQLiteHelper.TRV_COLUMN_ID,
            CCMSQLiteHelper.TRV_COLUMN_DESC };

    public TravauxDataSource(Context ctx) {
        dbHelper=new CCMSQLiteHelper(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public Travaux createTravaux(String description) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.TRV_COLUMN_DESC, description);
        long insertId = db.insert(CCMSQLiteHelper.TABLE_TRAVAUX, null,
                values);
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_TRAVAUX,
                allColumns, CCMSQLiteHelper.TRV_COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Travaux newTravail = cursorToTravaux(cursor);
        cursor.close();
        CCMSQLiteHelper.setLastModified(db);
        return newTravail;
    }

    // Ajout pour le backup et restore.
    public void createTravaux(Travaux travail) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.TRV_COLUMN_ID, travail.getId());
        values.put(CCMSQLiteHelper.TRV_COLUMN_DESC, travail.getDescription());
        db.insert(CCMSQLiteHelper.TABLE_TRAVAUX, null,
                values);
        CCMSQLiteHelper.setLastModified(db);
    }

    public void deleteTravail(Travaux travail) {
        long id = travail.getId();
        db.delete(CCMSQLiteHelper.TABLE_TRAVAUX, CCMSQLiteHelper.TRV_COLUMN_ID
                + " = " + id, null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public void deleteAllTravaux() {
        db.delete(CCMSQLiteHelper.TABLE_TRAVAUX,null,null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public void updateTravaux(Travaux travail) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.TRV_COLUMN_ID,travail.getId());
        values.put(CCMSQLiteHelper.TRV_COLUMN_DESC, travail.getDescription());
        db.update(CCMSQLiteHelper.TABLE_TRAVAUX,values,CCMSQLiteHelper.TRV_COLUMN_ID+"="+travail.getId(),null);
        CCMSQLiteHelper.setLastModified(db);
    }

    public List<Travaux> getAllTravaux() {
        List<Travaux> travaux = new ArrayList<Travaux>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_TRAVAUX,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Travaux travail = cursorToTravaux(cursor);
            travaux.add(travail);
            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return travaux;
    }

    public Travaux getTravailFromId(Long id) {
       Cursor cursor = db.query(CCMSQLiteHelper.TABLE_TRAVAUX, allColumns,CCMSQLiteHelper.TRV_COLUMN_ID+"="+id,null,null,null,null);
       if(cursor.getCount()==0) {
           return null;
       }
       cursor.moveToFirst();
       Travaux travail=cursorToTravaux(cursor);
       cursor.close();
       return travail;
    }

    private Travaux cursorToTravaux(Cursor cursor) {
        Travaux travail = new Travaux();
        travail.setId(cursor.getLong(0));
        travail.setDescription(cursor.getString(1));
        return travail;
    }

    // Gestion des timestamps
    public Long getLastModified() {
        return CCMSQLiteHelper.getLastModified(db);
    }

}
