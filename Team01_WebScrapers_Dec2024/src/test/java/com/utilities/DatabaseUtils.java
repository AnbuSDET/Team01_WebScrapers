package com.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;


public class DatabaseUtils {
	private static Connection dbConnection;

	private static final Logger logger = Logger.getLogger(DatabaseUtils.class.getName());

	public static void initializeDBConnection() throws Throwable {
		String dbUrl = PropertyFileReader.getGlobalValue("dbUrl");
		String dbUsername = PropertyFileReader.getGlobalValue("dbUsername");
		String dbPassword = PropertyFileReader.getGlobalValue("dbPassword");
		dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		
	}

	public static void createTable(String tableName) {
		String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + "recipe_id VARCHAR(50), "
				+ "recipe_name VARCHAR(255), " + "recipe_category VARCHAR(100), " + "food_category VARCHAR(100), "
				+ "ingredients TEXT, " + "preparation_time VARCHAR(50), " + "cooking_time VARCHAR(50), "
				+ "recipe_tags TEXT, " + "no_of_servings VARCHAR(50), " + "cuisine_category VARCHAR(100), "
				+ "recipe_description TEXT, " + "preparation_method TEXT, " + "nutrient_values TEXT, "
				+ "recipe_url VARCHAR(500))";
		try (PreparedStatement pstmt = dbConnection.prepareStatement(createTableSQL)) {
			pstmt.execute();
			logger.info("Table `" + tableName + "` ensured to exist.");
		} catch (SQLException e) {
			logger.severe("Error creating table: " + e.getMessage());
		}
	}

	public static void insertIntoTable(String tableName, String recipe_id, String recipeName, String recipeCategory,
			String foodCategory, String ingredients, String preparationTime, String cookingTime, String recipeTags,
			String noOfServings, String cuisineCategory, String recipeDescription, String preparationMethod,
			String nutrientValues, String recipeUrl) throws SQLException {

		String insertQuery = "INSERT INTO " + tableName
				+ " (recipe_id, recipe_name, recipe_category, food_category, ingredients, preparation_time, cooking_time, "
				+ "recipe_tags, no_of_servings, cuisine_category, recipe_description, preparation_method, nutrient_values, recipe_url) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = dbConnection.prepareStatement(insertQuery)) {
			statement.setString(1, recipe_id);
			statement.setString(2, recipeName);
			statement.setString(3, recipeCategory);
			statement.setString(4, foodCategory);
			statement.setString(5, ingredients);
			statement.setString(6, preparationTime);
			statement.setString(7, cookingTime);
			statement.setString(8, recipeTags);
			statement.setString(9, noOfServings);
			statement.setString(10, cuisineCategory);
			statement.setString(11, recipeDescription);
			statement.setString(12, preparationMethod);
			statement.setString(13, nutrientValues);
			statement.setString(14, recipeUrl);
			statement.executeUpdate();
		} catch (SQLException e) {
			logger.severe("Error inserting into table: " + e.getMessage());
			throw e; // Rethrow the exception after logging it
		}
	}

	public static void closeDBConnection() {
		if (dbConnection != null) {
			try {
				dbConnection.close();
				logger.info("Database connection closed.");
			} catch (SQLException e) {
				logger.severe("Error closing database connection: " + e.getMessage());
			}
		}
	}

}
