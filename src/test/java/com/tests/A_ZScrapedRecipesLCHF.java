package com.tests;

import org.testng.annotations.AfterMethod;
import java.time.Duration;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.baseclass.BaseTest;

import com.pages.Recipes_LCHFPage;


public class A_ZScrapedRecipesLCHF {

	private Recipes_LCHFPage lchfPage;


	@BeforeMethod
	public void setup() throws Throwable {
		BaseTest.browsersetup();

		lchfPage = new Recipes_LCHFPage();
		lchfPage.readExcel();

	}

	// if you want to run in parallel set it to true
	@DataProvider(name = "alphabetDataProvider", parallel = false)
	public Object[][] alphabetDataProvider() {
		return new Object[][] {{"A"}};
	}

	
	@Test(priority=1,dataProvider = "alphabetDataProvider")
	public void clickAlphabetLink(String alphabet) throws Throwable {
		waitForElementToBeClickable(By.xpath("//a[text()='" + alphabet + "']")).click();
		System.out.println("Clicked on alphabet: " + alphabet);

		lchfPage.extractDataFromPages(BaseTest.getDriver(), alphabet);

	}
	

	
//	@Test
//	public void clickAllAlphabets() {
//	    List<String> alphabets = Stream.of("E","F","G","H") 
//	            .collect(Collectors.toList());
//
//	    alphabets.forEach(alphabet -> {
//	        try {
//	            // Wait for the element and click
//	            waitForElementToBeClickable(By.xpath("//a[text()='" + alphabet + "']")).click();
//	            System.out.println("Clicked on alphabet: " + alphabet);
//
//	            // Extract data from the page after clicking
//	            lchfPage.extractDataFromPages(BaseTest.getDriver(), alphabet);
//	        } catch (Throwable e) {
//	            e.printStackTrace();
//	        }
//	    });
//	}

	private WebElement waitForElementToBeClickable(By locator) throws Throwable {
		FluentWait<WebDriver> wait = new FluentWait<>(BaseTest.getDriver()).withTimeout(Duration.ofSeconds(30))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	@AfterMethod
	public void tearDown() {
		BaseTest.tearDown();
	}
}