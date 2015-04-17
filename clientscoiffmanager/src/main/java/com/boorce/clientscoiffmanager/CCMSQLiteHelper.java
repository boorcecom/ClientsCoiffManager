package com.boorce.clientscoiffmanager;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class CCMSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TRAVAUX = "travaux";
    public static final String TRV_COLUMN_ID = "_id";
    public static final String TRV_COLUMN_DESC = "description";

    public static final String TABLE_RDV_TRV= "rdv_travaux";
    public static final String RT_COLUMN_RID="rid";
    public static final String RT_COLUMN_TID="tid";


    public static final String TABLE_RENDEZVOUS = "rendezvous";
    public static final String RDV_COLUMN_UID = "_uid";
    public static final String RDV_COLUMN_CID = "clientid";
    public static final String RDV_COLUMN_DATE = "date";
    public static final String RDV_COLUMN_DESC = "description";


    private static final String DATABASE_NAME = "clientcoiffmanager.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TRAVAUX = "create table "
            + TABLE_TRAVAUX + "(" + TRV_COLUMN_ID
            + " integer primary key autoincrement, " + TRV_COLUMN_DESC
            + " text not null);";

    private static final String DATABASE_CREATE_RDV_TRV = "create table "
            + TABLE_RDV_TRV + "(" + RT_COLUMN_RID
            + " integer not null, " + RT_COLUMN_TID
            + " integer not null, PRIMARY KEY ( "+RT_COLUMN_RID+", "+RT_COLUMN_TID+" ) );";


    private static final String DATABASE_CREATE_RENDEZVOUS = "create table "
            + TABLE_RENDEZVOUS + "(" + RDV_COLUMN_UID
            + " integer primary key autoincrement, "
            + RDV_COLUMN_CID + " text not null, "
            + RDV_COLUMN_DATE + " text not null,"
            + RDV_COLUMN_DESC +" text);";


    public CCMSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TRAVAUX);
        database.execSQL(DATABASE_CREATE_RENDEZVOUS);
        database.execSQL(DATABASE_CREATE_RDV_TRV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Rien à faire, version 1 !
        //onCreate(db);
    }
}
