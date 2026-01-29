package com.rahaga;

import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double price;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;
    private List<DishIngredient> dishIngredientList;

    public List<DishIngredient> getDishIngredientList() {
        return dishIngredientList;
    }

    public void setDishIngredientList(List<DishIngredient> dishIngredientList) {
        this.dishIngredientList = dishIngredientList;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDishCost() {
        double totalPrice = 0;
        List<Ingredient> ingredients = getIngredients();
        for (Ingredient ingredient : ingredients) {
            DishIngredient relation = getDishIngredientList().stream()
                    .filter(dl -> dl.getId_ingredient() == ingredient.getId())
                    .findFirst().orElse(null);
            if (relation == null) {
                throw new RuntimeException("Ingredient not found");
            }
            double cost = ingredient.getPrice() * relation.getQuantity_required();
            totalPrice += cost;
        }
        return totalPrice;
    }

    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients,Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.price = price;
    }
    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, List<DishIngredient> dishIngredientList) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.dishIngredientList = dishIngredientList;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            this.ingredients = null;
            return;
        }
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", ingredients=" + ingredients +
                '}';
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Price is null");
        }
        return price - getDishCost();
    }
}