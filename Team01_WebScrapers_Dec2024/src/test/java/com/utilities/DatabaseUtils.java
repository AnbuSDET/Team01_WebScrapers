package com.utilities;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils {

    private static final String URL = "jdbc:postgresql://localhost:5432/recipe_db"; 
    private static final String USER = "postgres"; 
    private static final String PASSWORD = ""; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertRecipeData(String recipeID, String recipeName, String recipeCategory,
                                        String foodCategory, String cuisineCategory, String preparationTime,
                                        String cookingTime, String recipeTags, String servings,
                                        String description, String preparationMethod, String nutrientValues,
                                        String ingredients, String recipeUrl) {
        String sql = "INSERT INTO recipes (recipe_id, recipe_name, recipe_category, food_category, cuisine_category, " +
                     "preparation_time, cooking_time, recipe_tags, servings, description, preparation_method, nutrient_values, " +
                     "ingredients, recipe_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipeID);
            pstmt.setString(2, recipeName);
            pstmt.setString(3, recipeCategory);
            pstmt.setString(4, foodCategory);
            pstmt.setString(5, cuisineCategory);
            pstmt.setString(6, preparationTime);
            pstmt.setString(7, cookingTime);
            pstmt.setString(8, recipeTags);
            pstmt.setString(9, servings);
            pstmt.setString(10, description);
            pstmt.setString(11, preparationMethod);
            pstmt.setString(12, nutrientValues);
            pstmt.setString(13, ingredients);
            pstmt.setString(14, recipeUrl);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
