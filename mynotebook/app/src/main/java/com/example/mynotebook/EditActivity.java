package com.example.mynotebook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mynotebook.adapter.ListItem;
import com.example.mynotebook.db.AppExecuter;
import com.example.mynotebook.db.MyConstants;
import com.example.mynotebook.db.MyDbManager;


// Создание активности для редактирования заметок
public class EditActivity extends AppCompatActivity {

    private final int PICK_IMAGE_CODE = 1;
    private ImageView imNewImage;
    private ConstraintLayout imageContainer;
    private Button fbAddImage;
    private Button imEditImage, imDeleteImage;
    private EditText edTitle, edDesc;
    private MyDbManager myDbManager;
    private String tempUri = "empty";

    private boolean isEditState = true;
    private ListItem item;
    private Toast savedToast;
    private Toast emptyToast;
    private TextView emptyToastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        getMyIntents();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // После того как активность оказывается на первом плане -
        // подключаем базу данных.
        myDbManager.openDb();
    }

    // Используем метод onActivityResult для получения результата из акивити
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Если результирующий код RESULT_OK, код запроса PICK_IMAGE_CODE и data (неравная null)
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){
            // Путю присваиваем значение из data
            tempUri = data.getData().toString();
            // Устанавливаем путь изображения
            imNewImage.setImageURI(data.getData());
            // Разрешаем чтение данных из внутреннего хранилища data
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    // Инициализация объектов
    private void init(){
        edTitle = findViewById(R.id.edTitle);
        edDesc = findViewById(R.id.edDesc);
        imNewImage = findViewById(R.id.imNewImage);
        fbAddImage = findViewById(R.id.fbAddImage);
        imageContainer = findViewById(R.id.imageContainer);
        imEditImage = findViewById(R.id.imEditImage);
        imDeleteImage = findViewById(R.id.imDeleteImage);
        myDbManager = new MyDbManager(this);
        savedToast = Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT);
        savedToast.setGravity(Gravity.CENTER,0,0);
        emptyToast = Toast.makeText(this, R.string.text_empty, Toast.LENGTH_LONG);
        emptyToast.setGravity(Gravity.CENTER,0,0);
        emptyToastView = (TextView) emptyToast.getView().findViewById(android.R.id.message);
    }

    // Intent для запуска EditActivity
    // Данным методом отображаем в соответствующих полях активити
    // записи из базы данных: заголовок, описание, картинка.
    // Если присутствует ссылка на картинку - отображаем её и
    // скрываем кнопку "Добавить картинку".
    private void getMyIntents(){
        Intent i = getIntent();
        if(i != null){
            item = (ListItem)i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            isEditState = i.getBooleanExtra(MyConstants.EDIT_STATE, true);
            if(!isEditState){
                edTitle.setText(item.getTitle());
                edDesc.setText(item.getDesc());
                if(!item.getUri().equals("empty")){
                    tempUri = item.getUri();
                    imageContainer.setVisibility(View.VISIBLE);
                    if (item.getUri().equals("empty")) {
                        fbAddImage.setVisibility(View.VISIBLE);
                    } else {
                        fbAddImage.setVisibility(View.GONE);
                    }
                    imNewImage.setImageURI(Uri.parse(item.getUri()));
                }
            }
        }
    }

    // Слушатель нажатий на кнопку "Сохранить"
    public void onClickSave(View view) {
        final String title = edTitle.getText().toString();
        final String desc = edDesc.getText().toString();
        // Если текстовые поля пустые (хотя бы одно из двух), то предупреждаем пользователя об этом.
        // Заполнение полей обязательно.
        if (title.equals("") || desc.equals("")) {
            emptyToastView.setGravity(Gravity.CENTER);
            emptyToast.show();
        } else {
            // Для внесения данных в поля заголовка и описания запускаем
            // дополнительный поток (РЕАЛИЗУЕМ МНОГОПОТОЧНОСТЬ)
            if(isEditState){
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        myDbManager.insertToDb(title, desc, tempUri);
                    }
                });
                savedToast.show();
            // Для изменения изменения данных в полях заголовка и описания
            // обновляем записи в базе данных
            } else {
                myDbManager.updateItem(title, desc, tempUri,item.getId());
                savedToast.show();
            }
            // Закрываем базу данных
            myDbManager.closeDb();
            // Завершаем работу EditActivity
            finish();
        }
    }

    // Слушатель нажатий для кнопки удаления картинки
    public void onClickDeleteImage(View view){
        // Происходит замена установленной картинки на картинку по умолчанию
        imNewImage.setImageResource(R.drawable.ic_add_image);
        tempUri = "empty";
        imageContainer.setVisibility(View.GONE);
        fbAddImage.setVisibility(View.VISIBLE);
    }

    // Слушатель нажатий для добавления картинки
    public void onClickAddImage(View view){
        imageContainer.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    // Слушатель нажатий для выбора картинки
    public void onClickChooseImage(View view){
        // Обращаемся к приложению в смартфоне где хранятся картинки
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        // Дожидаемся отклика приложения и запускаем активность
        startActivityForResult(chooser, PICK_IMAGE_CODE);
    }
}