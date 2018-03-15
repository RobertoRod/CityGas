package com.example.facop.citygas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by facop on 23/10/2017.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table usuario (id integer primary key, idUsuario integer, nombre text, email text)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Auto-generated method stub
        db.execSQL("drop table if exists usuario");
        db.execSQL("create table saldo (id integer primary key, idUsuario integer, nombre text, email text)");
    }
}

