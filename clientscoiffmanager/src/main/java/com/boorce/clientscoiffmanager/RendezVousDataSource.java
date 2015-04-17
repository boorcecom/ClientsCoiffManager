package com.boorce.clientscoiffmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RendezVousDataSource {

    private TravauxDataSource Tds;

    private SQLiteDatabase db;
    private CCMSQLiteHelper dbHelper;
    private String[] allColumns = { CCMSQLiteHelper.RDV_COLUMN_UID, CCMSQLiteHelper.RDV_COLUMN_CID,
            CCMSQLiteHelper.RDV_COLUMN_DATE, CCMSQLiteHelper.RDV_COLUMN_DESC };

    public RendezVousDataSource(Context ctx) {
        dbHelper=new CCMSQLiteHelper(ctx);
        Tds = new TravauxDataSource(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public RendezVous createRendezVous(String cid, String Date, String description) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.RDV_COLUMN_CID,cid);
        values.put(CCMSQLiteHelper.RDV_COLUMN_DATE,Date);
        values.put(CCMSQLiteHelper.RDV_COLUMN_DESC, description);
        long insertId = db.insert(CCMSQLiteHelper.TABLE_RENDEZVOUS, null,
                values);
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RENDEZVOUS,
                allColumns, CCMSQLiteHelper.RDV_COLUMN_UID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        RendezVous newRendezVous = cursorToRendezVous(cursor);
        cursor.close();
        return newRendezVous;
    }

    public void updateRendezVous(RendezVous rendezVous) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.RDV_COLUMN_UID,rendezVous.getUid());
        values.put(CCMSQLiteHelper.RDV_COLUMN_CID,rendezVous.getCid());
        values.put(CCMSQLiteHelper.RDV_COLUMN_DATE, rendezVous.getDate());
        values.put(CCMSQLiteHelper.RDV_COLUMN_DESC, rendezVous.getDescription());
        db.update(CCMSQLiteHelper.TABLE_RENDEZVOUS,values,CCMSQLiteHelper.RDV_COLUMN_UID+"="+rendezVous.getUid(),null);
    }

    public void deleteRendezVous(RendezVous clientTravail) {
        long id = clientTravail.getUid();
        db.delete(CCMSQLiteHelper.TABLE_RENDEZVOUS, CCMSQLiteHelper.RDV_COLUMN_UID
                + " = " + id, null);
    }

    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rendezVous = new ArrayList<RendezVous>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RENDEZVOUS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RendezVous rendezVousReturn = cursorToRendezVous(cursor);
            rendezVous.add(rendezVousReturn);
            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return rendezVous;
    }

    public RendezVous getRendezVousFromId(long id) {
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RENDEZVOUS, allColumns,CCMSQLiteHelper.RDV_COLUMN_UID+"="+id,null,null,null,null);
        if(cursor.getCount()==0) {
            return null;
        }
        cursor.moveToFirst();
        RendezVous rendezVous= cursorToRendezVous(cursor);
        cursor.close();
        return rendezVous;
    }

    public List<RendezVous> getRendezVousFromClientID(String Cid) {
        List<RendezVous> rendezVous = new ArrayList<RendezVous>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RENDEZVOUS, allColumns,CCMSQLiteHelper.RDV_COLUMN_CID+"='"+Cid+"'",null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RendezVous rendezVousReturn = cursorToRendezVous(cursor);
            rendezVous.add(rendezVousReturn);
            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return rendezVous;
    }

    private RendezVous cursorToRendezVous(Cursor cursor) {

        Tds.open();
        RendezVous rendezVous = new RendezVous();
        rendezVous.setUid(cursor.getLong(0));
        rendezVous.setCid(cursor.getString(1));
        rendezVous.setDate(cursor.getString(2));
        rendezVous.setDescription(cursor.getString(3));
        Tds.close();
        return rendezVous;
    }

}

