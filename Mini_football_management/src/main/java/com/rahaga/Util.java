package main.java.com.rahaga;

import org.td.entity.CategoryEnum;
import org.td.entity.DishTypeEnum;
import org.td.entity.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public DishTypeEnum getDishType(String dishType) {
        return switch (dishType) {
            case "START" -> DishTypeEnum.START;
            case "MAIN" -> DishTypeEnum.MAIN;
            case "DESSERT" -> DishTypeEnum.DESSERT;
            default -> null;
        };
    }

    public CategoryEnum getCategoryType(String categoryType) {
        return switch (categoryType) {
            case "VEGETABLE" -> CategoryEnum.VEGETABLE;
            case "ANIMAL" -> CategoryEnum.ANIMAL;
            case "DAIRY" -> CategoryEnum.DAIRY;
            case "MARI?E" -> CategoryEnum.MARINE;
            case "OTHER" -> CategoryEnum.OTHER;
            default -> null;
        };
    }

    public List<Ingredient> mapResultSetToIngredientList(ResultSet rs) throws SQLException {
        List<Ingredient> ingredientList = new ArrayList<>();
        while(rs.next()) {
            ingredientList.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getDouble("price")
            ));
        }
        return ingredientList;
    }
}