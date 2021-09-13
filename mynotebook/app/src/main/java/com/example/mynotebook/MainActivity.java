package com.example.mynotebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotebook.adapter.ListItem;
import com.example.mynotebook.adapter.MainAdapter;
import com.example.mynotebook.db.AppExecuter;
import com.example.mynotebook.db.MyDbManager;
import com.example.mynotebook.db.OnDataReceived;

import java.util.List;

// Имплеиентируем интерфейс OnDataReceived для работы с методом
// onReceived, который вызываетм класс ListItem, включающий элементы:
// заголовок, описание, картинка.
public class MainActivity extends AppCompatActivity implements OnDataReceived {

    private MyDbManager myDbManager;
    private EditText edTitle, edDisc;
    private RecyclerView rcView;
    private MainAdapter mainAdapter;
    static boolean greetings = false;
    private Toast helloToast;
    private TextView helloToastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    // Методом onCreateOptionsMenu создаём меню с названием
    // приложения и кнопкой поиска.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item= menu.findItem(R.id.id_search);
        SearchView sv = (SearchView)item.getActionView();
        // Настраиваем работу кнопки поиска
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // Постепенный поиск текста в базе данных
            @Override
            public boolean onQueryTextChange(final String newText) {
                readFromDb(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // Функция для инициализации объектов
    private void init() {
        myDbManager = new MyDbManager(this);
        edTitle = findViewById(R.id.edTitle);
        edDisc = findViewById(R.id.edDesc);
        rcView = findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        getItemTouchHelper().attachToRecyclerView(rcView);
        rcView.setAdapter(mainAdapter);
        // Инициализируем сообщение о возможности удаления заметок
        helloToast = Toast.makeText(getApplicationContext(),
                getString(R.string.hellotoast), Toast.LENGTH_LONG);
        helloToast.setGravity(Gravity.CENTER, 0, 0);
        helloToastView = (TextView) helloToast.getView().findViewById(android.R.id.message);
    }

    // Этим методом после старта главной активности запускаем базу данных,
    // а также однократно выводим сообщение для пользователя о возможности
    // удаления заметок.
    @Override
    protected void onResume() {
        super.onResume();
        if (!greetings) {
            if (helloToastView != null) {
                helloToastView.setGravity(Gravity.CENTER);
            }
            helloToast.show();
            greetings = true;
        }
        myDbManager.openDb();
        readFromDb("");
    }

    // Переход на активити для создания и редактирования записи
    public void onClickAdd(View view) {
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
    }

    // При закрытии приложения закрываем базу данных
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.closeDb();
    }

    // Метод для работы с функционалом "свайп": удаление записи правым/левым свайпом
    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mainAdapter.removeItem(viewHolder.getAdapterPosition(), myDbManager);
            }
        });
    }

    // Запускаем дополнительный поток в котором считываем из базы данных
    // строку заголовка и отображаем её на MainActivity
    private void readFromDb(final String text){
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.getFromDb(text, MainActivity.this);
            }
        });
    }

    // Запускаем дополнительный поток для создания записи
    @Override
    public void onReceived(final List<ListItem> list) {
        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                mainAdapter.updateAdapter(list);
            }
        });
    }
}