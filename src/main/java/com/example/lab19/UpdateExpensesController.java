package com.example.lab19;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.sql.SQLException;

public class UpdateExpensesController {
    public TextField descriptionTextField;
    public TextField amountTextField;
    public Label descriptionLabel;
    public Label amountLabel;


    private int id;

    public void setId(int id) {
        this.id = id;

    }

    public void setDescriptionTextField(String msg) {
        this.descriptionTextField.setText(msg);
    }

    public void setAmountTextField(double amount) {
        this.amountTextField.setText(String.valueOf(amount));
    }


    public void onUpdateButtonClick(MouseEvent mouseEvent) {

        try {
            double amount = getValidDoubleAmount();
            DatabaseManager.updateExpense(this.id, amount, descriptionTextField.getText());

            amountLabel.setText("Введіть нову суму витрати:");
            amountLabel.setTextFill(Color.BLACK);
            amountTextField.setText("");
            descriptionTextField.setText("");
            amountTextField.setStyle(null);
        } catch (SQLException | IllegalArgumentException ex ) {
            handleErrorMessage(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private double getValidDoubleAmount() throws IllegalArgumentException {
        double amount = Double.parseDouble(amountTextField.getText());
        if (amount <= 0 || String.valueOf(amount).contains(".") && String.valueOf(amount).split("\\.")[1].length() > 2) {
            throw new IllegalArgumentException(amount <= 0 ? "Сума має бути додатнім числом" : "Дозволено тільки 2 знаки після коми");
        }
        return amount;
    }

    private void handleErrorMessage(String message) {
        amountLabel.setText(message);
        amountLabel.setTextFill(Color.RED);
        amountTextField.setText("");
        amountTextField.setStyle("-fx-border-color: red");
    }



}
