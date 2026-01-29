package com.rahaga;

import java.util.Objects;

public class DishIngredient {
    private int id;
    private int id_dish;
    private int id_ingredient;
    private double quantity_required;
    private UnitType unit;

    public DishIngredient(int id, int idDish, int idIngredient, double quantityRequired, UnitType unit) {
        this.id = id;
        id_dish = idDish;
        id_ingredient = idIngredient;
        quantity_required = quantityRequired;
        this.unit = unit;
    }
    public DishIngredient( int idDish, int idIngredient, double quantityRequired, UnitType unit) {
        id_dish = idDish;
        id_ingredient = idIngredient;
        quantity_required = quantityRequired;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_dish() {
        return id_dish;
    }

    public void setId_dish(int id_dish) {
        this.id_dish = id_dish;
    }

    public int getId_ingredient() {
        return id_ingredient;
    }

    public void setId_ingredient(int id_ingredient) {
        this.id_ingredient = id_ingredient;
    }

    public double getQuantity_required() {
        return quantity_required;
    }

    public void setQuantity_required(double quantity_required) {
        this.quantity_required = quantity_required;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", id_dish=" + id_dish +
                ", id_ingredient=" + id_ingredient +
                ", quantity_required=" + quantity_required +
                ", unit=" + unit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return id == that.id && id_dish == that.id_dish && id_ingredient == that.id_ingredient && quantity_required == that.quantity_required && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, id_dish, id_ingredient, quantity_required, unit);
    }
}