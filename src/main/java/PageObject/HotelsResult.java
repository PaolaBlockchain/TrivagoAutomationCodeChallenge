package PageObject;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import PageObject.helpers.SmartWaits;

public class HotelsResult {

    private final WebDriver driver;
    private final SmartWaits waits;

    public HotelsResult(WebDriver driver) {
        this.driver = driver;
        this.waits = new SmartWaits(driver);
    }

    /* ===========================
       Locators
       =========================== */

    private final By containerResult     = By.xpath("//*[@id='main_content']/div[4]");
    private final By containerToolBar    = By.className("filter-toolbar");
    private final By containerSuggestion = By.xpath(
            "//*[@id='page_wrapper']/section/div/div/ul/li[5]/div/div/section/div/div[1]/div/div/div[2]/div"
    );
    private final By containerFooter     = By.className("refinement-row__actions");

    private final By byNameLocator_className = By.className("item__details");
    private final By byNameLocator_li        = By.tagName("li");
    private final By byNameLocator_H3        = By.tagName("h3");
    private final By byNameLocator_span      = By.tagName("span");
    private final By byNameLocator_id        = By.id("undefined-input");

    private final By buttonDone  = By.xpath("//*[@id='page_wrapper']/section/div/div/ul/li[5]/div/div/section/div/footer/button[2]");
    private final By buttonReset = By.xpath("//*[@id='page_wrapper']/section/div/div/ul/li[5]/div/div/section/div/footer/button[1]");

    private final By logoName = By.id("js_navigation");
    private final By lostFocus = By.xpath("//*[@id='js-fullscreen-hero']/div/div/2]/form/div/div/div[3]/button/span[2]/span");

    /* ===========================
       Actions
       =========================== */

    /**
     * Return all hotel names currently listed (before/after filters).
     */
    public ArrayList<String> showResult() {
        ArrayList<String> nameHotelList = new ArrayList<>();

        // Wait until the results container is visible
        WebElement results = waits.waitVisible(containerResult, 15);

        // Each result card contains an h3 with the hotel name
        List<WebElement> cards = results.findElements(byNameLocator_className);
        for (WebElement card : cards) {
            WebElement h3 = card.findElement(byNameLocator_H3);
            String name = h3.getText();
            if (name != null && !name.isBlank()) {
                System.out.println(name);
                nameHotelList.add(name);
            }
        }
        return nameHotelList;
    }

    /**
     * Verify that a given hotel name appears in the results after applying a filter.
     */
    public boolean IsNameHotelList(String nameHotel, String nameFilter) {
        boolean found = false;
        for (String name : showResult()) {
            if (name.contains(nameHotel)) {
                System.out.println("The hotel searched is: '" + nameHotel + "' and it IS in the list. Filter: " + nameFilter);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("The hotel searched is: '" + nameHotel + "' and it is NOT in the list. Filter: " + nameFilter);
        }
        return found;
    }

    /**
     * Apply a filter by typing into the filter input and selecting the suggestion.
     */
    public boolean applyFilter(String nameFilter) {
        boolean openedSelector = false;

        // Open the filter toolbar and click the "Select" span
        WebElement toolbar = waits.waitVisible(containerToolBar, 10);
        List<WebElement> spans = toolbar.findElements(byNameLocator_span);
        for (WebElement span : spans) {
            if ("Select".equalsIgnoreCase(span.getText())) {
                if (span.isDisplayed()) {
                    span.click();
                    openedSelector = true;
                }
                break;
            }
        }

        // Type the filter text
        waits.waitVisible(byNameLocator_id, 10).sendKeys(nameFilter);

        // Pick the suggestion and return whether both steps succeeded
        boolean picked = findElementInContainer(containerSuggestion, byNameLocator_li, nameFilter, lostFocus);
        return openedSelector && picked;
    }

    /**
     * Find an item inside a container and click it; then click an action button if present.
     */
    public boolean findElementInContainer(By nameContainer, By nameLocatorInContainer, String stringToFind, By buttonActions) {
        WebElement container = waits.waitVisible(nameContainer, 10);
        List<WebElement> items = container.findElements(nameLocatorInContainer);

        for (WebElement item : items) {
            String text = item.getText();
            if (stringToFind.equals(text)) {
                if (item.isDisplayed()) {
                    item.click();

                    // Click the provided action (e.g., lostFocus/button) if visible
                    WebElement action = waits.waitVisible(buttonActions, 10);
                    if (action.isDisplayed() && action.isEnabled()) {
                        action.click();

                        // Ensure results re-render before returning
                        waits.waitVisible(containerResult, 25);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    /**
     * Check whether "(1) Filter" label is visible on the toolbar.
     */
    public boolean isFilterApplied() {
        WebElement toolbar = waits.waitVisible(containerToolBar, 10);
        List<WebElement> spans = toolbar.findElements(byNameLocator_span);
        for (WebElement span : spans) {
            if ("(1) Filter".equalsIgnoreCase(span.getText())) {
                System.out.println("Filter was applied");
                return true;
            }
        }
        return false;
    }

    /**
     * Reset the active filter (if any).
     */
    public boolean cleanFilter() {
        if (isFilterApplied()) {
            // Click the "(1) Filter" badge then hit Reset
            findElementInContainer(containerToolBar, byNameLocator_span, "(1) Filter", buttonReset);
            waits.waitVisible(lostFocus, 10).click();
            System.out.println("Filter reset");
            return true;
        }
        return false;
    }
}
