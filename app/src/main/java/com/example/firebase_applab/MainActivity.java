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
 * MainActivity - главный экран приложения.
 * 1. Проверяет, авторизован ли пользователь.
 * 2. Если да - показывает информацию о нем (из Realtime Database).
 * 3. Если нет - перенаправляет на LoginActivity.
 * 4. Реализует выход из системы.
 */
public class MainActivity extends AppCompatActivity {

    // (Остальной код класса MainActivity без изменений)
    // ...
    private Button btnLogout;
    private TextView tvUserName, tvUserEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            goToLogin();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserInfo();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            goToLogin();
        });
    }

    private void loadUserInfo() {
        String uid = currentUser.getUid();

        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    tvUserName.setText("Имя: " + user.getName());
                    tvUserEmail.setText("Email: " + user.getEmail());
                } else {
                    Toast.makeText(MainActivity.this, "Не удалось загрузить данные пользователя.", Toast.LENGTH_SHORT).show();
                    tvUserEmail.setText(currentUser.getEmail());
                    tvUserName.setText("Имя не найдено");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Ошибка загрузки: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}