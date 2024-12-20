package com.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.baseclass.BaseTest;

import pages.Recipes_LFVPage;
import com.utilities.CreateExcel;

public class A_ZScrapedRecipesLFV {

	private Recipes_LFVPage homePage;

	
	public void createExcelFile() {
		CreateExcel.createExcelWorkBook();
	}

	@BeforeMethod
	public void setup() throws Throwable {
		BaseTest.browsersetup();
		homePage = new Recipes_LFVPage();
		homePage.readExcel();
	}

	// if you want to run in parallel set it to true

	@DataProvider(name = "alphabetDataProvider", parallel = false)
	public Object[][] alphabetDataProvider() {
		return new Object[][] { { "A" } };
	}

	
	  
	//@Test(dataProvider = "alphabetDataProvider")
	public void clickAlphabetLink(String alphabet) throws Throwable {
		waitForElementToBeClickable(By.xpath("//a[text()='" + alphabet + "']")).click();
		System.out.println("Clicked on alphabet: " + alphabet);
		homePage.extractDataFromPages(BaseTest.getDriver());
	}
	 

	@Test
	public void main() throws Throwable {
		System.out.println( " Clicking A-Z");
		waitForElementToBeClickable(By.xpath("//a[text()='A']")).click();
		homePage.extractDataFromPages(BaseTest.getDriver());
	}

	private WebElement waitForElementToBeClickable(By locator) throws Throwable {
		FluentWait<WebDriver> wait = new FluentWait<>(BaseTest.getDriver()).withTimeout(Duration.ofSeconds(30))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	//@AfterMethod
	public void tearDown() {
		BaseTest.tearDown();
	}
}