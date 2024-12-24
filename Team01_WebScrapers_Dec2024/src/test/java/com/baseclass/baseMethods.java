package com.baseclass;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.recipe.LFV_Add;
import com.recipe.Receipedata;
import com.tests.A_ZScrapedRecipesLFV;


public class baseMethods  {			
	
	String alphabetPageTitle = "";	
	
	
	public boolean eliminateRecipe(List<String> excelIngredients, List<String> webIngredients) {
		
	    Set<String> excelSet = new HashSet<>(excelIngredients);
	    for (String webIngredient : webIngredients) {
	        for (String excelIngredient : excelSet) {
	        	if (webIngredient.toLowerCase().contains(excelIngredient.toLowerCase())) 
	             {
	                // Found a match, eliminate the recipe
	                return false;
	            }
	        }
	    }
	    // No matches found, keep the recipe
	    return true;
	}
	

	public boolean addIngredients(List<String> excelIngredients, List<String> webIngredients) {
		
	    Set<String> excelSet = new HashSet<>(excelIngredients);
	    for (String webIngredient : webIngredients) {
	        for (String excelIngredient : excelSet) {
	            if (webIngredient.toLowerCase().contains(excelIngredient.toLowerCase())) {	               
	                return true;
	            }
	        }
	    }
	    
	    return false;
	}
	
	
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
				
			//System.out.println("Recipe Category is :" + recipeCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Recipe category element not found for recipe: " + DTO.getRecipe_Name());
			recipeCategory = "Unknown";
		}
		//DTO.setRecipe_Category(recipeCategory);		
		//System.out.println("DTO :" + DTO);	
		return recipeCategory;
	}

	public String getTags(Receipedata DTO) throws Throwable {
		String recipeTags;
		try {
			 recipeTags = BaseTest.getDriver().findElement(By.id("recipe_tags")).getText();
			//System.out.println("Tags are : " + recipeTags);
		} catch (NoSuchElementException e) {
			recipeTags = "Unknown";
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

			//System.out.println("Food Category is :" + foodCategory);

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
			//System.out.println("Cuisine Category is: " + cuisineCategory);
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
			//System.out.println("Preperation Time is :" + preparationTime);
			// je.executeScript("window.scrollBy(0,200)");
		} catch (NoSuchElementException e) {
			preparationTime = "0";
		}
		
		return preparationTime;
	}

	public String getCookingTime(Receipedata DTO) throws Throwable {
		String cookingTime;
		try {
			cookingTime = BaseTest.getDriver().findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
			//System.out.println("Cooking Time is :" + cookingTime);
		} catch (NoSuchElementException e) {
			cookingTime = "0";
		}
		
		return cookingTime;
	}

	public String getRecipeDescription(Receipedata DTO) throws Throwable {
		String recipeDescription;
		try {
			recipeDescription = BaseTest.getDriver().findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']")).getText();
			//System.out.println("Recipe Description: " + recipeDescription);
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}
     
       return recipeDescription.trim();
	}

	public String getPreparationMethod(Receipedata DTO) throws Throwable {
		String preparationMethod;
		try {
			preparationMethod = BaseTest.getDriver().findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']")).getText();
		//	System.out.println("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}
     
      return preparationMethod;
	}
	

	public String getNutrientValues(Receipedata DTO) throws Throwable {
		String nutrientValues;
		try {
			nutrientValues = BaseTest.getDriver().findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
			//System.out.println("Nutrient Values: " + nutrientValues);
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
		
		return nutrientValues;
	}

	public String getNoOfServings(Receipedata DTO) throws Throwable {
		String noOfServings;
		try {
			noOfServings = BaseTest.getDriver().findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
			//System.out.println("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			noOfServings = "0";
		}
		
		return noOfServings;
	}
	
	public String getRecipeURL(Receipedata DTO) throws Throwable
	{
		String Url;
		try {
			Url = BaseTest.getDriver().getCurrentUrl();
			//System.out.println("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			Url = "";
		}
		
		return Url;
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
			addDTO.setRecipe_URL(eliminateDTO.getRecipe_URL());		
		} catch (NoSuchElementException e) {
			System.out.println("copyData :: "+e.getLocalizedMessage());
		}
		//System.out.println("copyData :: Final Add::"+addDTO);
		return addDTO;
	}
	
	
	
}
