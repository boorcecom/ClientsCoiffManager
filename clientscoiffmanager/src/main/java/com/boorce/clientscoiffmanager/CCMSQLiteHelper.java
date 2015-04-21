package com.boorce.clientscoiffmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class CCMSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TRAVAUX = "travaux";
    public static final String TRV_COLUMN_ID = "_id";
    public static final String TRV_COLUMN_DESC = "description";

// Ajout d'un UID sur rdv_travaux, changement de primary key, et ajout d'index
    public static final String TABLE_RDV_TRV= "rdv_travaux";
    public static final String RT_COLUMN_UID="uid";
    public static final String RT_COLUMN_RID="rid";
    public static final String RT_COLUMN_TID="tid";


    public static final String TABLE_RENDEZVOUS = "rendezvous";
    public static final String RDV_COLUMN_UID = "_uid";
// Change link from clientid to clientName sur la V1 de la base (non release)
//    public static final String RDV_COLUMN_CID = "clientid";
    public static final String RDV_COLUMN_CNAME = "clientname";
    public static final String RDV_COLUMN_DATE = "date";
    public static final String RDV_COLUMN_DESC = "description";

    //Timestamp global pour les backups
    public static final String TABLE_PARAMETERS ="parametres";
    public static final String PAR_COLUMN_ENTRY="entry";
    public static final String PAR_COLUMN_DATA="data";

    // Ajout des index
    public static final String INDEX_CNAME_RENDEZVOUS ="index_rendezvous_cname";
    public static final String INDEX_DATE_RENDEZVOUS ="index_rendezvous_date";

    public static final String INDEX_RID_RDV_TRV="index_rdv_trv_rid";

    private static final String DATABASE_NAME = "clientcoiffmanager.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TRAVAUX = "create table "
            + TABLE_TRAVAUX + "(" + TRV_COLUMN_ID
            + " integer primary key autoincrement, " + TRV_COLUMN_DESC
            + " text not null);";

    private static final String DATABASE_CREATE_RDV_TRV = "create table "
            + TABLE_RDV_TRV + "("
            + RT_COLUMN_UID + " integer primary key autoincrement, "
            + RT_COLUMN_RID + " integer not null, "
            + RT_COLUMN_TID + " integer not null );";


    private static final String DATABASE_CREATE_RENDEZVOUS = "create table "
            + TABLE_RENDEZVOUS + "(" + RDV_COLUMN_UID
            + " integer primary key autoincrement, "
            + RDV_COLUMN_CNAME + " text not null, "
            + RDV_COLUMN_DATE + " text not null,"
            + RDV_COLUMN_DESC +" text);";

    private static final String DATABASE_CREATE_PARAMETERS ="create table "
            + TABLE_PARAMETERS +"(" + PAR_COLUMN_ENTRY
            + " text not null primary key, "
            + PAR_COLUMN_DATA +" text not null);";

// Ajout des index
    private static final String DATABASE_CREATE_INDEX_CNAME_RDV = "create index "
            + INDEX_CNAME_RENDEZVOUS + " on " + TABLE_RENDEZVOUS
            + " ( " +RDV_COLUMN_CNAME +" ); ";

    private static final String DATABASE_CREATE_INDEX_DATE_RDV = "create index "
            + INDEX_DATE_RENDEZVOUS + " on " + TABLE_RENDEZVOUS
            + " ( " +RDV_COLUMN_DATE +" ASC ); ";

    private static final String DATABASE_CREATE_INDEX_RID_RDV_TRV = "create index "
            + INDEX_RID_RDV_TRV + " on " + TABLE_RDV_TRV
            + " ( " +RT_COLUMN_RID +"); ";


    public CCMSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TRAVAUX);
        database.execSQL(DATABASE_CREATE_RENDEZVOUS);
        database.execSQL(DATABASE_CREATE_RDV_TRV);
        database.execSQL(DATABASE_CREATE_PARAMETERS);
        ContentValues values = new ContentValues();
        values.put(PAR_COLUMN_ENTRY,"com.boorce.ccm:lastmodified");
        values.put(PAR_COLUMN_DATA, String.valueOf(System.currentTimeMillis()));
        database.insert(TABLE_PARAMETERS,null,values);
// Ajout des index
        database.execSQL(DATABASE_CREATE_INDEX_CNAME_RDV);
        database.execSQL(DATABASE_CREATE_INDEX_DATE_RDV);
        database.execSQL(DATABASE_CREATE_INDEX_RID_RDV_TRV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Rien à faire, version 1 !
        //onCreate(db);
        if(oldVersion==1) {
            database.execSQL(DATABASE_CREATE_PARAMETERS);
            ContentValues values = new ContentValues();
            values.put(PAR_COLUMN_ENTRY,"com.boorce.ccm:lastmodified");
            values.put(PAR_COLUMN_DATA, String.valueOf(System.currentTimeMillis()));
            database.insert(TABLE_PARAMETERS,null,values);
        }
    }

    // Gestion des timestamp et des paramètres
    static public void setLastModified(SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(PAR_COLUMN_ENTRY,"com.boorce.ccm:lastmodified");
        values.put(PAR_COLUMN_DATA, String.valueOf(System.currentTimeMillis()));
        database.replace(TABLE_PARAMETERS,null,values);
    }

    static public String getParameters(SQLiteDatabase database, String parameter) {
        String[] allColumns = { PAR_COLUMN_ENTRY, PAR_COLUMN_DATA };
        Cursor cursor = database.query(TABLE_PARAMETERS, allColumns,
                CCMSQLiteHelper.PAR_COLUMN_ENTRY+" like '%"+parameter+"%'",null,null,null,null);
        cursor.moveToFirst();
        String returnData=cursor.getString(1);
        cursor.close();
        return returnData;
    }



    static public Long getLastModified(SQLiteDatabase database) {
        String timestamp=getParameters(database,"com.boorce.ccm:lastmodified");
        return Long.parseLong(timestamp,10);
    }

}
