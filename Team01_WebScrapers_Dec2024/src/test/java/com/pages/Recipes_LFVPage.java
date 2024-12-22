package com.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.baseclass.BaseTest;
import com.baseclass.baseMethods;
import com.tests.A_ZScrapedRecipesLFV;

import com.utilities.ExcelReader;
import com.utilities.ExcelValueCheck;

import com.utilities.ExcelWriter;
import com.utilities.PropertyFileReader;

import recipe.LFV_Add;
import recipe.Receipedata;
import org.apache.commons.beanutils.BeanUtils;

public class Recipes_LFVPage extends A_ZScrapedRecipesLFV {

	private WebDriver driver;
	private List<String> excelVeganIngredients;
	private List<String> excelNotFullyVeganIngredients;
	private List<String> excelEliminateIngredients = new ArrayList<>();
	private String recipeName;		
	String alphabetPageTitle = "";
	List<String> unmatchedLFVIngredients;
	
	baseMethods basemethods = new baseMethods();
	private List<String> excelRecipeToAvoidList;
	private static final Object lock = new Object();

	List<String> columnNamesVegan = Collections.singletonList("Add");
	List<String> columnNamesNotFullyVegan = Collections.singletonList("To Add ( if not fully vegan)");
	List<String> columnNamesEliminate = Collections.singletonList("Eliminate");
	List<String> columnNamesRecipeToAvoid = Collections.singletonList("Recipes to avoid");

	
	public void readExcel() throws Throwable {
		String userDir = System.getProperty("user.dir");
		String getPathread = PropertyFileReader.getGlobalValue("inputExcelPath");
		String inputDataPath = userDir + getPathread;

		try {
			synchronized (lock) {
				excelVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesVegan, inputDataPath);
				excelNotFullyVeganIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesNotFullyVegan, inputDataPath);
				excelEliminateIngredients = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesEliminate, inputDataPath);
				excelRecipeToAvoidList = ExcelReader.getDataFromExcel("Final list for LFV Elimination ",
						columnNamesRecipeToAvoid, inputDataPath);
				System.out.println("Recipe to Avoid List: " + excelRecipeToAvoidList);

				System.out.println("Add Ingredients List: " + excelVeganIngredients);
				System.out.println("Not Fully Vegan Ingredients List: " + excelNotFullyVeganIngredients);
				System.out.println("Eliminate Ingredients List: " + excelEliminateIngredients);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void extractDataFromPages(WebDriver driver) throws Throwable {	
		this.driver = driver;
		extractRecipes();
	}
	

	private void extractRecipes() throws Throwable {
		int pageIndex = 0;
  System.out.println(" Page Number" + pageIndex);
  
		while (true) {
			pageIndex++;
			System.out.println("Page Number: " + pageIndex);

			try {
				List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
				System.out.println("No_of_recipes: " + recipeCards.size());

				for (int j = 0; j < recipeCards.size(); j++) {					
					processRecipe(j);
				}
			} catch (Exception e) {
				System.out.println("Error while extracting data: " + e.getMessage());
				break;
			}

			
			 if (!navigateToNextPage()) 
			 { 
				 break;
				 }
			 
		}
	}

