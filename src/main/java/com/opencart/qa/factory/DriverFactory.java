package com.opencart.qa.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.opencart.qa.exceptions.BrowserException;
import com.opencart.qa.exceptions.FrameworkException;

public class DriverFactory {

	WebDriver driver;
	Properties prop;

	public static String highlight;

	public OptionsManager optionsManager;

	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

	/**
	 * This method is used to init the driver on the basis of given browserName.
	 * 
	 * @param browserName
	 * @return it returns the driver value
	 */
	public WebDriver initDriver(Properties prop) {		
		
		String browserName = prop.getProperty("browser");
		// String browserName = System.getProperty("browser");//chrome
		System.out.println("browser name : " + browserName);

		highlight = prop.getProperty("highlight");
		optionsManager = new OptionsManager(prop);

		switch (browserName.trim().toLowerCase()) {
		case "chrome":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run tcs on grid:
				initRemoteDriver(browserName);
			} else {
				// run tcs on local:
				tlDriver.set(new ChromeDriver(optionsManager.getChromeOptions()));
			}
			break;
		case "firefox":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run tcs on grid:
				initRemoteDriver(browserName);
			} else {
				// run tcs on local:
				tlDriver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));
			}
			break;
		case "edge":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run tcs on grid:
				initRemoteDriver(browserName);
			} else {
				// run tcs on local:
				tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));
			}
			break;
		case "safari":
			tlDriver.set(new SafariDriver());
			break;

		default:
			System.out.println("=====Invalid browser=====" + browserName);
			throw new BrowserException("====Invalid Browser====");
		}

		getDriver().manage().deleteAllCookies();
		getDriver().manage().window().maximize();
		getDriver().get(prop.getProperty("url"));

		return getDriver();

	}

	/**
	 * this will setup the RWD with hub url and browser options. it will supply the test to the remote grid machine
	 * @param browserName
	 */
	@SuppressWarnings("deprecation")
	private void initRemoteDriver(String browserName) {
		System.out.println("Running tcs on selenium grid...with browser: "+ browserName);
		try {
			switch (browserName.trim().toLowerCase()) {
			case "chrome":
				tlDriver.set(
						new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getChromeOptions()));
				break;
			case "firefox":
				tlDriver.set(
						new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getFirefoxOptions()));
				break;
			case "edge":
				tlDriver.set(new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getEdgeOptions()));
				break;

			default:
				System.out.println("Plz supply the right browser name...." + browserName);
				break;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * this will return one local copy of driver for a specific thread
	 */
	public static WebDriver getDriver() {
		return tlDriver.get();
	}

	/**
	 * this method is used to init the properties file
	 * 
	 * @return it return the properties class object which is having all the
	 *         properties (key-value pair)
	 */

	// mvn test -Denv="qa"
	// mvn clean install
	// mvn package
	// mvn deploy
	public Properties initProp() {
		FileInputStream ip = null;
		prop = new Properties();

		String envName = System.getProperty("env");// qa
		System.out.println("Env Name is : " + envName);

		try {
			if (envName == null) {
				System.out.println("env name is null, hence running test cases on QA environment...");
				ip = new FileInputStream("./src/test/resources/config/config.qa.properties");
			} else {
				switch (envName.trim().toLowerCase()) {
				case "qa":
					ip = new FileInputStream("./src/test/resources/config/config.qa.properties");
					break;
				case "dev":
					ip = new FileInputStream("./src/test/resources/config/config.dev.properties");
					break;
				case "stage":
					ip = new FileInputStream("./src/test/resources/config/config.stage.properties");
					break;
				case "uat":
					ip = new FileInputStream("./src/test/resources/config/config.uat.properties");
					break;
				case "prod":
					ip = new FileInputStream("./src/test/resources/config/config.properties");
					break;
				default:
					System.out.println("=========invalid env name: =====" + envName);
					throw new FrameworkException("INVALID ENV NaME: " + envName);

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			prop.load(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;

	}

	/**
	 * takescreenshot
	 */

	public static File getScreenshotFile() {
		return ((TakesScreenshot) getDriver()).getScreenshotAs((OutputType.FILE));// temp dir
	}

	public static byte[] getScreenshotByte() {
		return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);// temp dir

	}

	public static String getScreenshotBase64() {
		return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);// temp dir

	}

}