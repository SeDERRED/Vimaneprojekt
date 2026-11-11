package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;

public class LogServer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/telemetry_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/history", new HistoryHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Веб-сайт истории запущен! Откройте в браузере: http://localhost:8080/history");
        } catch (IOException e) {
            System.err.println("Не удалось запустить веб-сервер: " + e.getMessage());
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>")
                    .append("<html lang='ru'><head><meta charset='UTF-8'>")
                    .append("<title>История запусков приложения</title>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; background: #121212; color: #e0e0e0; padding: 20px; }")
                    .append("h2 { color: #4CAF50; }")
                    .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; background: #1e1e1e; }")
                    .append("th, td { padding: 12px 15px; border: 1px solid #333; text-align: left; }")
                    .append("th { background-color: #333; color: #fff; }")
                    .append("tr:hover { background-color: #2a2a2a; }")
                    .append("</style></head><body>")
                    .append("<h2>История запусков приложения</h2>")
                    .append("<table><tr><th>ID</th><th>Пользователь (Username)</th><th>Время запуска</th></tr>");

            try {
                // Явно регистрируем драйвер MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT id, username, launch_time FROM app_launches ORDER BY launch_time DESC")) {

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String username = rs.getString("username");
                        Timestamp time = rs.getTimestamp("launch_time");

                        html.append("<tr>")
                                .append("<td>").append(id).append("</td>")
                                .append("<td>").append(username).append("</td>")
                                .append("<td>").append(time).append("</td>")
                                .append("</tr>");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Покажет полный стек ошибки в консоли IDEA
                html.append("<tr><td colspan='3' style='color: red;'>Ошибка: ").append(e.getClass().getName()).append(" - ").append(e.getMessage()).append("</td></tr>");
            }

            html.append("</table></body></html>");

            byte[] responseBytes = html.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}