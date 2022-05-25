package com.example.ceb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userdetails ( name TEXT primary key,number TEXT, complaint TEXT,latitude TEXT, longitude TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {

        DB.execSQL("drop Table if exists Userdetails");
    }

    public  Boolean insertData(String name ,String number , String complaint,String latitude, String longitude){
        SQLiteDatabase DB =this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("number",number);
        contentValues.put("complaint",complaint);
        contentValues.put("latitude",latitude);
        contentValues.put("longitude",longitude);
        long results = DB.insert("Userdetails",null,contentValues);
        if (results==-1){
            return false;
        }else {
            return true;
        }
    }
}
