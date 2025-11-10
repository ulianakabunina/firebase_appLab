package com.example.firebase_applab;

/**
 * Модель данных пользователя для Firebase.
 * Содержит поля, пустой конструктор и конструктор с параметрами,
 * а также геттеры и сеттеры.
 */
public class User {

    // --- Поля ---
    private String name;
    private String email;
    private String registrationDate;

    /**
     * Пустой конструктор.
     * Обязателен для Firebase Realtime Database
     * при десериализации данных (чтении из БД).
     */
    public User() {
        // Пусто
    }

    /**
     * Конструктор с параметрами name и email.
     * Используется для удобного создания объекта при регистрации.
     *
     * @param name  Имя пользователя
     * @param email Email пользователя
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // --- Геттеры и Сеттеры ---
    // (Код геттеров и сеттеров без изменений)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}