	private boolean navigateToNextPage() throws Throwable {
		try {
			WebElement nextPageIndex = BaseTest.getDriver().findElement(By.xpath("//*[@class='rescurrpg']/following-sibling::a"));
			nextPageIndex.click();
			return true;
		} catch (Exception e) {
			System.out.println("No more pages for this alphabet");
			return false;
		}
	}

	
	
	
	private void processRecipe(int index) throws Throwable {
	
		try {
			
			List<WebElement> recipeCards = BaseTest.getDriver().findElements(By.className("rcc_recipecard"));
			System.out.println(" Receipe Size:" +recipeCards.size());
			if (index < recipeCards.size()) {
				WebElement recipeCard = recipeCards.get(index);

				// Getting recipe id
				String recipeID = recipeCard.getAttribute("id");
				String id = recipeID.replaceAll("[^0-9]", "");
				System.out.println("Recipe Id: " + id);

				// Getting recipe name
				WebElement recipeNameElement = recipeCard.findElement(By.xpath(".//span[@class='rcc_recipename']/a"));
				recipeName = recipeNameElement.getText();
				System.out.println("Recipe Name: " + recipeName);

				// Clicking into the recipe link
				recipeNameElement.click();
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
				  List<String> webIngredients = extractIngredients();
				
				List<String> unmatchedLFVIngredients = getUnmatchedIngredients(excelEliminateIngredients,
						webIngredients);
				
				System.out.println("unmatchedLFVIngredients:"+unmatchedLFVIngredients.size());
				unmatchedLFVIngredients = eliminateRedundantUnmatchedIngredients(unmatchedLFVIngredients);
				
				System.out.println("Duplicate removed unmatchedLFVIngredients:"+unmatchedLFVIngredients.size());
				List<String> matchedVeganIngredients = matchIngredientsWithExcel(excelVeganIngredients, webIngredients);
				
				System.out.println("matchedVeganIngredients:"+matchedVeganIngredients.size());
				
				
				SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
				Session session = sessionFactory.openSession();	
				
				Receipedata receipe = new Receipedata();
				
				
				  receipe = session.find(Receipedata.class, id); 
				  
				  boolean RecipeIDexists=false;
				  
				  System.out.println("ExistigReceipe Obj:"+receipe);

					if (null != receipe) {
						System.out.println("Receipe ID :" + id + " already exists in DB");
						RecipeIDexists = true;
					} else {
						System.out.println("Receipe ID :" + id + " NOT exists in DB");
						receipe = new Receipedata();
					}
				
					if (!unmatchedLFVIngredients.isEmpty() && !RecipeIDexists) {
						synchronized (lock) {
						DTO.setIngredients(String.join(",",unmatchedLFVIngredients ));
						System.out.println("LFV Eliminate :: Receipe Object brfore Save:"+receipe);		
						session.beginTransaction();
						session.save(DTO);
						session.getTransaction().commit();
						session.close();													
						}
					}
					
					if (!matchedVeganIngredients.isEmpty() && !RecipeIDexists) {
						LFV_Add addObj = new LFV_Add();				
						//Coping the values from DTO for LVF Add table
						addObj = basemethods.copyData(DTO, addObj);
						synchronized (lock) {		
						
						addObj.setIngredients(String.join(",",matchedVeganIngredients ));
						System.out.println("LFV Add :: Receipe Object brfore Save:"+addObj);
						System.out.println("LFV Add :: Receipe ID::::::"+addObj.getRecipe_ID()+",Name::"+addObj.getRecipe_Name());
						Session addSession = sessionFactory.openSession();
						addSession.beginTransaction();
						addSession.save(addObj);
						addSession.getTransaction().commit();
						addSession.close();											
						}
					}
										

				int maxRetries = 3;
				int retryCount = 0;
				while (retryCount < maxRetries) {
					try {
						System.out.println("Retry");
						BaseTest.getDriver().navigate().back();
						BaseTest.getDriver().findElement(By.className("rcc_recipecard")).isDisplayed();
						return; // Navigation successful, exit retry loop
					} catch (NoSuchElementException e) {
						System.out.println("Element not found, retrying...");
						retryCount++;
					}
				}
			}
		
		else

	           {
				System.out.println("Index " + index + " out of bounds for recipe cards");	
	           }
		}
		 catch (IndexOutOfBoundsException e) {
			System.out.println("Index " + index + " out of bounds for recipe cards");
		} catch (Exception e) {
			System.out.println("Error in processRecipe: " + e.getMessage());
		}
	           		

}
	
		
	
	

	private List<String> extractIngredients() throws Throwable {
		List<WebElement> ingredientsList = BaseTest.getDriver().findElements(By.xpath("//div[@id='rcpinglist']//span[@itemprop='recipeIngredient']//a/span"));
		List<String> webIngredients = new ArrayList<>();

		for (WebElement ingredient : ingredientsList) {
			String ingredientName = ingredient.getText().trim().toLowerCase();
			webIngredients.add(ingredientName);
		}
		System.out.println("Ingredients: " + webIngredients);
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

	private List<String> eliminateRedundantUnmatchedIngredients(List<String> unmatchedIngredients) {
		return new ArrayList<>(new HashSet<>(unmatchedIngredients));
	}

	public List<String> matchwithRecipeToAvoid(List<String> excelIngredients) throws Throwable {
		List<String> matchedIngredients = new ArrayList<>();
		// Extract tags from the web page
		String tagText = BaseTest.getDriver().findElement(By.id("recipe_tags")).getText().toLowerCase();
		String[] tagArray = tagText.split(",\\s*"); // Split by comma and trim whitespace
		List<String> tags = Arrays.asList(tagArray);
		// Match tags with Excel ingredients list (partial matches allowed)
		for (String tag : tags) {
			for (String excelIngredient : excelIngredients) {
				if (normalize(tag).contains(normalize(excelIngredient))
						|| normalize(excelIngredient).contains(normalize(tag))) {
					System.out.println("Match found: " + excelIngredient + " in tags.");
					matchedIngredients.add(excelIngredient);
					// Assuming you want to add all matching ingredients
				}
			}
		}
		return matchedIngredients;
	}

	private String normalize(String text) {
		return text.toLowerCase().trim();
	}

	
}
