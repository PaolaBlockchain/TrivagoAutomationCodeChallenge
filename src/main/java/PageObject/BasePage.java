package PageObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static java.time.Duration.*;

/**
 * BasePage class
 * ----------------------------
 * This class is responsible for:
 *  - Reading configuration from global.properties
 *  - Initializing the WebDriver based on the selected browser
 *  - Opening the target URL and setting up browser settings
 *  - Closing and cleaning up after tests
 *
 * Using Selenium 4.6+ (Selenium Manager automatically manages browser drivers)
 */
public class BasePage {

    // Shared WebDriver instance accessible by subclasses
    protected static WebDriver driver = null;

    // Properties object to load configuration from global.properties
    protected static Properties p = new Properties();

    /**
     * Setup method
     * ----------------------------
     * Loads configuration, initializes WebDriver, and opens the target URL.
     */
    public void setup() throws IOException {
        // Load configuration file from resources folder (src/main/resources)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("global.properties")) {
            if (is == null) {
                throw new IOException("global.properties file not found in src/main/resources");
            }
            p.load(is);
        }

        // Read the browser name from global.properties (default = "chrome")
        String browser = p.getProperty("browser", "chrome").toLowerCase();

        // Initialize the correct WebDriver instance based on browser type
        switch (browser) {
            case "chrome":
                // Selenium Manager automatically finds or downloads the ChromeDriver
                driver = new ChromeDriver();
                break;

            case "firefox":
                driver = new FirefoxDriver();
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        // Set implicit wait timeout (replaces deprecated TimeUnit approach)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Maximize browser window
        driver.manage().window().maximize();

        // Navigate to the target URL defined in the properties file
        driver.get(p.getProperty("url"));
    }

    /**
     * Finish method
     * ----------------------------
     * Safely closes and quits the WebDriver instance.
     */
    public void finish() {
        if (driver != null) {
            try {
                driver.quit();  // Closes all browser windows and ends the session
            } catch (Exception ignored) {
            }
            driver = null;
        }
    }
}
