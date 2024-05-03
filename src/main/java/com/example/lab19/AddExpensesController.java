package com.example.lab19;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.sql.SQLException;

public class AddExpensesController {
    
    public TextField descriptionTextField;
    public TextField amountTextField;
    public Label descriptionLabel;
    public Label amountLabel;







    public void OnAddButtonClick(MouseEvent mouseEvent) throws SQLException {

        String description = descriptionTextField.getText();
        double amount = getValidDoubleAmount();
        if (amount != 0.0) {
            ExpenseModel newExpense = DatabaseManager.addExpense(amount,description);
            if (newExpense.getId() != -1) {

                amountLabel.setText("Enter expense amount:");
                amountLabel.setTextFill(Color.BLACK);
                amountTextField.setStyle(null);
            } else {

                amountLabel.setText("Невдалося додати нову витрату");
                amountLabel.setTextFill(Color.RED);
                amountTextField.setStyle("-fx-border-color: red");


            }
        }



    }



    private double getValidDoubleAmount() {
        try {
            double amount = Double.parseDouble(amountTextField.getText());
            if (amount <= 0) {
                throw new IllegalArgumentException("Сума має бути додатнім числом");
            } else if (String.valueOf(amount).contains(".")) {
                String[] parts = String.valueOf(amount).split("\\.");
                if (parts[1].length() > 2) {
                    throw new IllegalArgumentException("Дозволено тільки 2 знаки після коми");
                }
            }

            amountLabel.setText("Enter expense amount:");
            amountLabel.setTextFill(Color.BLACK);
            descriptionTextField.setText("");
            amountTextField.setText("");
            amountTextField.setStyle(null);
            return amount;
        } catch (NumberFormatException e) {
            handleErrorMessage("Сума має бути числом");
        } catch (IllegalArgumentException e) {
            handleErrorMessage(e.getMessage());
        }
        return 0.0;
    }

    private void handleErrorMessage(String message) {
        amountLabel.setText(message);
        amountLabel.setTextFill(Color.RED);
        amountTextField.setText("");
        amountTextField.setStyle("-fx-border-color: red");
    }


}
