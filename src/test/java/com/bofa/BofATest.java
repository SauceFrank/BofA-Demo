package com.bofa;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class BofATest {
//Environment variable for user and Sauce_accesskey
    public String sauce_username = System.getenv("SAUCE_USERNAME");
    public String sauce_accesskey = System.getenv("SAUCE_ACCESS_KEY");
    //public String sauce_region = System.getenv().getOrDefault("SAUCE_REGION", "us-west-1");
    public String build_type = System.getenv().getOrDefault("SAUCE_BUILD_TYPE", "Local");
    public String build = "BofA Demo " + build_type + ' ' + System.currentTimeMillis();

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{

                // Windows
//                new Object[]{"browser","chrome", "latest", "Windows 10",""},
                new Object[]{"browser","MicrosoftEdge", "latest", "Windows 10",""},
                new Object[]{"browser","firefox", "latest-2", "Windows 10",""},
                new Object[]{"browser","internet explorer", "11", "Windows 8.1",""},
                new Object[]{"browser","firefox", "55.0", "Windows 7",""},

                // Mac
                new Object[]{"browser","firefox", "latest", "macOS 10.14",""},
                new Object[]{"browser","safari", "latest", "macOS 10.13",""},
                new Object[]{"browser","safari", "11.0", "macOS 10.12",""},
//                new Object[]{"browser","chrome", "76.0", "OS X 10.11",""},

                //Devices
                new Object[]{"device","", "", "Android","Samsung.*"},
                new Object[]{"device","", "", "iOS", "iPhone.*"},
        };
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the  instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String environment, String browser, String version, String os, String device, String methodName) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        List<String> tags = Arrays.asList("demo", "frank", "java", build_type);

        capabilities.setCapability("username", sauce_username);
        capabilities.setCapability("accesskey", sauce_accesskey);
        capabilities.setCapability("tags", tags);
        capabilities.setCapability("build", build);

        String jobName = methodName;
        capabilities.setCapability("name", jobName);
        
        if (environment == "browser") {
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("version", version);
            capabilities.setCapability("platform", os);
            capabilities.setCapability("extendedDebugging", true);
            capabilities.setCapability("capturePerformance", true);
        } else {
            capabilities.setCapability("platformName", os);
            capabilities.setCapability("deviceName", device);
        }


        //Creates Selenium Driver
        webDriver.set(new RemoteWebDriver(
                new URL("https://ondemand." + System.getenv().getOrDefault("SAUCE_REGION", "us-west-1") + ".saucelabs.com:443/wd/hub"),
                capabilities));

        //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
       String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);

        //For CI plugins
        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
        System.out.println(message);

        return webDriver.get();
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        boolean status = result.isSuccess();
        ((JavascriptExecutor)webDriver.get()).executeScript("sauce:job-result="+ status);
        webDriver.get().quit();
    }

    /**
     * Runs a simple test verifying the title of the wikipedia.org home page.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */

    @Test(dataProvider = "hardCodedBrowsers")
    public void BofAPageTitle(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.bankofamerica.com/");
        assertEquals(driver.getTitle(), "Bank of America - Banking, Credit Cards, Loans and Merrill Investing");
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void BofAFunc(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.bankofamerica.com/smallbusiness/");
        assertEquals(driver.getTitle(), "Small Business Banking, Credit Cards & Loans – Bank of America");
        driver.get("https://www.ml.com/wealthmanagement.html");
        assertEquals(driver.getTitle(), "Wealth Management Services & Wealth Planning from Bank of America | Merrill Lynch");
        driver.get("https://about.bankofamerica.com/en");
        assertEquals(driver.getTitle(), "About Bank of America - Our People, Our Passion, Our Purpose");
        driver.get("https://business.bofa.com/content/boaml/en_us/home.html");
        assertEquals(driver.getTitle(), "Bank of America Merrill Lynch is Now Bank of America & BofA Securities");
    }

    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }
}
