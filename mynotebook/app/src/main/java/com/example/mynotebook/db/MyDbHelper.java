package com.example.mynotebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

// Создаём класс для создания базы данных и таблицы,
// который наследуется от класса SQLiteOpenHelper
public class MyDbHelper extends SQLiteOpenHelper {

    // Создание базы данных
    public MyDbHelper(@Nullable Context context) {
        super(context, MyConstants.DB_NAME, null, MyConstants.DB_VERSION);
    }

    // Создание таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyConstants.TABLE_STRUCTURE);
    }

    // Обновление базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление старой версии таблицы
        db.execSQL(MyConstants.DROP_TABLE);
        // Создание новой таблицы
        onCreate(db);
    }
}
