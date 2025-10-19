package PageObject;

import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import PageObject.helpers.SmartWaits;

public class HomeTrivagoPageObject {

    private final WebDriver driver;
    private final SmartWaits waits;

    public HomeTrivagoPageObject(WebDriver driver) {
        this.driver = driver;
        // Inject SmartWaits AFTER driver is available
        this.waits = new SmartWaits(driver);
    }

    /* ===========================
       Locators
       =========================== */

    // Location field / autocomplete
    private final By location_id = By.id("horus-querytext");
    private final By byNameLocator_span = By.tagName("span");
    private final By focus = By.className("btn-horus__value");

    // Dropdown list (autocomplete results)
    private final By containerResult = By.xpath("//*[@id='js-fullscreen-hero']/div/div[2]/form/div/div/div[1]/div[2]/div/div");
    private final By byNameLocator_ul = By.tagName("ul");
    private final By byNameLocator_li = By.tagName("li");

    // Calendar / datepickers
    private final By byNameLocator_td = By.tagName("td");
    private final By datepickerTable = By.className("cal-month");
    private final By buttonNext = By.className("cal-btn-next");

    // Room size
    private final By containerMenuRoom = By.xpath("//*[@id='js-fullscreen-hero']/div/div[2]/form/div[2]");
    private final By size = By.className("roomtype-btn__label");

    // Search button
    private final By searchButton = By.xpath("//*[@id='js-fullscreen-hero']/div/div[2]/form/div/div/div[3]/button");

    /* ===========================
       Actions
       =========================== */

    /**
     * Type a location and choose a suggestion from the dropdown.
     */
    public void imputLocation(String location) {
        // Focus and type
        waits.waitVisible(location_id, 10).click();
        driver.findElement(location_id).clear();
        driver.findElement(location_id).sendKeys(location);

        // Select from autocomplete
        selectElementDropDownList(location);
    }

    /**
     * Pick the first dropdown item that contains the given location text.
     * Defensive against stale list structure and off-by-one errors.
     */
    public void selectElementDropDownList(String location) {
        WebElement resultsContainer = waits.waitVisible(containerResult, 10)
                .findElement(byNameLocator_ul);

        List<WebElement> items = resultsContainer.findElements(byNameLocator_li);

        for (WebElement item : items) {
            List<WebElement> spans = item.findElements(byNameLocator_span);
            for (int i = 0; i < spans.size(); i++) {            // <--- fixed: i < size (not <=)
                String text = spans.get(i).getText();
                if (text != null && text.toLowerCase().contains(location.toLowerCase())) {
                    item.click();
                    return;                                      // Stop after selecting the match
                }
            }
        }
        // Optional: throw if nothing matched (helps tests fail fast with a clear reason)
        // throw new IllegalStateException("No dropdown option contained: " + location);
    }

    /**
     * Select today's date in the "check-in" calendar.
     * If current time is after 18:00, it selects tomorrow (see getCurrentDay()).
     */
    public void selectCheckInDate() {
        WebElement calendarTable = waits.waitVisible(datepickerTable, 10);
        List<WebElement> cells = calendarTable.findElements(byNameLocator_td);

        String targetDay = getCurrentDay(); // today or tomorrow after 18:00
        for (WebElement cell : cells) {
            if (targetDay.equals(cell.getText())) {
                cell.click();
                return;
            }
        }
        // If not found, you might want to click "next" and retry, depending on UI behavior.
        // throw new IllegalStateException("Check-in day not found in current month: " + targetDay);
    }

    /**
     * Select check-out date by moving N months forward (clicking the “next” arrow),
     * then picking the same day-of-month as returned by getCurrentDay().
     *
     * @param numberOfMonths how many months to move forward before selecting the day
     */
    public void selectCheckOutDate(int numberOfMonths) {
        // Move forward N months if needed
        for (int m = 0; m < numberOfMonths; m++) {
            WebElement next = waits.waitVisible(buttonNext, 10);
            if (next.isDisplayed()) {
                next.click();
                // small stability wait to allow calendar to re-render
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }
        }

        WebElement calendarTable = waits.waitVisible(datepickerTable, 10);
        List<WebElement> cells = calendarTable.findElements(byNameLocator_td);

        String targetDay = getCurrentDay();
        for (WebElement cell : cells) {
            if (targetDay.equals(cell.getText())) {
                cell.click();
                return;
            }
        }
        // throw new IllegalStateException("Check-out day not found after moving " + numberOfMonths + " months.");
    }

    /**
     * Compute the label for "today" unless it's after 18:00 local time;
     * in that case, returns "tomorrow" (day-of-month as String).
     */
    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour > 18) {
            return getDayPlus(1); // tomorrow
        } else {
            return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * Day-of-month after adding N days (used for “tomorrow” logic).
     * NOTE: despite the old name in your version, this is days, not months.
     */
    private String getDayPlus(int days) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Select the desired room size from the room menu container.
     */
    public void selectRoomSize(String sizeDescription) {
        WebElement menu = waits.waitVisible(containerMenuRoom, 10);
        List<WebElement> options = menu.findElements(byNameLocator_li);

        for (WebElement option : options) {
            String text = option.getText();
            if (text != null && text.trim().equalsIgnoreCase(sizeDescription.trim())) {
                option.click();
                return;
            }
        }
        // throw new IllegalStateException("Room size not found: " + sizeDescription);
    }

    /**
     * Click the Search button (returns true if clicked).
     */
    public boolean searchButton() {
        WebElement btn = waits.waitVisible(searchButton, 10);
        if (btn.isDisplayed() && btn.isEnabled()) {
            btn.click();
            return true;
        }
        return false;
    }
}
