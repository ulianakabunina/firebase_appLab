package com.example.firebase_applab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * MainActivity — главный экран приложения.
 *
 * Назначение:
 * 1. Проверяет, авторизован ли пользователь (через Firebase Authentication).
 * 2. Если пользователь авторизован, загружает его данные из Firebase Realtime Database.
 * 3. Отображает имя и email пользователя.
 * 4. Предоставляет возможность выйти из системы.
 * 5. Если пользователь не авторизован — перенаправляет на экран входа (LoginActivity).
 */
public class MainActivity extends AppCompatActivity {

    // Объявление полей класса
    /** Кнопка для выхода из аккаунта */
    private Button btnLogout;

    /** Текстовые поля для отображения имени и email пользователя */
    private TextView tvUserName, tvUserEmail;

    /** Объект для работы с Firebase Authentication */
    private FirebaseAuth mAuth;

    /** Ссылка на Realtime Database */
    private DatabaseReference mDatabase;

    /** Текущий авторизованный пользователь */
    private FirebaseUser currentUser;

    /**
     * Метод жизненного цикла onCreate() — вызывается при создании Activity.
     * Здесь происходит:
     * - инициализация Firebase,
     * - проверка авторизации,
     * - загрузка пользовательских данных,
     * - настройка кнопки выхода.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Получаем текущего пользователя (если он авторизован)
        currentUser = mAuth.getCurrentUser();

        // Если пользователь не вошёл в систему — переходим на экран входа
        if (currentUser == null) {
            goToLogin();
            return;
        }

        // Инициализация базы данных
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Привязка элементов интерфейса к переменным
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnLogout = findViewById(R.id.btnLogout);

        // Загружаем информацию о пользователе из базы данных
        loadUserInfo();

        // Обработчик нажатия на кнопку "Выход"
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();  // Выходим из аккаунта Firebase
            goToLogin();      // Возвращаемся на экран авторизации
        });
    }

    /**
     * Загружает информацию о пользователе из Firebase Realtime Database.
     * Ожидается, что в базе есть узел:
     * "Users/{uid}", где uid — уникальный идентификатор пользователя.
     *
     * Пример структуры:
     * Users
     *   └── UID12345
     *         ├── name: "Иван"
     *         └── email: "ivan@example.com"
     */
    private void loadUserInfo() {
        String uid = currentUser.getUid(); // Получаем UID текущего пользователя

        // Обращаемся к ветке "Users/{uid}" в Realtime Database
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Конвертируем данные в объект User (должен быть отдельный класс User.java)
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    // Если данные успешно получены — отображаем
                    tvUserName.setText("Имя: " + user.getName());
                    tvUserEmail.setText("Email: " + user.getEmail());
                } else {
                    // Если данных нет — показываем сообщение и выводим fallback-информацию
                    Toast.makeText(MainActivity.this, "Не удалось загрузить данные пользователя.", Toast.LENGTH_SHORT).show();
                    tvUserEmail.setText(currentUser.getEmail());
                    tvUserName.setText("Имя не найдено");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении из базы данных
                Toast.makeText(MainActivity.this, "Ошибка загрузки: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Перенаправляет пользователя на экран входа (LoginActivity).
     * Используется при выходе из аккаунта или если пользователь не авторизован.
     *
     * Флаги Intent:
     * FLAG_ACTIVITY_NEW_TASK — создаёт новую задачу.
     * FLAG_ACTIVITY_CLEAR_TASK — очищает предыдущие Activity из стека.
     */
    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Завершаем текущую Activity, чтобы пользователь не вернулся кнопкой "Назад"
    }
}
