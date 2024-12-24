
package com.recipe;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "LFV_Elimination")
public class Receipedata implements Serializable {

	@Id
	private String Recipe_ID;
	private String Recipe_Name;
	private String Recipe_Category;
	private String Food_Category;
	private String Ingredients;
	private String Preparation_Time;
	private String Cooking_Time;
	private String Tag;
	private String No_of_servings;
	private String Cuisine_category;
	private String Recipe_Description;
	private String Preparation_method;
	private String Nutrient_values;
	private String Recipe_URL;

	public String getRecipe_ID() {
		return Recipe_ID;
	}

	public void setRecipe_ID(String recipe_ID) {
		Recipe_ID = recipe_ID;
	}

	public String getRecipe_Name() {
		return Recipe_Name;
	}

	public void setRecipe_Name(String recipe_Name) {
		Recipe_Name = recipe_Name;
	}

	public String getRecipe_Category() {
		return Recipe_Category;
	}

	public void setRecipe_Category(String recipe_Category) {
		Recipe_Category = recipe_Category;
	}

	public String getFood_Category() {
		return Food_Category;
	}

	public void setFood_Category(String food_Category) {
		Food_Category = food_Category;
	}

	public String getIngredients() {
		return Ingredients;
	}

	public void setIngredients(String ingredients) {
		Ingredients = ingredients;
	}

	public String getPreparation_Time() {
		return Preparation_Time;
	}

	public void setPreparation_Time(String preparation_Time) {
		Preparation_Time = preparation_Time;
	}

	public String getCooking_Time() {
		return Cooking_Time;
	}

	public void setCooking_Time(String cooking_Time) {
		Cooking_Time = cooking_Time;
	}

	public String getTag() {
		return Tag;
	}

	public void setTag(String tag) {
		Tag = tag;
	}

	public String getNo_of_servings() {
		return No_of_servings;
	}

	public void setNo_of_servings(String no_of_servings) {
		No_of_servings = no_of_servings;
	}

	public String getCuisine_category() {
		return Cuisine_category;
	}

	public void setCuisine_category(String cuisine_category) {
		Cuisine_category = cuisine_category;
	}

	public String getRecipe_Description() {
		return Recipe_Description;
	}

	public void setRecipe_Description(String recipe_Description) {
		Recipe_Description = recipe_Description;
	}

	public String getPreparation_method() {
		return Preparation_method;
	}

	public void setPreparation_method(String preparation_method) {
		Preparation_method = preparation_method;
	}

	public String getNutrient_values() {
		return Nutrient_values;
	}

	public void setNutrient_values(String nutrient_values) {
		Nutrient_values = nutrient_values;
	}

	public String getRecipe_URL() {
		return Recipe_URL;
	}

	public void setRecipe_URL(String recipe_URL) {
		Recipe_URL = recipe_URL;
	}

}
