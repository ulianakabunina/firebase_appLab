package com.example.firebase_applab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RegisterActivity — экран регистрации нового пользователя.
 *
 * Назначение:
 * 1. Позволяет пользователю создать новый аккаунт (через Firebase Authentication).
 * 2. После успешной регистрации — сохраняет данные пользователя (имя, email, дата регистрации)
 *    в Firebase Realtime Database.
 * 3. После регистрации перенаправляет пользователя на экран входа (LoginActivity).
 */
public class RegisterActivity extends AppCompatActivity {

    // Элементы интерфейса
    /** Поле для ввода имени пользователя */
    private EditText etName;

    /** Поле для ввода email */
    private EditText etEmail;

    /** Поле для ввода пароля */
    private EditText etPassword;

    /** Кнопка "Зарегистрироваться" */
    private Button btnRegister;

    /** Индикатор загрузки (показывается при регистрации) */
    private ProgressBar progressBar;

    // Firebase
    /** Объект Firebase Authentication для регистрации пользователя */
    private FirebaseAuth mAuth;

    /** Ссылка на Realtime Database для хранения данных пользователя */
    private DatabaseReference mDatabase;

    /**
     * Метод жизненного цикла Activity — вызывается при создании экрана.
     * Здесь происходит инициализация Firebase, UI-элементов и обработчика кнопки регистрации.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Привязка элементов интерфейса
        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.registerProgressBar);

        // Обработчик кнопки "Зарегистрироваться"
        btnRegister.setOnClickListener(v -> registerUser());
    }

    /**
     * Метод выполняет регистрацию нового пользователя.
     * 1. Проверяет корректность введённых данных.
     * 2. Создаёт пользователя в Firebase Authentication.
     * 3. Сохраняет его данные в Realtime Database.
     */
    private void registerUser() {
        // Считываем введённые данные
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверка полей
        if (TextUtils.isEmpty(name)) {
            etName.setError("Имя не может быть пустым");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email не может быть пустым");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Пароль не может быть пустым");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Пароль должен быть не менее 6 символов");
            etPassword.requestFocus();
            return;
        }

        // Отображаем индикатор загрузки
        progressBar.setVisibility(View.VISIBLE);

        // Создаём пользователя в Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Регистрация успешна — получаем UID пользователя
                        String uid = mAuth.getCurrentUser().getUid();

                        // Сохраняем дополнительные данные пользователя в базе
                        saveUserDataToDatabase(uid, name, email);
                    } else {
                        // Ошибка регистрации
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this,
                                "Ошибка регистрации: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Сохраняет данные пользователя (имя, email, дата регистрации)
     * в Firebase Realtime Database в узел "Users/{uid}".
     *
     * @param uid  — уникальный идентификатор пользователя (из Firebase Authentication)
     * @param name — имя пользователя
     * @param email — email пользователя
     */
    private void saveUserDataToDatabase(String uid, String name, String email) {
        // Форматируем дату регистрации
        String regDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Создаём объект User
        User user = new User(name, email);
        user.setRegistrationDate(regDate);

        // Сохраняем объект в базе данных Firebase
        mDatabase.child("Users").child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Данные успешно сохранены
                        Toast.makeText(RegisterActivity.this,
                                "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();

                        // Переход на экран входа
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Ошибка сохранения данных
                        Toast.makeText(RegisterActivity.this,
                                "Ошибка сохранения данных: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
