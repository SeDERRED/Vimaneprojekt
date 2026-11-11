package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/telemetry_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void logAppLaunch() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Создаем таблицу для фиксирования запусков, если её нет
                String tableSql = "CREATE TABLE IF NOT EXISTS app_launches (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(100), " +
                        "launch_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(tableSql);

                // Узнаем, кто запустил (имя пользователя системы)
                String currentUser = System.getProperty("user.name");

                // Записываем: кто и когда
                String insertSql = "INSERT INTO app_launches (username) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, currentUser);
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка записи запуска: " + e.getMessage());
        }
    }
}
