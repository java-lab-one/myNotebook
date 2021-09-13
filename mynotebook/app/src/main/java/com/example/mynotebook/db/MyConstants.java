package com.example.mynotebook.db;

// Создаём класс с константами для упрощения работы
// с вводом синтаксических констррукций на языке sql
public class MyConstants {
    // Указываем основные атрибуты таблицы
    public static final String EDIT_STATE = "edit_state";
    // Ключь для передачи интента
    public static final String LIST_ITEM_INTENT = "list_item_intent";
    public static final String TABLE_NAME = "my_table";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DISC = "disc";
    // Ссылка на картинку
    public static final String URI = "uri";
    public static final String DB_NAME = "my_db.db";
    // Константа DB_VERSION необходима для перезаписи базы данных в случае
    // добавления новых столбцов в таблицу.
    public static final int DB_VERSION = 2;

    // Команды для создания и удаления таблицы с учётом того существует она или нет
    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + DISC + " TEXT," +
            URI + " TEXT)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
