package com.pages;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.baseclass.BaseTest;
import com.baseclass.baseMethods;
import com.recipe.LFV_Add;
import com.recipe.Receipedata;
import com.utilities.DatabaseUtils;
import com.utilities.ExcelReader;
import com.utilities.PropertyFileReader;

public class RecipePages {

	private WebDriver driver;
	private List<String> excellchfAddIngredients;
	private List<String> excellchfEliminateIngredients;
	private List<String> excellchfFoodProcessingIngredients;
   Receipedata dto = new Receipedata(); 
	boolean recipeIDExists = false;

	// private static final Logger logger =
	// Logger.getLogger(Recipes_LCHFPage.class.getName());
	private List<String> excelAllergyIngredients;

	List<String> columnNamesAdd = Collections.singletonList("Add");
	List<String> columnNamesEliminate_LCHF = Collections.singletonList("Eliminate");
	List<String> columnNamesFoodProcessing = Collections.singletonList("Food Processing");
	List<String> columnNamesAllergy = Collections.singletonList("Eliminate"); 
	
	private List<String> excelVeganIngredients;
	private List<String> excelNotFullyVeganIngredients;
	private List<String> excelEliminateIngredients = new ArrayList<>();
	private String recipeName;
	String alphabetPageTitle = "";
	List<String> unmatchedLFVIngredients;

	private List<String> excelRecipeToAvoidList;
	private static final Object lock = new Object();

	List<String> columnNamesVegan = Collections.singletonList("Add");
	List<String> columnNamesNotFullyVegan = Collections.singletonList("To Add ( if not fully vegan)");
	List<String> columnNamesEliminate_LFV = Collections.singletonList("Eliminate");
	List<String> columnNamesRecipeToAvoid = Collections.singletonList("Recipes to avoid");

	baseMethods basemethods = new baseMethods();

	public void readExcelLFV_LCHF() throws Throwable {
		String userDir = System.getProperty("user.dir");
		String getPathread = PropertyFileReader.getGlobalValue("inputExcelPath");
		String inputDataPath = userDir + getPathread;

		try {
			excellchfAddIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ", columnNamesAdd,
					inputDataPath);
			excellchfEliminateIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ",
					columnNamesEliminate_LCHF, inputDataPath);
			excellchfFoodProcessingIngredients = ExcelReader.getDataFromExcel("Final list for LCHFElimination ",
					columnNamesFoodProcessing, inputDataPath);
			// logger.info("LCHF Add: " + excellchfAddIngredients);
			// logger.info("LCHF Eliminate: " + excellchfEliminateIngredients);
			// logger.info("LCHF Food Processing: " + excellchfFoodProcessingIngredients);

			excelVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ", columnNamesVegan,
					inputDataPath);
			excelNotFullyVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesNotFullyVegan, inputDataPath);
			excelEliminateIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesEliminate_LFV, inputDataPath);
			excelRecipeToAvoidList = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesRecipeToAvoid, inputDataPath);
			excelAllergyIngredients = ExcelReader.getDataFromExcel("Final List for Allergies", columnNamesAllergy,
					inputDataPath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void extractDataFromPages(WebDriver driver) throws Throwable {
		this.driver = driver;
		
		extractRecipes();
	}

	private void processRecipe(int index) throws Throwable {
		
		System.out.println("Inside the Process recipe pages");
		List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
		

		if (index < recipeCards.size()) {
			WebElement recipeCard = recipeCards.get(index);

			// Getting recipe id
			String recipeID = recipeCard.getAttribute("id");
			String id = recipeID.replaceAll("[^0-9]", "");
			

			// Getting recipe name
			WebElement recipeNameElement = recipeCard.findElement(By.xpath(".//span[@class='rcc_recipename']/a"));
			recipeName = recipeNameElement.getText();
			System.out.println("Recipe Name: " + recipeName);

			// Clicking into the recipe link
			recipeNameElement.click();
			dto.setRecipe_ID(id);
			dto.setRecipe_Name(recipeName);
			dto=extractRecipeDetails();		
			

			// logger.info("Processing recipe: " + recipeName + " (ID: " + recipeID + ")")

			DatabaseUtils.initializeDBConnection();
			// logger.info("Database connection established successfully.");

			DatabaseUtils.createTable("LCHFAdd");
			DatabaseUtils.createTable("LCHFEliminate");
			DatabaseUtils.createTable("LCHFFoodProcessing");
			DatabaseUtils.createTable("LCHFAllergy");
			DatabaseUtils.createTable("LFV_Elimination");
			DatabaseUtils.createTable("LFV_Add");
			DatabaseUtils.createTable("LFV_NotFullyVegan");
			DatabaseUtils.createTable("LFV_ReceipesToAvoid");
			DatabaseUtils.createTable("LFV_Allergies");
			

			List<String> webIngredients = extractIngredients();

			boolean lchfEliminate = basemethods.eliminateRecipe(excellchfEliminateIngredients, webIngredients);

			boolean lchfAdd = basemethods.addIngredients(excellchfAddIngredients, webIngredients);

			boolean lchfFoodProcessing = basemethods.addIngredients(excellchfFoodProcessingIngredients, webIngredients);
			
			boolean lchfAllergy=basemethods.eliminateRecipe(excelAllergyIngredients, webIngredients);
			
			
			boolean LFVEliminate = basemethods.eliminateRecipe(excelEliminateIngredients, webIngredients);

			boolean LFVAdd = basemethods.addIngredients(excelVeganIngredients, webIngredients);

			boolean LFVnotFullyVegan = basemethods.addIngredients(excelNotFullyVeganIngredients, webIngredients);

			boolean LFVreceipesToavoid = basemethods.eliminateRecipe(excelRecipeToAvoidList, webIngredients);
			
			boolean LFVAllergy=basemethods.eliminateRecipe(excelAllergyIngredients, webIngredients);
			

			saveToDatabasemethodsWithChecks_LCHF(dto, recipeID, recipeName, lchfEliminate, lchfAdd, lchfFoodProcessing,lchfAllergy,
					webIngredients);

			saveToDatabasemethodsWithChecks_LFV(dto, recipeID, recipeName, LFVEliminate, LFVAdd, LFVnotFullyVegan,LFVreceipesToavoid,LFVAllergy, webIngredients);

			retryNavigation();

			DatabaseUtils.closeDBConnection();
		}

	}
	
	

	public void saveToDatabasemethodsWithChecks_LFV(Receipedata dto, String recipeID, String recipeName,
			boolean LFVEliminate, boolean LFVAdd, boolean LFVnotFullyVegan, boolean LFVreceipesToavoid,boolean LFVAllergy,
			List<String> webIngredients) throws Throwable {
		try {
			synchronized (lock) {
				
				if (LFVEliminate ) {
					
					saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LFV_Elimination", "LFV_Elimination",
							webIngredients);
				}
				if ( LFVEliminate) {
					if (LFVAdd) {
						saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LFV_Add", "LFV_Add", webIngredients);
					}
				}
				if ( LFVEliminate) {
					if (LFVAdd) {
						if (LFVnotFullyVegan) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LFV_NotFullyVegan",
									"LFV_NotFullyVegan", webIngredients);
						}
					}
				}
				if ( LFVEliminate) {
					if (LFVAdd) {
						if (LFVreceipesToavoid) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LFV_ReceipesToAvoid",
									"LFV_ReceipesToAvoid", webIngredients);
						}
					}
				}
				if (LFVEliminate)  {
					if(LFVAdd) {
						if(LFVAllergy) {					
						
				    saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LFV_Allergies", 
				                                "LFV_Allergies", webIngredients);
				}
				}
				}
			}
		} catch (SQLException e) {
			if (e.getMessage().contains("duplicate key value violates unique constraint")) {
				// logger.warning("Duplicate recipe_id found: " + recipeID + ". Skipping to next
				// recipe.");
			} else {
				throw e;
			}
		}

	}

	private void saveToDatabasemethodsWithChecks_LCHF(Receipedata dto, String recipeID, String recipeName,
			boolean lchfEliminate, boolean lchfAdd, boolean lchfFoodProcessing,boolean Allergy, List<String> webIngredients)
			throws SQLException {
		try {
			synchronized (lock) {
				if (lchfEliminate ) {
					saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFEliminate", "LCHFEliminate",
							webIngredients);
				}
				if (lchfEliminate) {
					if (lchfAdd) {
						saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFAdd", "LCHFAdd", webIngredients);
					}
				}
				if (lchfEliminate) {
					if (lchfAdd) {
						if (lchfFoodProcessing) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFFoodProcessing",
									"LCHFFoodProcessing", webIngredients);
						}
					}
				}
				if (lchfEliminate) {
					if(lchfAdd) {
						if(Allergy) {
				    saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFAllergy", 
				                                "LCHFAllergy", webIngredients);
				}
					}
				}
			}
		} catch (SQLException e) {
			if (e.getMessage().contains("duplicate key value violates unique constraint")) {
				// logger.warning("Duplicate recipe_id found: " + recipeID + ". Skipping to next
				// recipe.");
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
		// logger.info("Recipe saved to " + tableName + " table: " + recipeName + "
		// under category: " + category);
	}

	private void retryNavigation() throws Throwable {
		int maxRetries = 3;
		int retryCount = 0;
		while (retryCount < maxRetries) {
			try {
				BaseTest.getDriver().navigate().back();
				BaseTest.getDriver().findElement(By.className("rcc_recipecard")).isDisplayed();
				return; // Navigation successful, exit retry loop
			} catch (NoSuchElementException e) {
				// logger.warning("Navigation failed, retrying... (" + (retryCount + 1) + "/" +
				// maxRetries + ")");
				retryCount++;
			}
		}
		// logger.severe("Failed to navigate back after " + maxRetries + " attempts.");
	}

	private Receipedata extractRecipeDetails() throws Throwable {
		System.out.println("Testing");
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

	private List<String> extractIngredients() throws Throwable {
		List<WebElement> ingredientsList = BaseTest.getDriver()
				// .findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
				.findElements(By.xpath("//div[@id='rcpinglist']"));

		List<String> webIngredients = new ArrayList<>();
		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		return webIngredients;
	}

	private void extractRecipes() throws Throwable {
		System.out.println("Inside the Extract pages");
		int pageIndex = 0;
		while (true) {
			pageIndex++;
			// logger.info("Page Number: " + pageIndex);
			try {
				List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
				// logger.info("No_of_recipes: " + recipeCards.size());
				for (int j = 0; j < recipeCards.size(); j++) {
					System.out.println("recipeCards" + recipeCards.size());
					processRecipe(j);
				}
			} catch (Exception e) {
				// logger.severe("Error while extracting data: " + e.getMessage());
				break;
			}
			if (!navigateToNextPage()) {
				break;
			}
		}
	}

	private boolean navigateToNextPage() throws Throwable {
		try {
			WebElement nextPageIndex = BaseTest.getDriver()
					.findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			System.out.println("No more pages for this alphabet");
			return false;
		}
	}

}
