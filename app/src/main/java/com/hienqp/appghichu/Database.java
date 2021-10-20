package com.hienqp.appghichu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    // Constructor
    public Database(
            @Nullable Context context,
            @Nullable String name,
            @Nullable SQLiteDatabase.CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    // phương thức truy vấn không trả về kết quả (chỉ thực thi): CREATE, INSERT, UPDATE, DELETE
    // phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
    public void QueryData(String sql) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }

    // phương thức truy vấn đồng thời lấy ra kết quả: SELECT
    // phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
    // kết quả trả về dạng con trỏ Cursor
    public Cursor GetData(String sql) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
