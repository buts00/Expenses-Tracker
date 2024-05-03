package com.example.lab19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.lab19.ExpenseController.PATH_TO_CONFIG;

public class DatabaseConfig {
    private static String DB_URL;
    private static String USER;
    private static String DB_PASSWORD;

    public static void init() throws IOException {
        readEnvVariables();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database.", e);
        }
    }

    private static void readEnvVariables() throws IOException {
        Path filePath = Paths.get(PATH_TO_CONFIG);
        Pattern pattern = Pattern.compile("^\\s*([^=]+)\\s*=\\s*['\"]?([^'\"]*)['\"]?\\s*$");

        Files.lines(filePath)
                .forEach(line -> {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        String key = matcher.group(1).trim();
                        String value = matcher.group(2).trim();
                        switch (key) {
                            case "DB_URL":
                                DB_URL = value;
                                break;
                            case "USER":
                                USER = value;
                                break;
                            case "DB_PASSWORD":
                                DB_PASSWORD = value;
                                break;
                        }
                    }
                });
    }
}
