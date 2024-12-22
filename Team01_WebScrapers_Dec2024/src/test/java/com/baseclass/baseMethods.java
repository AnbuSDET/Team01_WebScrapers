package com.baseclass;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.recipe.LFV_Add;
import com.recipe.Receipedata;

public class baseMethods {
String alphabetPageTitle = "";	
	
	public String getRecipeCategory(Receipedata DTO) throws Throwable {
		String recipeCategory;
		try {
			// je.executeScript("window.scrollBy(0,200)");
			 recipeCategory = BaseTest.getDriver().findElement(By.xpath("//a[@itemprop='recipeCategory'][1]")).getText();
			if (recipeCategory.toLowerCase().contains("lunch") || DTO.getRecipe_Name().toLowerCase().contains("lunch")) {
				recipeCategory = "Lunch";
			} else if (recipeCategory.toLowerCase().contains("breakfast")
					|| DTO.getRecipe_Name().toLowerCase().contains("breakfast")) {
				recipeCategory = "Breakfast";
			} else if (recipeCategory.toLowerCase().contains("dinner") || DTO.getRecipe_Name().toLowerCase().contains("dinner")) {
				recipeCategory = "Dinner";
			} else if (recipeCategory.toLowerCase().contains("snack") || DTO.getRecipe_Name().toLowerCase().contains("snack")) {
				recipeCategory = "Snack";
			} else {
				recipeCategory = "NA";
			}
		} catch (NoSuchElementException e) {
			System.out.println("Recipe category element not found for recipe: " + DTO.getRecipe_Name());
			recipeCategory = "Unknown";
		}
		
		return recipeCategory;
	}

	public String getTags(Receipedata DTO) throws Throwable {
		String recipeTags;
		try {

			List<WebElement> tagElements = BaseTest.getDriver().findElements(By.xpath("//div[@id='recipe_tags']/a"));

			// Extract the text of each element and store it in a list
			List<String> tagTexts = new ArrayList<>();
			for (WebElement element : tagElements) {
				tagTexts.add(element.getText());
			}

			// Join the tags into a single string or print individually
			recipeTags = String.join(", ", tagTexts);
			System.out.println("Tags are: " + recipeTags);
		} catch (NoSuchElementException e) {
			recipeTags = "Unknown";
			System.out.println("Tags not found.");
		}
		return recipeTags;
	}


	// Get the Food Category(Veg/non-veg/vegan/Jain)
	public String getFoodCategory(Receipedata DTO) {
		String foodCategory;
		try {
			if (DTO.getRecipe_Name().contains("Vegan") || DTO.getTag().contains("Vegan")) {
				foodCategory = "VEGAN";
			} else if (DTO.getRecipe_Name().contains("Jain") || DTO.getTag().contains("Jain")) {
				foodCategory = "JAIN";
			} else if (DTO.getRecipe_Name().contains("Egg") || DTO.getTag().contains("Egg")) {
				foodCategory = "EGGITARIAN";
			} else if (DTO.getRecipe_Name().contains("NonVeg") || DTO.getTag().contains("NonVeg")) {
				foodCategory = "NONVEGETARIAN";
			} else if (DTO.getRecipe_Name().contains("Vegetarian") || DTO.getTag().contains("Vegetarian")) {
				foodCategory = "VEGETARIAN";
			} else {
				foodCategory = "NA";
			}

		} catch (NoSuchElementException e) {
			System.out.println("Food category element not found for recipe: " + DTO.getRecipe_Name());
			foodCategory = "Unknown";
		}
				return foodCategory;
	}

