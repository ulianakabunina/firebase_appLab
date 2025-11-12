package com.example.firebase_applab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity — экран входа пользователя в приложение.
 *
 * Назначение:
 * 1. Позволяет пользователю войти в систему с помощью email и пароля.
 * 2. Использует Firebase Authentication для проверки данных.
 * 3. При успешном входе перенаправляет на MainActivity.
 * 4. Предоставляет ссылку для перехода на экран регистрации (RegisterActivity).
 */
public class LoginActivity extends AppCompatActivity {

    // Элементы интерфейса
    /** Поле для ввода email пользователя */
    private EditText etEmail;

    /** Поле для ввода пароля */
    private EditText etPassword;

    /** Кнопка для выполнения входа */
    private Button btnLogin;

    /** Ссылка для перехода к экрану регистрации */
    private TextView tvGoToRegister;

    /** Индикатор загрузки (ProgressBar), отображается во время входа */
    private ProgressBar progressBar;

    // Firebase
    /** Объект Firebase Authentication для выполнения входа */
    private FirebaseAuth mAuth;

    /**
     * Метод жизненного цикла Activity — вызывается при создании экрана.
     * Здесь происходит инициализация Firebase, элементов интерфейса и обработчиков событий.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Привязка элементов интерфейса из XML к переменным
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.loginProgressBar);

        // Обработчик кнопки входа
        btnLogin.setOnClickListener(v -> loginUser());

        // Переход на экран регистрации
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    /**
     * Метод выполняет вход пользователя с помощью Firebase Authentication.
     * Проверяет правильность заполнения полей и отображает индикатор загрузки.
     */
    private void loginUser() {
        // Получаем значения из полей ввода
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверка на пустой email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email не может быть пустым");
            etEmail.requestFocus();
            return;
        }

        // Проверка на пустой пароль
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Пароль не может быть пустым");
            etPassword.requestFocus();
            return;
        }

        // Показываем индикатор загрузки
        progressBar.setVisibility(View.VISIBLE);

        // Выполняем вход через Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Скрываем индикатор после выполнения запроса
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Вход успешен
                        Toast.makeText(LoginActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();

                        // Переход на главный экран (MainActivity)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Закрываем LoginActivity
                    } else {
                        // Ошибка входа — показываем сообщение пользователю
                        Toast.makeText(LoginActivity.this, "Ошибка входа: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
