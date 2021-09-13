package com.example.mynotebook.db;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Создаём класс AppExecuter для управления потоками
public class AppExecuter {
    private static AppExecuter instance;
    private final Executor mainIO;
    private final Executor subIO;

    public AppExecuter(Executor mainIO, Executor subIO) {
        this.mainIO = mainIO;
        this.subIO = subIO;
    }

    public static AppExecuter getInstance(){

        // Если объекта AppExecuter не существует, то создаём его
        if(instance == null) instance = new AppExecuter( new MainThreadHandler(), Executors.newSingleThreadExecutor());
        return instance;
    }

    // Создаём класс MainThreadHandler с имплементированным интерфейсом Executor
    // для обработки пользовательских событий через потоки
    public static class MainThreadHandler implements Executor{
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        // Запуск команды от пользователя на выполнение потоку
        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    }

    // Создаём геттеры для главного и второстепенного потока
    public Executor getMainIO() {
        return mainIO;
    }

    public Executor getSubIO() {
        return subIO;
    }
}
