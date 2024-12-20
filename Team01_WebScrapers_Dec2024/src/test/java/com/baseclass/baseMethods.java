package com.baseclass;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.tests.A_ZScrapedRecipesLFV;

import recipe.Receipedata;


public class baseMethods  {		
	
	
	String alphabetPageTitle = "";
	
	
	public void getRecipeCategory(Receipedata DTO) throws Throwable {
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
				
			System.out.println("Recipe Category is :" + recipeCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Recipe category element not found for recipe: " + DTO.getRecipe_Name());
			recipeCategory = "Unknown";
		}
		DTO.setRecipe_Category(recipeCategory);
		System.out.println("DTO :" + DTO);		
	}

	public void getTags(Receipedata DTO) throws Throwable {
		String recipeTags;
		try {
			 recipeTags = BaseTest.getDriver().findElement(By.id("recipe_tags")).getText();
			System.out.println("Tags are : " + recipeTags);
		} catch (NoSuchElementException e) {
			recipeTags = "Unknown";
		}
		DTO.setTag(recipeTags);
	}

	// Get the Food Category(Veg/non-veg/vegan/Jain)
	public void getFoodCategory(Receipedata DTO) {
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

			System.out.println("Recipe Category is :" + foodCategory);

		} catch (NoSuchElementException e) {
			System.out.println("Food category element not found for recipe: " + DTO.getRecipe_Name());
			foodCategory = "Unknown";
		}
		DTO.setFood_Category(foodCategory);
	}

	public void getcuisineCategory(Receipedata DTO) {
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
			System.out.println("Cuisine Category is: " + cuisineCategory);
		} catch (NoSuchElementException e) {
			System.out.println("Cuisine category element not found for recipe: " + DTO.getRecipe_Name());
			cuisineCategory = "Unknown";
		}

	}

	public void getPreparationTime(Receipedata DTO) throws Throwable {
		String preparationTime;
		try {
			 preparationTime = BaseTest.getDriver().findElement(By.xpath("//time[@itemprop='prepTime']")).getText();
			System.out.println("Preperation Time is :" + preparationTime);
			// je.executeScript("window.scrollBy(0,200)");
		} catch (NoSuchElementException e) {
			preparationTime = "0";
		}
		DTO.setPreparation_Time(preparationTime);
	}

	public void getCookingTime(Receipedata DTO) throws Throwable {
		String cookingTime;
		try {
			cookingTime = BaseTest.getDriver().findElement(By.xpath("//time[@itemprop='cookTime']")).getText();
			System.out.println("Cooking Time is :" + cookingTime);
		} catch (NoSuchElementException e) {
			cookingTime = "0";
		}
		DTO.setCooking_Time(cookingTime);
	}

	public void getRecipeDescription(Receipedata DTO) throws Throwable {
		String recipeDescription;
		try {
			recipeDescription = BaseTest.getDriver().findElement(By.xpath("//span[@id='ctl00_cntrightpanel_lblDesc']")).getText();
			System.out.println("Recipe Description: " + recipeDescription);
		} catch (NoSuchElementException e) {
			recipeDescription = "Unknown";
		}
       DTO.setRecipe_Description(recipeDescription);
	}

	public void getPreparationMethod(Receipedata DTO) throws Throwable {
		String preparationMethod;
		try {
			preparationMethod = BaseTest.getDriver().findElement(By.xpath("//div[@id='ctl00_cntrightpanel_pnlRcpMethod']")).getText();
			System.out.println("Preparation Method : " + preparationMethod);

		} catch (NoSuchElementException e) {
			preparationMethod = "Unknown";
		}
      DTO.setPreparation_method(preparationMethod);
	}
	

	public void getNutrientValues(Receipedata DTO) throws Throwable {
		String nutrientValues;
		try {
			nutrientValues = BaseTest.getDriver().findElement(By.xpath("//table[@id='rcpnutrients']/tbody")).getText();
			System.out.println("Nutrient Values: " + nutrientValues);
		} catch (NoSuchElementException e) {
			nutrientValues = "Unknown";
		}
		DTO.setNutrient_values(nutrientValues);
	}

	public void getNoOfServings(Receipedata DTO) throws Throwable {
		String noOfServings;
		try {
			noOfServings = BaseTest.getDriver().findElement(By.id("ctl00_cntrightpanel_lblServes")).getText();
			System.out.println("No of Servings: " + noOfServings);
		} catch (NoSuchElementException e) {
			noOfServings = "0";
		}
		DTO.setNo_of_servings(noOfServings);
	}
	
	
	
}
