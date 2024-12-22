package com.pages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;

import com.baseclass.baseMethods;
import com.recipe.Receipedata;


import com.utilities.DatabaseUtils;
import com.utilities.ExcelReader;
import com.utilities.PropertyFileReader;


public class Recipes_LCHFPage {

	private WebDriver driver;
	private List<String> excellchfAddIngredients;
	private List<String> excellchfEliminateIngredients;
	private List<String> excellchfFoodProcessingIngredients;
	Receipedata dto = new Receipedata();

	private static final Logger logger = Logger.getLogger(Recipes_LCHFPage.class.getName());

	List<String> columnNamesAdd = Collections.singletonList("Add");
	List<String> columnNamesEliminate = Collections.singletonList("Eliminate");
	List<String> columnNamesFoodProcessing = Collections.singletonList("Food Processing");

	baseMethods base=new baseMethods();
	@BeforeClass
	public void readExcel() throws Throwable {
		String userDir = System.getProperty("user.dir");
		String getPathread = PropertyFileReader.getGlobalValue("inputExcelPath");
		String inputDataPath = userDir + getPathread;

		try {
			excellchfAddIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ", columnNamesAdd,
					inputDataPath);
			excellchfEliminateIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ",
					columnNamesEliminate, inputDataPath);
			excellchfFoodProcessingIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ",
					columnNamesFoodProcessing, inputDataPath);
			logger.info("LCHF Add: " + excellchfAddIngredients);
			logger.info("LCHF Eliminate: " + excellchfEliminateIngredients);
			logger.info("LCHF Food Processing: " + excellchfFoodProcessingIngredients);
		} catch (IOException e) {
			logger.severe("Error reading Excel: " + e.getMessage());
		}
	}

	public void extractDataFromPages(WebDriver driver, String alphabetPageTitle) throws Throwable {
		this.driver = driver;
		extractRecipes();
	}

	private void extractRecipes() throws Throwable {
		int pageIndex = 0;
		while (true) {
			pageIndex++;
			logger.info("Page Number: " + pageIndex);
			try {
				List<WebElement> recipeCards = driver.findElements(By.className("rcc_recipecard"));
				logger.info("No_of_recipes: " + recipeCards.size());
				for (int j = 0; j < recipeCards.size(); j++) {
					processRecipe(j);
				}
			} catch (Exception e) {
				logger.severe("Error while extracting data: " + e.getMessage());
				break;
			}
			if (!navigateToNextPage()) {
				break;
			}
		}
	}

	private void processRecipe(int index) throws Throwable {
		List<WebElement> recipeCards = driver.findElements(By.className("rcc_recipecard"));
		logger.info("Number of recipe cards found: " + recipeCards.size());

		if (index < 0 || index >= recipeCards.size()) {
			logger.warning("Index out of bounds: " + index + " for recipe list of size " + recipeCards.size());
			return;
		}

		WebElement recipeCard = recipeCards.get(index);
		String recipeID = recipeCard.getAttribute("id").replaceAll("[^0-9]", "");
		WebElement recipeNameElement = recipeCard.findElement(By.xpath(".//span[@class='rcc_recipename']/a"));
		String recipeName = recipeNameElement.getText();

		if (recipeName == null || recipeName.trim().isEmpty()) {
			logger.warning("Recipe name is null or empty for index: " + index);
			return;
		}
		 dto.setRecipe_Name(recipeName);
		    logger.info("Processing recipe: " + recipeName + " (ID: " + recipeID + ")");

		try {
			// Initialize database connection
			DatabaseUtils.initializeDBConnection();
			logger.info("Database connection established successfully.");

			// Ensure tables exist
			DatabaseUtils.createTable("LCHFAdd");
			DatabaseUtils.createTable("LCHFEliminate");
			DatabaseUtils.createTable("LCHFFoodProcessing");

			// Navigate to recipe details page
			recipeNameElement.click();

			// Extract recipe details
			dto = extractRecipeDetails();

			// Extract ingredients and match
			List<String> webIngredients = extractIngredients();
			List<String> matchedLchfAddIngredients = matchIngredientsWithExcel(excellchfAddIngredients, webIngredients);
			List<String> unmatchedLchfIngredients = getUnmatchedIngredients(excellchfEliminateIngredients,
					webIngredients);
			List<String> matchedLchfFoodProcessing = matchWithTag(excellchfFoodProcessingIngredients);

			// Attempt to save to database with duplicate check
			saveToDatabaseWithChecks(dto, recipeID, recipeName, matchedLchfAddIngredients, unmatchedLchfIngredients,
					matchedLchfFoodProcessing);

			// Retry navigation in case of failure
			retryNavigation();

		} catch (Exception e) {
			logger.severe("Error in processRecipe: " + e.getMessage());
		} finally {
			DatabaseUtils.closeDBConnection(); // Ensure database connection is closed
		}
	}

	private void saveToDatabaseWithChecks(Receipedata dto, String recipeID, String recipeName,
			List<String> matchedLchfAddIngredients, List<String> unmatchedLchfIngredients,
			List<String> matchedLchfFoodProcessing) throws SQLException {
		try {
			if (!matchedLchfAddIngredients.isEmpty()) {
				saveRecipeToDatabase(dto, recipeID, recipeName, matchedLchfAddIngredients, "LCHFAdd");
			}
			if (!unmatchedLchfIngredients.isEmpty()) {
				saveRecipeToDatabase(dto, recipeID, recipeName, unmatchedLchfIngredients, "LCHFEliminate");
			}
			if (!matchedLchfFoodProcessing.isEmpty()) {
				saveRecipeToDatabase(dto, recipeID, recipeName, matchedLchfFoodProcessing, "LCHFFoodProcessing");
			}
		} catch (SQLException e) {
			if (e.getMessage().contains("duplicate key value violates unique constraint")) {
				logger.warning("Duplicate recipe_id found: " + recipeID + ". Skipping to next recipe.");
			} else {
				throw e; // Rethrow unexpected exceptions
			}
		}
	}

	private void saveRecipeToDatabase(Receipedata dto2, String recipeID, String recipeName, List<String> ingredients,
			String tableName) throws SQLException {
		DatabaseUtils.insertIntoTable(tableName, recipeID, recipeName, dto2.getRecipe_Category(), dto2.getFood_Category(),
				String.join(", ", ingredients), dto2.getPreparation_Time(), dto2.getCooking_Time(), dto2.getTag(),
				dto2.getNo_of_servings(), dto2.getCuisine_category(), dto2.getRecipe_Description(),
				dto2.getPreparation_method(), dto2.getNutrient_values(), driver.getCurrentUrl());
		logger.info("Recipe saved to " + tableName + " table: " + recipeName);
	}

	private void retryNavigation() {
		int maxRetries = 3;
		int retryCount = 0;
		while (retryCount < maxRetries) {
			try {
				driver.navigate().back();
				driver.findElement(By.className("rcc_recipecard")).isDisplayed();
				return; // Navigation successful, exit retry loop
			} catch (NoSuchElementException e) {
				logger.warning("Navigation failed, retrying... (" + (retryCount + 1) + "/" + maxRetries + ")");
				retryCount++;
			}
		}
		logger.severe("Failed to navigate back after " + maxRetries + " attempts.");
	}

	private Receipedata extractRecipeDetails() throws Throwable {
		dto.setRecipe_Category(base.getRecipeCategory(dto));
		dto.setTag(base.getTags(dto));
		dto.setFood_Category(base.getFoodCategory(dto));
		dto.setCuisine_category(base.getcuisineCategory(dto));
		dto.setPreparation_Time(base.getPreparationTime(dto));
		dto.setCooking_Time(base.getCookingTime(dto));
		dto.setPreparation_method(base.getPreparationMethod(dto));
		dto.setNutrient_values(base.getNutrientValues(dto));
		dto.setNo_of_servings(base.getNoOfServings(dto));
		dto.setRecipe_Description(base.getRecipeDescription(dto));
		return dto;
	}

	private List<String> extractIngredients() {
		List<WebElement> ingredientsList = driver
				.findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();
		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		return webIngredients;
	}

	private List<String> matchIngredientsWithExcel(List<String> excelIngredients, List<String> webIngredients) {
		List<String> matchedIngredients = new ArrayList<>();

		// Match ingredients with Excel ingredients list (partial matches allowed)
		for (String webIngredient : webIngredients) {
			for (String excelIngredient : excelIngredients) {
				if (webIngredient.contains(excelIngredient.toLowerCase())
						|| excelIngredient.toLowerCase().contains(webIngredient)) {
					System.out.println("Ingredient match found: Web Ingredient - " + webIngredient
							+ ", Excel Ingredient - " + excelIngredient);
					matchedIngredients.add(webIngredient);
				}
			}
		}
		return matchedIngredients;
	}

	private boolean navigateToNextPage() {
		try {
			WebElement nextPageIndex = driver.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			System.out.println("No more pages for this alphabet");
			return false;
		}
	}

	public List<String> matchWithTag(List<String> excelIngredients) {
		List<String> matchedIngredients = new ArrayList<>();
		String tagText = driver.findElement(By.id("recipe_tags")).getText().toLowerCase();
		String[] tagArray = tagText.split(",\\s*");
		List<String> tags = Arrays.asList(tagArray);
		for (String tag : tags) {
			for (String excelIngredient : excelIngredients) {
				if (normalize(tag).contains(normalize(excelIngredient))
						|| normalize(excelIngredient).contains(normalize(tag))) {
					System.out.println("Match found: " + excelIngredient + " in tags.");
					matchedIngredients.add(excelIngredient);
				}
			}
		}
		return matchedIngredients;
	}

	private String normalize(String text) {

		return text.toLowerCase().trim();
	}

	private List<String> getUnmatchedIngredients(List<String> excelIngredients, List<String> webIngredients) {
		Set<String> excelSet = new HashSet<>(excelIngredients);
		List<String> unmatchedIngredients = new ArrayList<>();

		for (String webIngredient : webIngredients) {
			boolean found = false;
			for (String excelIngredient : excelSet) {
				if (webIngredient.toLowerCase().contains(excelIngredient.toLowerCase())) {
					found = true;
					break;
				}
			}
			if (!found) {
				unmatchedIngredients.add(webIngredient);
			}
		}
		return unmatchedIngredients;
	}

}