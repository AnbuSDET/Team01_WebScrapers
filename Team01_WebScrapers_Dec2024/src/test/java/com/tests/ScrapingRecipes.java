package com.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.baseclass.BaseTest;
import com.utilities.PropertyFileReader;

public class ScrapingRecipes extends BaseTest{

	
	static String browserName;
	private static BaseTest seleniumBase;
	
	@Test(priority=1)
	public void recipeScraping() throws Throwable {
	browserName = PropertyFileReader.getGlobalValue("browserName");
	// Initialize the driver
	seleniumBase = new BaseTest();
	seleniumBase.setDriver(browserName);
	getUrl("baseUrl");
	verifyTitle("Indian Recipes | Indian Vegetarian Recipes | Top Indian Veg Dishes");
	System.out.println("******************");

	WebElement recipies = driver.findElement(By.xpath("//div[contains(text(),'RECIPES')]"));

	recipies.click();
}
}
