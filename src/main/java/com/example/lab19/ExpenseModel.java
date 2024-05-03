package com.example.lab19;


import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ExpenseModel   {



    private SimpleIntegerProperty id;
    private SimpleStringProperty description;
    private SimpleDoubleProperty amount;



    public final int getId() {
        return id.get();
    }

    public final void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }



    public final String getDescription() {
        return description.get();
    }

    public final void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }



    public final double getAmount() {
        return amount.get();
    }

    public final void setAmount(double amount) {
        this.amount = new SimpleDoubleProperty(amount);
    }



    public ExpenseModel(int id, String description, double amount) {
        this.id = new SimpleIntegerProperty(id);
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
    }





}
