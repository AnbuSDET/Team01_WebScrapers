package com.baseclass;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;



import com.utilities.PropertyFileReader;

public class BaseTest {
	
	   private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	    public static void browsersetup() throws Throwable {
	        ChromeOptions options = new ChromeOptions();
	        options.addArguments("--headless"); // Uncomment if you want to run in headless mode
	        options.addArguments("--disable-popup-blocking");
	        options.addArguments("--disable-notifications");
	        options.addArguments("--disable-extensions");
	        options.addArguments("disable-gpu"); // Applicable to Windows OS only
	        options.addArguments("blink-settings=imagesEnabled=false"); // Disable images
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
	        driver.findElement(By.xpath("//a[text()='Recipe A To Z']")).click();
	    }

		public static void tearDown() {
			if (tlDriver.get() != null) {
				tlDriver.get().quit();
				tlDriver.remove();
			}
		}
}