	public String getcuisineCategory(Receipedata DTO) {
		String cuisineCategory;
		try {
			String lowerCaseRecipeName = DTO.getRecipe_Name().toLowerCase();
			String lowerCaseRecipeTags = DTO.getTag().toLowerCase();		

			if (lowerCaseRecipeName.contains("indian") || lowerCaseRecipeTags.contains("indian")) {
				cuisineCategory = "Indian";
			} else if (lowerCaseRecipeName.contains("south indian") || lowerCaseRecipeTags.contains("south indian")) {
				cuisineCategory = "South Indian";
			} else if (lowerCaseRecipeName.contains("rajasthani") || lowerCaseRecipeTags.contains("rajasthani")) {
				cuisineCategory = "Rajasthani";
			} else if (lowerCaseRecipeName.contains("punjabi") || lowerCaseRecipeTags.contains("punjabi")) {
				cuisineCategory = "Punjabi";
			} else if (lowerCaseRecipeName.contains("bengali") || lowerCaseRecipeTags.contains("bengali")) {
				cuisineCategory = "Bengali";
			} else if (lowerCaseRecipeName.contains("orissa") || lowerCaseRecipeTags.contains("orissa")) {
				cuisineCategory = "Orissa";
			} else if (lowerCaseRecipeName.contains("gujarati") || lowerCaseRecipeTags.contains("gujarati")) {
				cuisineCategory = "Gujarati";
			} else if (lowerCaseRecipeName.contains("maharashtrian") || lowerCaseRecipeTags.contains("maharashtrian")) {
				cuisineCategory = "Maharashtrian";
			} else if (lowerCaseRecipeName.contains("andhra") || lowerCaseRecipeTags.contains("andhra")) {
				cuisineCategory = "Andhra";
			} else if (lowerCaseRecipeName.contains("kerala") || lowerCaseRecipeTags.contains("kerala")) {
				cuisineCategory = "Kerala";
			} else if (lowerCaseRecipeName.contains("goan") || lowerCaseRecipeTags.contains("goan")) {
				cuisineCategory = "Goan";
			} else if (lowerCaseRecipeName.contains("kashmiri") || lowerCaseRecipeTags.contains("kashmiri")) {
				cuisineCategory = "Kashmiri";
			} else if (lowerCaseRecipeName.contains("himachali") || lowerCaseRecipeTags.contains("himachali")) {
				cuisineCategory = "Himachali";
			} else if (lowerCaseRecipeName.contains("tamil nadu") || lowerCaseRecipeTags.contains("tamil nadu")) {
				cuisineCategory = "Tamil Nadu";
			} else if (lowerCaseRecipeName.contains("karnataka") || lowerCaseRecipeTags.contains("karnataka")) {
				cuisineCategory = "Karnataka";
			} else if (lowerCaseRecipeName.contains("sindhi") || lowerCaseRecipeTags.contains("sindhi")) {
				cuisineCategory = "Sindhi";
			} else if (lowerCaseRecipeName.contains("chhattisgarhi") || lowerCaseRecipeTags.contains("chhattisgarhi")) {
				cuisineCategory = "Chhattisgarhi";
			} else if (lowerCaseRecipeName.contains("madhya pradesh")
					|| lowerCaseRecipeTags.contains("madhya pradesh")) {
				cuisineCategory = "Madhya Pradesh";
			} else if (lowerCaseRecipeName.contains("assamese") || lowerCaseRecipeTags.contains("assamese")) {
				cuisineCategory = "Assamese";
			} else if (lowerCaseRecipeName.contains("manipuri") || lowerCaseRecipeTags.contains("manipuri")) {
				cuisineCategory = "Manipuri";
			} else if (lowerCaseRecipeName.contains("tripuri") || lowerCaseRecipeTags.contains("tripuri")) {
				cuisineCategory = "Tripuri";
			} else if (lowerCaseRecipeName.contains("sikkimese") || lowerCaseRecipeTags.contains("sikkimese")) {
				cuisineCategory = "Sikkimese";
			} else if (lowerCaseRecipeName.contains("mizo") || lowerCaseRecipeTags.contains("mizo")) {
				cuisineCategory = "Mizo";
			} else if (lowerCaseRecipeName.contains("arunachali") || lowerCaseRecipeTags.contains("arunachali")) {
				cuisineCategory = "Arunachali";
			} else if (lowerCaseRecipeName.contains("uttarakhand") || lowerCaseRecipeTags.contains("uttarakhand")) {
				cuisineCategory = "Uttarakhand";
			} else if (lowerCaseRecipeName.contains("haryanvi") || lowerCaseRecipeTags.contains("haryanvi")) {
				cuisineCategory = "Haryanvi";
			} else if (lowerCaseRecipeName.contains("awadhi") || lowerCaseRecipeTags.contains("awadhi")) {
				cuisineCategory = "Awadhi";
			} else if (lowerCaseRecipeName.contains("bihari") || lowerCaseRecipeTags.contains("bihari")) {
				cuisineCategory = "Bihari";
			} else if (lowerCaseRecipeName.contains("uttar pradesh") || lowerCaseRecipeTags.contains("uttar pradesh")) {
				cuisineCategory = "Uttar Pradesh";
			} else if (lowerCaseRecipeName.contains("delhi") || lowerCaseRecipeTags.contains("delhi")) {
				cuisineCategory = "Delhi";
			} else if (lowerCaseRecipeName.contains("north indian") || lowerCaseRecipeTags.contains("north indian")) {
				cuisineCategory = "North Indian";
			} else {
				cuisineCategory = "NA";
			}
		} catch (NoSuchElementException e) {
			System.out.println("Cuisine category element not found for recipe: " + DTO.getRecipe_Name());
			cuisineCategory = "Unknown";
		}
      return cuisineCategory;
	}

