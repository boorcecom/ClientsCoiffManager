package com.boorce.clientscoiffmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RdvTrvDataSource {

    private SQLiteDatabase db;
    private CCMSQLiteHelper dbHelper;
    private String[] allColumns = { CCMSQLiteHelper.RT_COLUMN_UID,
            CCMSQLiteHelper.RT_COLUMN_RID, CCMSQLiteHelper.RT_COLUMN_TID };

    public RdvTrvDataSource(Context ctx) {
        dbHelper=new CCMSQLiteHelper(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public RdvTrv createRdvTrv(long rdvId, long trvId) {
        ContentValues values = new ContentValues();
        values.put(CCMSQLiteHelper.RT_COLUMN_RID, rdvId);
        values.put(CCMSQLiteHelper.RT_COLUMN_TID, trvId);
        long insertId=db.insert(CCMSQLiteHelper.TABLE_RDV_TRV, null,values);
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV,
                allColumns, CCMSQLiteHelper.RT_COLUMN_UID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        RdvTrv newRdvTrv = cursorToRdvTrv(cursor);
        cursor.close();
        return newRdvTrv;
    }

    public void deleteRdvTrv(RdvTrv toDelRdvTrv) {
        deleteRdvTrv(toDelRdvTrv.getUid());
    }

    public void deleteRdvTrvAll(long rdvId, long trvId) {
        db.delete(CCMSQLiteHelper.TABLE_RDV_TRV, CCMSQLiteHelper.RT_COLUMN_RID
                + " = " + rdvId + " AND "+ CCMSQLiteHelper.RT_COLUMN_TID +" = "+ trvId, null);
    }

    public void deleteFirstRdvTrvFromRidTid(long rdvId, long trvId) {
        List<RdvTrv> listRdvTrv = getRdvTrvFromRidTid(rdvId,trvId);
        if(listRdvTrv.size()>0) {
            deleteRdvTrv(listRdvTrv.get(0));
        }
    }

    public void deleteRdvTrv(long uid) {
        db.delete(CCMSQLiteHelper.TABLE_RDV_TRV, CCMSQLiteHelper.RT_COLUMN_UID
                + " = " + uid, null);
    }


    public List<RdvTrv> getAllRdvTrv() {
        List<RdvTrv> allRdvTrv = new ArrayList<RdvTrv>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RdvTrv unRdvTrv = cursorToRdvTrv(cursor);
            allRdvTrv.add(unRdvTrv);
            cursor.moveToNext();
        }
// make sure to close the cursor
        cursor.close();
        return allRdvTrv;
    }

    public List<RdvTrv> getRdvTrvFromRid(Long getRid) {
        List<RdvTrv> someRdvTrv = new ArrayList<RdvTrv>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV, allColumns,
                CCMSQLiteHelper.RT_COLUMN_RID+"="+getRid,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RdvTrv unRdvTrv = cursorToRdvTrv(cursor);
            someRdvTrv.add(unRdvTrv);
            cursor.moveToNext();
        }
        cursor.close();
        return someRdvTrv;
    }

    public List<RdvTrv> getRdvTrvFromTid(Long getTid) {
        List<RdvTrv> someRdvTrv = new ArrayList<RdvTrv>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV, allColumns,
                CCMSQLiteHelper.RT_COLUMN_TID+"="+getTid,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RdvTrv unRdvTrv = cursorToRdvTrv(cursor);
            someRdvTrv.add(unRdvTrv);
            cursor.moveToNext();
        }
        cursor.close();
        return someRdvTrv;
    }

    public List<RdvTrv> getRdvTrvFromRidTid(Long getRid, Long getTid) {
        List<RdvTrv> someRdvTrv = new ArrayList<RdvTrv>();
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV, allColumns,
                CCMSQLiteHelper.RT_COLUMN_RID+"="+getRid + " AND " +
                CCMSQLiteHelper.RT_COLUMN_TID+"="+getTid ,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RdvTrv unRdvTrv = cursorToRdvTrv(cursor);
            someRdvTrv.add(unRdvTrv);
            cursor.moveToNext();
        }
        cursor.close();
        return someRdvTrv;
    }

    private RdvTrv cursorToRdvTrv(Cursor cursor) {
        RdvTrv rdv_trv = new RdvTrv();
        rdv_trv.setUid(cursor.getLong(0));
        rdv_trv.setRid(cursor.getLong(1));
        rdv_trv.setTid(cursor.getLong(2));
        return rdv_trv;
    }

}
