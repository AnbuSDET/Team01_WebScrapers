package com.pages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.List;

import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;

import com.baseclass.BaseTest;
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
	private List<String> excelAllergyIngredients = new ArrayList<>();
	private static final Object lock = new Object();
	Receipedata dto = new Receipedata();
	boolean recipeIDExists = false;

	private static final Logger logger = Logger.getLogger(Recipes_LCHFPage.class.getName());

	List<String> columnNamesAdd = Collections.singletonList("Add");
	List<String> columnNamesEliminate = Collections.singletonList("Eliminate");
	List<String> columnNamesFoodProcessing = Collections.singletonList("Food Processing");
	List<String> columnNamesAllergy = Collections.singletonList("Allergies (Bonus points)");

	baseMethods basemethods = new baseMethods();

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
				List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
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
		List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
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

		dto.setRecipe_ID(recipeID);
		dto.setRecipe_Name(recipeName);

		logger.info("Processing recipe: " + recipeName + " (ID: " + recipeID + ")");

		try {
			DatabaseUtils.initializeDBConnection();
			logger.info("Database connection established successfully.");

			DatabaseUtils.createTable("LCHFAdd");
			DatabaseUtils.createTable("LCHFEliminate");
			DatabaseUtils.createTable("LCHFFoodProcessing");
			DatabaseUtils.createTable("LCHFAllergy");

			recipeNameElement.click();

			dto = extractRecipeDetails();
			List<String> webIngredients = extractIngredients();

			boolean lchfEliminate = basemethods.eliminateRecipe(excellchfEliminateIngredients, webIngredients);
			boolean lchfAdd = basemethods.addIngredients(excellchfAddIngredients, webIngredients);
			boolean lchfFoodProcessing = basemethods.addIngredients(excellchfFoodProcessingIngredients, webIngredients);
			boolean lchfAllergies = basemethods.eliminateRecipe(excelAllergyIngredients, webIngredients);

			saveToDatabasemethodsWithChecks(dto, recipeID, recipeName, lchfEliminate, lchfAdd, lchfFoodProcessing,
					lchfAllergies, webIngredients);

			retryNavigation();

		} catch (Exception e) {
			logger.severe("Error in processRecipe: " + e.getMessage());
		} finally {
			DatabaseUtils.closeDBConnection();
		}
	}

	private void saveToDatabasemethodsWithChecks(Receipedata dto, String recipeID, String recipeName,
			boolean lchfEliminate, boolean lchfAdd, boolean lchfFoodProcessing, boolean lchfAllergies,
			List<String> webIngredients) throws SQLException {
		try {
			synchronized (lock) {
				if (lchfEliminate && !recipeIDExists) {
					saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFEliminate", "LCHFEliminate",
							webIngredients);
				}
				if (!recipeIDExists && lchfEliminate) {
					if (lchfAdd) {
						saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFAdd", "LCHFAdd", webIngredients);
					}
				}
				if (!recipeIDExists && lchfEliminate) {
					if (lchfAdd) {
						if (lchfFoodProcessing) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFFoodProcessing",
									"LCHFFoodProcessing", webIngredients);
						}
					}
				}
				if (!recipeIDExists && lchfEliminate) {
					if (lchfAdd) {
						if (lchfAllergies) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFAllergies", "LCHFAllergies",
									webIngredients);
						}
					}
				}
			}
		} catch (SQLException e) {
			if (e.getMessage().contains("duplicate key value violates unique constraint")) {
				logger.warning("Duplicate recipe_id found: " + recipeID + ". Skipping to next recipe.");
			} else {
				throw e;
			}
		}
	}

	private void saveRecipeToDatabasemethods(Receipedata dto, String recipeID, String recipeName, String category,
			String tableName, List<String> webIngredients) throws SQLException {
		DatabaseUtils.insertIntoTable(tableName, recipeID, recipeName, dto.getRecipe_Category(), dto.getFood_Category(),
				String.join(",", webIngredients), dto.getPreparation_Time(), dto.getCooking_Time(), dto.getTag(),
				dto.getNo_of_servings(), dto.getCuisine_category(), dto.getRecipe_Description(),
				dto.getPreparation_method(), dto.getNutrient_values(), dto.getRecipe_URL());
		logger.info("Recipe saved to " + tableName + " table: " + recipeName + " under category: " + category);
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
		dto.setRecipe_Category(basemethods.getRecipeCategory(dto));
		dto.setTag(basemethods.getTags(dto));
		dto.setFood_Category(basemethods.getFoodCategory(dto));
		dto.setCuisine_category(basemethods.getcuisineCategory(dto));
		dto.setPreparation_Time(basemethods.getPreparationTime(dto));
		dto.setCooking_Time(basemethods.getCookingTime(dto));
		dto.setPreparation_method(basemethods.getPreparationMethod(dto));
		dto.setNutrient_values(basemethods.getNutrientValues(dto));
		dto.setNo_of_servings(basemethods.getNoOfServings(dto));
		dto.setRecipe_Description(basemethods.getRecipeDescription(dto));
		dto.setRecipe_URL(basemethods.getRecipeURL(dto));
		return dto;
	}

	private List<String> extractIngredients() {
		List<WebElement> ingredientsList = driver
				// .findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
				.findElements(By.xpath("//div[@id='rcpinglist']"));

		List<String> webIngredients = new ArrayList<>();
		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		return webIngredients;
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

}