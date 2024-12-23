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
import org.testng.annotations.BeforeClass;

import com.baseclass.baseMethods;
import com.recipe.LFV_Add;
import com.recipe.Receipedata;
import com.tests.ScrapingRecipes;
import com.utilities.DatabaseUtils;
import com.utilities.ExcelReader;
import com.utilities.PropertyFileReader;

public class RecipePages extends ScrapingRecipes {

	private WebDriver driver;
	private List<String> excellchfAddIngredients;
	private List<String> excellchfEliminateIngredients;
	private List<String> excellchfFoodProcessingIngredients;
	Receipedata dto = new Receipedata();
	List<String> webIngredients = extractIngredients();
	private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
	boolean RecipeIDexists = false;
	private List<String> excelAllergyIngredients = new ArrayList<>();
	List<String> columnNamesAllergy = Collections.singletonList("Allergies (Bonus points)");

	private static final Logger logger = Logger.getLogger(RecipePages.class.getName());

	List<String> columnNamesAdd = Collections.singletonList("Add");
	List<String> columnNamesEliminate_LCHF = Collections.singletonList("Eliminate");
	List<String> columnNamesFoodProcessing = Collections.singletonList("Food Processing");
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

	@BeforeClass
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
			logger.info("LCHF Add: " + excellchfAddIngredients);
			logger.info("LCHF Eliminate: " + excellchfEliminateIngredients);
			logger.info("LCHF Food Processing: " + excellchfFoodProcessingIngredients);

			excelVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ", columnNamesVegan,
					inputDataPath);
			excelNotFullyVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesNotFullyVegan, inputDataPath);
			excelEliminateIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesEliminate_LFV, inputDataPath);
			excelRecipeToAvoidList = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
					columnNamesRecipeToAvoid, inputDataPath);
			// excelRecipeToAvoidList = ExcelReader.getDataFromExcel("Allergies",
			// columnNameAllergies,
			// inputDataPath);
			System.out.println("Recipe to Avoid List: " + excelRecipeToAvoidList);

			System.out.println("Add Ingredients List: " + excelVeganIngredients);
			System.out.println("Not Fully Vegan Ingredients List: " + excelNotFullyVeganIngredients);
			System.out.println("Eliminate Ingredients List: " + excelEliminateIngredients);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void extractDataFromPages(WebDriver driver) throws Throwable {
		this.driver = driver;
		extractRecipes();
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

		dto.setRecipe_ID(recipeID);
		dto.setRecipe_Name(recipeName);

		Session session = sessionFactory.openSession();

		dto = session.find(Receipedata.class, recipeID);

		System.out.println("ExistigReceipe Obj:" + dto);

		if (null != dto) {
			System.out.println("Receipe ID :" + recipeID + " already exists in DB");
			RecipeIDexists = true;
		} else {
			System.out.println("Receipe ID :" + recipeID + " NOT exists in DB");
			dto = new Receipedata();
		}

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

		// ************************************************** LFV Scraping
		// ***************************************//

		try {

			// Clicking into the recipe link

			Receipedata DTO = new Receipedata();

			DTO.setRecipe_ID(recipeID);
			DTO.setRecipe_Name(recipeName);
			DTO.setRecipe_Category(basemethods.getRecipeCategory(DTO));
			DTO.setTag(basemethods.getTags(DTO));
			DTO.setFood_Category(basemethods.getFoodCategory(DTO));
			DTO.setCuisine_category(basemethods.getcuisineCategory(DTO));
			DTO.setPreparation_Time(basemethods.getPreparationTime(DTO));
			DTO.setPreparation_method(basemethods.getPreparationMethod(DTO));
			DTO.setCooking_Time(basemethods.getCookingTime(DTO));
			DTO.setNutrient_values(basemethods.getNutrientValues(DTO));
			DTO.setNo_of_servings(basemethods.getNoOfServings(DTO));
			DTO.setRecipe_Description(basemethods.getRecipeDescription(DTO));
			DTO.setRecipe_URL(basemethods.getRecipeURL(DTO));

			Session session1 = sessionFactory.openSession();

			Receipedata receipe = new Receipedata();

			receipe = session1.find(Receipedata.class, recipeID);

			boolean RecipeIDexists = false;
			System.out.println("ExistigReceipe Obj:" + receipe);

			if (null != receipe) {
				System.out.println("Receipe ID :" + recipeID + " already exists in DB");
				RecipeIDexists = true;
			} else {
				System.out.println("Receipe ID :" + recipeID + " NOT exists in DB");
				receipe = new Receipedata();
			}

			boolean LFVEliminate = basemethods.eliminateRecipe(excelEliminateIngredients, webIngredients);

			boolean LFVAdd = basemethods.addIngredients(excelVeganIngredients, webIngredients);

			boolean LFVnotFullyVegan = basemethods.addIngredients(excelNotFullyVeganIngredients, webIngredients);

			boolean LFVreceipesToavoid = basemethods.eliminateRecipe(excelRecipeToAvoidList, webIngredients);
			
			boolean LFVAllergy=basemethods.eliminateRecipe(excelAllergyIngredients, webIngredients);

			if (LFVEliminate && !RecipeIDexists) {
				synchronized (lock) {
					DTO.setIngredients(String.join(",", webIngredients));
					// System.out.println("LFV Eliminate :: Receipe Object brfore Save:" + receipe);
					session.beginTransaction();
					session.save(DTO);
					session.getTransaction().commit();
					session.close();
				}
			}

			if (!RecipeIDexists && LFVEliminate) {
				if (LFVAdd) {

					LFV_Add addObj = new LFV_Add();
					// Coping the values from DTO for LVF Add table
					addObj = basemethods.copyData(DTO, addObj);
					synchronized (lock) {
						DTO.setIngredients(String.join(",", webIngredients));
						Session addSession = sessionFactory.openSession();
						addSession.beginTransaction();
						addSession.save(addObj);
						addSession.getTransaction().commit();
						addSession.close();
					}
				}

			}

			if (!RecipeIDexists && LFVEliminate) {
				if (LFVAdd) {
					if (LFVnotFullyVegan) {

						try {

							synchronized (lock) {
								StringBuilder sql2 = new StringBuilder();
								Connection conn = DatabaseUtils.getConnection();
								sql2.append(
										"INSERT INTO LFV_NotFullyVegan (recipe_id, recipe_name, recipe_category, food_category, cuisine_category,"
												+ "preparation_time, cooking_time, recipe_tags, servings, description, preparation_method, "
												+ "nutrient_values, ingredients, recipe_url) VALUES (")
										.append(DTO.getRecipe_ID()).append(DTO.getRecipe_Name())
										.append(DTO.getRecipe_Category()).append(DTO.getFood_Category())
										.append(DTO.getCuisine_category()).append(DTO.getPreparation_Time())
										.append(DTO.getCooking_Time()).append(DTO.getTag())
										.append(DTO.getNo_of_servings()).append(DTO.getRecipe_Description())
										.append(DTO.getPreparation_method()).append(DTO.getNutrient_values())
										.append(String.join(",", webIngredients)).append(DTO.getRecipe_URL())
										.append(")");
								PreparedStatement pstmt = conn.prepareStatement(sql2.toString());
								pstmt.executeUpdate();
							}
						} catch (Exception e) {
							System.out.println("Error writing to DB LFV_NotFullyVegan: " + e.getMessage());
						}
					}
				}
			}
			
			if (!RecipeIDexists && LFVEliminate ) {
				if(LFVAdd) {
					if(LFVAllergy) {		
				try {
					
					synchronized (lock) {
						StringBuilder sql2 = new StringBuilder();
						Connection conn = DatabaseUtils.getConnection();
						sql2.append(
								"INSERT INTO LFV_Allergy (recipe_id, recipe_name, recipe_category, food_category, cuisine_category,"
								+ "preparation_time, cooking_time, recipe_tags, servings, description, preparation_method, "
								+ "nutrient_values, ingredients, recipe_url) VALUES (")
								.append(DTO.getRecipe_ID()).append(DTO.getRecipe_Name())
								.append(DTO.getRecipe_Category()).append(DTO.getFood_Category())
								.append(DTO.getCuisine_category()).append(DTO.getPreparation_Time())
								.append(DTO.getCooking_Time()).append(DTO.getTag()).append(DTO.getNo_of_servings())
								.append(DTO.getRecipe_Description()).append(DTO.getPreparation_method())
								.append(DTO.getNutrient_values()).append(String.join(",", webIngredients))
								.append(DTO.getRecipe_URL()).append(")");
						PreparedStatement pstmt = conn.prepareStatement(sql2.toString());
						pstmt.executeUpdate();
					}
				} catch (Exception e) {
					System.out.println("Error writing to DB LFV_Allergy: " + e.getMessage());
				}
			}
				}
			}
			

			if (!RecipeIDexists && LFVEliminate) {
				if (LFVAdd) {
					if (LFVreceipesToavoid) {

						StringBuilder sql = new StringBuilder();
						try {
							synchronized (lock) {
								Connection conn = DatabaseUtils.getConnection();
								sql.append(
										"INSERT INTO LFV_ReceipesToAvoid (recipe_id, recipe_name, recipe_category, food_category, cuisine_category,preparation_time, cooking_time, recipe_tags, servings, description, preparation_method, nutrient_values, ingredients, recipe_url) VALUES (")
										.append(DTO.getRecipe_ID()).append(DTO.getRecipe_Name())
										.append(DTO.getRecipe_Category()).append(DTO.getFood_Category())
										.append(DTO.getCuisine_category()).append(DTO.getPreparation_Time())
										.append(DTO.getCooking_Time()).append(DTO.getTag())
										.append(DTO.getNo_of_servings()).append(DTO.getRecipe_Description())
										.append(DTO.getPreparation_method()).append(DTO.getNutrient_values())
										.append(DTO.getIngredients()).append(DTO.getRecipe_URL());
								PreparedStatement pstmt = conn.prepareStatement(sql.toString());
								pstmt.executeUpdate();
							}
						} catch (Exception e) {
							System.out.println("Error writing to DB LFV_ReceipesToAvoid: " + e.getMessage());
						}
					}
				}
			}

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
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index " + index + " out of bounds for recipe cards");
		} catch (Exception e) {
			System.out.println("Error in processRecipe: " + e.getMessage());
		}

	}

	private void saveToDatabasemethodsWithChecks(Receipedata dto, String recipeID, String recipeName,
			boolean lchfEliminate, boolean lchfAdd, boolean lchfFoodProcessing,boolean lchfAllergies, List<String> webIngredients)
			throws SQLException {
		try {
			synchronized (lock) {
				if (lchfEliminate && !RecipeIDexists) {
					saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFEliminate", "LCHFEliminate",
							webIngredients);
				}
				if (!RecipeIDexists && lchfEliminate) {
					if (lchfAdd) {
						saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFAdd", "LCHFAdd", webIngredients);
					}
				}
				if (!RecipeIDexists && lchfEliminate) {
					if (lchfAdd) {
						if (lchfFoodProcessing) {
							saveRecipeToDatabasemethods(dto, recipeID, recipeName, "LCHFFoodProcessing",
									"LCHFFoodProcessing", webIngredients);
						}
					}
				}
				if (!RecipeIDexists && lchfEliminate) {
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
