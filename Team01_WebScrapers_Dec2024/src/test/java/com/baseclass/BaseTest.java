package com.baseclass;



import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utilities.PropertyFileReader;

public class BaseTest {
	
	   private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	    public static void browsersetup() throws Throwable {
	        ChromeOptions options = new ChromeOptions();
	         options.addArguments("--headless"); // Uncomment if you want to run in headless mode
	        options.addArguments("--disable-popup-blocking");
	        options.addArguments("--disable-notifications");
	        options.addArguments("--disable-extensions");
	        options.addArguments("--disable-cache");
	        options.addArguments("--disk-cache-size=0");
	        options.addArguments("blink-settings=imagesEnabled=false"); // Disable images
	        options.addArguments("--disable-plugins");
	        options.addArguments("--disable-infobars");
	        options.addArguments("--enable-fast-unload");
	        options.addArguments("--enable-quic");
	        options.addArguments("--disable-gpu"); 
	        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

	        tlDriver.set(new ChromeDriver(options));
	        openWebsite();
	    }

	    public static WebDriver getDriver() throws Throwable {
	        if (tlDriver.get() == null) {
	            browsersetup();
	        }
	        return tlDriver.get();
	    }

	    public static void openWebsite() throws Throwable {
	        WebDriver driver = getDriver();
	        driver.get(PropertyFileReader.getGlobalValue("url"));
	        driver.manage().window().maximize();
	        driver.manage().deleteAllCookies();
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Recipe A To Z']")));
	        driver.findElement(By.xpath("//a[text()='Recipe A To Z']")).click();
	    }

		public static void tearDown() {
			if (tlDriver.get() != null) {
				tlDriver.get().quit();
				tlDriver.remove();
			}
		}
}
