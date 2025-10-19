package PageObject.helpers;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * SmartWaits utility class.
 * Provides reusable Selenium explicit and fluent waits with Duration (Selenium 4 style).
 */
public class SmartWaits {

    private final WebDriver driver;

    public SmartWaits(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Wait until a specific element becomes visible on the page.
     * @param locator  The By locator of the element
     * @param seconds  Timeout in seconds
     * @return The visible WebElement
     */
    public WebElement waitVisible(By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for an element to be clickable before returning it.
     */
    public WebElement waitClickable(By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Custom FluentWait — waits with polling intervals and ignores exceptions.
     */
    public WebElement fluentWait(By locator, long timeoutSeconds, long pollMillis) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollMillis))
                .ignoring(NoSuchElementException.class);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
