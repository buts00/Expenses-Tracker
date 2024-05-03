package com.example.lab19;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.example.lab19.DatabaseManager.*;


public class ExpenseController implements Initializable {

    @FXML
    public Label allSumLabel;

    @FXML
    public TableView<ExpenseModel> expensesTableView;

    @FXML
    public TextField finderTextField;

    @FXML
    private TableColumn<ExpenseModel, Integer> idColumn;

    @FXML
    private TableColumn<ExpenseModel, String> descriptionColumn;

    @FXML
    private TableColumn<ExpenseModel, Double> amountColumn;

    static  DatabaseManager manager;


    public static final String PATH_TO_CONFIG = ".env";

    private final ObservableList<ExpenseModel> expenseObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableView();
        loadExpensesFromDatabase();

        try {
            allSumLabel.setText("Загальна сума витрат: "  +  showTotalExpenses());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setupFiltering();
    }

    private void setupFiltering() {
        FilteredList<ExpenseModel> filteredList = new FilteredList<>(expenseObservableList, b -> true);
        finderTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(expenseSearchModel -> {
                if (newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();
                if (expenseSearchModel.getDescription().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (Integer.toString(expenseSearchModel.getId()).contains(searchKeyword)) {
                    return true;
                } else return Double.toString(expenseSearchModel.getAmount()).contains(searchKeyword);
            });
        });

        SortedList<ExpenseModel> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(expensesTableView.comparatorProperty());

        expensesTableView.setItems(sortedList);
    }

    private void initializeTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        expensesTableView.setItems(expenseObservableList);
    }

    private void loadExpensesFromDatabase() {

        try {
            DatabaseConfig.init();
            manager= new DatabaseManager();
            expenseObservableList.addAll(manager.getExpensesForPage(expenseObservableList.size()));

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public  void onNextPageClick(MouseEvent mouseEvent) throws SQLException {

        expenseObservableList.addAll(manager.getExpensesForPage(expenseObservableList.size()));
        allSumLabel.setText("Загальна сума витрат: "  +  showTotalExpenses());
    }


    public void onDeleteButtonClick(MouseEvent mouseEvent) throws SQLException {
        ExpenseModel expenseModel = expensesTableView.getSelectionModel().getSelectedItem();
        if (expenseModel != null) {
            deleteExpenseAsync(expenseModel.getId());
            expenseObservableList.remove(expenseModel);
            allSumLabel.setText("Загальна сума витрат: "  +  showTotalExpenses());
        }

    }

    public void OnAddExpensesButtonClick(MouseEvent mouseEvent) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add_expenses.fxml"));
            Parent root = fxmlLoader.load();
            Stage addExpenseStage = new Stage();
            addExpenseStage.initModality(Modality.APPLICATION_MODAL);
            addExpenseStage.setTitle("Add Expenses");
            addExpenseStage.setScene(new Scene(root, 300, 200));
            addExpenseStage.setResizable(false);
            addExpenseStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnUpdateExpensesButtonClick(MouseEvent mouseEvent) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("update_expenses.fxml"));
            Parent root = fxmlLoader.load();
            ExpenseModel expenseModel = expensesTableView.getSelectionModel().getSelectedItem();
            if (expenseModel != null) {
                UpdateExpensesController controller = fxmlLoader.getController();
                controller.setId(expenseModel.getId());
                controller.setAmountTextField(expenseModel.getAmount());
                controller.setDescriptionTextField(expenseModel.getDescription());
                Stage updateExpenseStage = new Stage();
                updateExpenseStage.initModality(Modality.APPLICATION_MODAL);
                updateExpenseStage.setTitle("update Expenses");
                updateExpenseStage.setScene(new Scene(root, 300, 230));
                updateExpenseStage.setResizable(false);
                updateExpenseStage.showAndWait();
                expenseObservableList.clear();
                loadExpensesFromDatabase();
                allSumLabel.setText("Загальна сума витрат: " + showTotalExpenses()); // Оновлення мітки зі сумою
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void OnClearAllButtonClick(MouseEvent mouseEvent) throws SQLException {
        clearAllExpensesAsync();
        expenseObservableList.clear();
        loadExpensesFromDatabase();
    }

    public void OnExportButtonClick(ActionEvent actionEvent) {
       exportDataAsync();
    }

    public void OnImportButtonClick(ActionEvent actionEvent) {
        importDataAsync();
        expenseObservableList.clear();
        loadExpensesFromDatabase();
    }

    public void OnAboutButtonClick(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про програму");
        alert.setHeaderText("Інформація про програму");
        alert.setContentText("Ця програма є трекером витрат і призначена для ведення обліку особистих фінансів. За допомогою цієї програми ви можете додавати, редагувати, видаляти та переглядати ваші витрати. Також вона надає можливість експортувати та імпортувати дані про витрати. Підтримує CRUD операції для керування вашими фінансами.\n\n" +
                "Версія: 1.0\n" +
                "Автор: Андрій Буц");

        alert.showAndWait();
    }
}
