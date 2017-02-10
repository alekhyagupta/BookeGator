package com.sqlite.sqliteapp;

//import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME  = "User2.dp";
    public static final String TABLE_NAME  = "user_table2";
    public static final String col0 = "ufid";
    public static final String col1 = "name";
    public static final String col2 = "phone";
    public static final String col3 = "email";
    public static final String col4 = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( ufid text primary key, name text, phone text, email text, address text );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String ufid, String name, String phone, String email, String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col0, ufid);
        contentValues.put(col1, name);
        contentValues.put(col2, phone);
        contentValues.put(col3, email);
        contentValues.put(col4, address);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }

    public boolean Exists(String _id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME + " where ufid = %s", new String[] { _id });
        if(cursor.getCount() > 0)
        {
            //cursor.close();
            return true;
        }
        else
            return false;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}
