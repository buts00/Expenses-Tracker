package com.example.lab19;

import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DatabaseManager {
    static final String NAME_OF_TABLE = "expenses";
    private static final int RECORDS_PER_PAGE = 16;

    private static final String SEP = ";";

    private static final String GET_EXPENSES_QUERY_WITH_OFFSET = String.format("SELECT * FROM %s ORDER BY id LIMIT ? OFFSET ?", NAME_OF_TABLE);
    private static final String DELETE_EXPENSE_QUERY = String.format("DELETE FROM %s WHERE id = ?", NAME_OF_TABLE);

    private static final String GET_TOTAL_EXPENSES_QUERY = String.format("SELECT SUM(amount) AS total FROM %s", NAME_OF_TABLE);

    private static final String INSERT_EXPENSE_QUERY = String.format("INSERT INTO %s (description, amount) VALUES (?, ?)", NAME_OF_TABLE);

    private static final String UPDATE_EXPENSE_QUERY = String.format("UPDATE %s SET description = ?, amount = ? WHERE id = ?", NAME_OF_TABLE);
    private static final String CLEAR_ALL_EXPENSES_QUERY = String.format("DELETE FROM %s", NAME_OF_TABLE);

    private static final String GET_EXPENSES_QUERY = String.format("SELECT * FROM %s", NAME_OF_TABLE);

    List<ExpenseModel> getExpensesForPage(int offset) throws SQLException {
        List<ExpenseModel> expenses = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement stmt = connection.prepareStatement(GET_EXPENSES_QUERY_WITH_OFFSET)) {
            stmt.setInt(1, RECORDS_PER_PAGE);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String description = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    expenses.add(new ExpenseModel(id, description, amount));
                }
            }

        }


        return expenses;
    }

    static void deleteExpense(int id) throws SQLException {
        try (PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(DELETE_EXPENSE_QUERY)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static double showTotalExpenses() throws SQLException {
        try (PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(GET_TOTAL_EXPENSES_QUERY); ResultSet totalRs = stmt.executeQuery()) {
            if (totalRs.next()) {
                return totalRs.getDouble("total");
            }

        }
        return 0;
    }

    public static ExpenseModel addExpense(double amount, String description) throws SQLException {
        int id = -1;
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_EXPENSE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, description);
            stmt.setDouble(2, amount);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
            return new ExpenseModel(id, description, amount);
        }
    }

    public static void updateExpense(int id, double amount, String description) throws SQLException {

        try (PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(UPDATE_EXPENSE_QUERY)) {
            stmt.setString(1, description);
            stmt.setDouble(2, amount);
            stmt.setInt(3, id);
            int updatedRows = stmt.executeUpdate();
        }
    }

    public static void clearAllExpenses() throws SQLException {
        try (PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(CLEAR_ALL_EXPENSES_QUERY)) {
            stmt.executeUpdate();

        }
    }

    public static void deleteExpenseAsync(int id) {
        Thread thread = new Thread(() -> {
            try {
                deleteExpense(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void clearAllExpensesAsync() {
        Thread thread = new Thread(() -> {
            try {
                clearAllExpenses();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void exportDataAsync() {
        Thread thread = new Thread(DatabaseManager::exportData);
        thread.start();
    }

    public static void importDataAsync() {
        Thread thread = new Thread(DatabaseManager::importData);
        thread.start();
    }

    public static void exportData() {
        Platform.runLater(() -> {
            try {
                String projectDir = System.getProperty("user.dir");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Data");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setInitialDirectory(new File(projectDir));
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    try (PrintWriter writer = new PrintWriter(file)) {
                        PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(GET_EXPENSES_QUERY);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String desc = rs.getString("description");
                            double amtStr = rs.getDouble("amount");
                            writer.printf("%d%s%s%s%.2f\n", id, SEP, desc, SEP, amtStr);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void importData() {
        Platform.runLater(() -> {
            String projectDir = System.getProperty("user.dir");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Data File");
            fileChooser.setInitialDirectory(new File(projectDir));
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(INSERT_EXPENSE_QUERY);
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(SEP);
                        String desc = parts[1];
                        String amtStr = parts[2].replace(",", ".");
                        double amt = Double.parseDouble(amtStr);
                        stmt.setString(1, desc);
                        stmt.setDouble(2, amt);
                        stmt.executeUpdate();
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}