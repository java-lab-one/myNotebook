package com.example.mynotebook.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.mynotebook.adapter.ListItem;
import java.util.ArrayList;
import java.util.List;


// Создаём вспомогательный класс для взаимодействия с базой данных
public class MyDbManager {
    // Поле контекста с активити
    private Context context;
    // Определяем созданный нами класс для создания базы данных и таблиц
    private MyDbHelper myDbHelper;
    // Определяем базу данных для взаимодействия
    private SQLiteDatabase db;

    public MyDbManager(Context context) {
        this.context = context;
        myDbHelper = new MyDbHelper(context);
    }

    // Открытие базы данных для записис и считывания
    public void openDb() {
        db = myDbHelper.getWritableDatabase();
    }

    // Запись табличных значений в базу данных
    public void insertToDb(String title, String disc, String uri) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DISC, disc);
        cv.put(MyConstants.URI, uri);
        db.insert(MyConstants.TABLE_NAME, null, cv);
    }

    // Обновление данных в таблице
    public void updateItem(String title, String disc, String uri, int id) {
        String selection = MyConstants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DISC, disc);
        cv.put(MyConstants.URI, uri);
        db.update(MyConstants.TABLE_NAME, cv, selection, null);
    }

    // Удаление данных
    public void delete(int id) {
        String selection = MyConstants._ID + "=" + id;
        db.delete(MyConstants.TABLE_NAME, selection, null);
    }

    // Извлечь данные по запросу пользователя
    public void getFromDb(String searchText, OnDataReceived onDataReceived) {
        // Создаём список из элементов ListItem (заголовок, описание, картинка)
        final List<ListItem> tempList = new ArrayList<>();
        String selection = MyConstants.TITLE + " like ?";
        final Cursor cursor = db.query(MyConstants.TABLE_NAME, null, selection,
                new String[]{"%" + searchText + "%"}, null, null, null);

        while (cursor.moveToNext()) {
            ListItem item = new ListItem();
            // С помощью курсора берём из базы данных значения
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DISC));
            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            // Записываем данные в класс ListItem
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);

        }
        cursor.close();
        onDataReceived.onReceived(tempList);
    }

    // Закрыть базу данных
    public void closeDb() {
        myDbHelper.close();
    }
}