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
    private String[] allColumns = { CCMSQLiteHelper.RT_COLUMN_RID, CCMSQLiteHelper.RT_COLUMN_TID };

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
        db.insert(CCMSQLiteHelper.TABLE_RDV_TRV, null,values);
        RdvTrv retRdvTrv=new RdvTrv();
        retRdvTrv.setRid(rdvId);
        retRdvTrv.setTid(trvId);
        return retRdvTrv;
    }

    public void deleteRdvTrv(RdvTrv toDelRdvTrv) {
        deleteRdvTrv(toDelRdvTrv.getRid(),toDelRdvTrv.getTid());
    }

    public void deleteRdvTrv(long rdvId, long trvId) {
        db.delete(CCMSQLiteHelper.TABLE_RDV_TRV, CCMSQLiteHelper.RT_COLUMN_RID
                + " = " + rdvId + " AND "+ CCMSQLiteHelper.RT_COLUMN_TID +" = "+ trvId, null);
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
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV, allColumns,CCMSQLiteHelper.RT_COLUMN_RID+"="+getRid,null,null,null,null);
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
        Cursor cursor = db.query(CCMSQLiteHelper.TABLE_RDV_TRV, allColumns,CCMSQLiteHelper.RT_COLUMN_TID+"="+getTid,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RdvTrv unRdvTrv = cursorToRdvTrv(cursor);
            someRdvTrv.add(unRdvTrv);
            cursor.moveToNext();
        }
        cursor.close();
        return someRdvTrv;
    }

    public boolean RdvTrvExists(Long rid, Long tid) {
        Cursor cursor=db.query(CCMSQLiteHelper.TABLE_RDV_TRV,allColumns,CCMSQLiteHelper.RT_COLUMN_TID+"="+tid+" and "
            + CCMSQLiteHelper.RT_COLUMN_RID+"="+rid,null,null,null,null);
        boolean RTExists=(cursor.getCount()>0);
        cursor.close();
        return RTExists;
    }

    private RdvTrv cursorToRdvTrv(Cursor cursor) {
        RdvTrv rdv_trv = new RdvTrv();
        rdv_trv.setRid(cursor.getLong(0));
        rdv_trv.setTid(cursor.getLong(1));
        return rdv_trv;
    }

}