	public String getPreparationTime(Receipedata DTO) throws Throwable {
		String preparationTime;
		try {
			 preparationTime = BaseTest.getDriver().findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
		} catch (NoSuchElementException e) {
			preparationTime = "0";
		}
		
		return preparationTime;
	}

	public String getCookingTime(Receipedata DTO) throws Throwable {
		String cookingTime;
		try {
			cookingTime = BaseTest.getDriver().findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
		} catch (NoSuchElementException e) {
			cookingTime = "0";
		}
		
		return cookingTime;
	}

	public String getRecipeDescription(Receipedata DTO) throws Throwable {
		String recipeDescription;
		try {
			recipeDescription = BaseTest.getDriver().findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']")).getText();
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}
     
       return recipeDescription.trim();
	}

	public String getPreparationMethod(Receipedata DTO) throws Throwable {
		String preparationMethod;
		try {
			preparationMethod = BaseTest.getDriver().findElement(By.xpath("//div[@id='recipe_small_steps']")).getText();
			System.out.println("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}
		return preparationMethod;
	}
	

	public String getNutrientValues(Receipedata DTO) throws Throwable {
		String nutrientValues;
		try {
			nutrientValues = BaseTest.getDriver().findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
		
		return nutrientValues;
	}

	public String getNoOfServings(Receipedata DTO) throws Throwable {
		String noOfServings;
		try {
			noOfServings = BaseTest.getDriver().findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
		} catch (NoSuchElementException e) {
			noOfServings = "0";
		}
		
		return noOfServings;
	}
	
	
	public LFV_Add copyData(Receipedata eliminateDTO,LFV_Add addDTO) throws Throwable {
		
		try {
			addDTO.setRecipe_ID(eliminateDTO.getRecipe_ID());
			addDTO.setRecipe_Name(eliminateDTO.getRecipe_Name());				
			addDTO.setRecipe_Category(eliminateDTO.getRecipe_Category());				  
			addDTO.setTag(eliminateDTO.getTag());
			addDTO.setFood_Category(eliminateDTO.getFood_Category());
			addDTO.setCuisine_category(eliminateDTO.getCuisine_category());
			addDTO.setPreparation_Time(eliminateDTO.getPreparation_Time());				  
			addDTO.setPreparation_method(eliminateDTO.getPreparation_method());
			addDTO.setCooking_Time(eliminateDTO.getCooking_Time());
			addDTO.setNutrient_values(eliminateDTO.getNutrient_values());
			addDTO.setNo_of_servings(eliminateDTO.getNo_of_servings());
			addDTO.setRecipe_Description(eliminateDTO.getRecipe_Description());
			
		} catch (NoSuchElementException e) {
			System.out.println("copyData :: "+e.getLocalizedMessage());
		}
		return addDTO;
	}
	
	
}
