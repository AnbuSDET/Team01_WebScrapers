package recipe;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "LFV_Elimination")
public class Receipedata implements Serializable{
	
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
	
}
