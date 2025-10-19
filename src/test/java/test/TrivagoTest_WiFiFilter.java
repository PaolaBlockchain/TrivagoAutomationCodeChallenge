package test;

import org.testng.annotations.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import PageObject.BasePage;
import PageObject.HomeTrivagoPageObject;
import PageObject.HotelsResult;

public class TrivagoTest_WiFiFilter extends BasePage {

    private HomeTrivagoPageObject home;
    private HotelsResult results;

    /** Open browser once per class and prepare Page Objects */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        setup();                         // BasePage: inicializa driver, abre URL, maximiza, etc.
        home = new HomeTrivagoPageObject(driver);
        results = new HotelsResult(driver);
    }

    /**
     * Initial search using parameters from XML (with @Optional defaults to allow single-test runs).
     */
    @Parameters({ "location", "sizeDescription", "numberOfMonths" })
    @Test
    public void findLocation(
            @Optional("Cork") String location,
            @Optional("Double room") String sizeDescription,
            @Optional("1") int numberOfMonths
    ) throws IOException {
        System.out.println("\n=== TC: findLocation ===");
        home.imputLocation(location);
        home.selectCheckInDate();
        home.selectCheckOutDate(numberOfMonths);
        home.selectRoomSize(sizeDescription);
        assertTrue(home.searchButton(), "Search button was not clicked / not visible");
    }

    /**
     * Uses DataProvider (negative assertion): after applying the filter, the hotel should NOT be present.
     * NOTE: do NOT mix @Parameters here; DataProvider supplies both args.
     */
    @Test(dataProvider = "getDataFalse", dependsOnMethods = "findLocation")
    public void showResultsWithFiltersFromDataProviderFalse(String nameHotel, String nameFilter) {
        System.out.println("\n=== TC: showResultsWithFiltersFromDataProviderFalse ===");
        assertTrue(results.applyFilter(nameFilter), "Filter could not be applied: " + nameFilter);
        assertFalse(results.IsNameHotelList(nameHotel, nameFilter),
                "Unexpectedly found hotel in filtered list: " + nameHotel);
    }

    /**
     * Reset the active filter (runs after the negative case).
     */
    @Test(dependsOnMethods = "showResultsWithFiltersFromDataProviderFalse")
    public void resetFilter() {
        System.out.println("\n=== TC: resetFilter ===");
        assertTrue(results.cleanFilter(), "Filter could not be reset");
    }

    /**
     * Uses DataProvider (positive assertion): after applying the filter, the hotel SHOULD be present.
     */
    @Test(dataProvider = "getDataTrue", dependsOnMethods = "resetFilter")
    public void showResultsWithFiltersFromDataProviderTrue(String nameHotel, String nameFilter) {
        System.out.println("\n=== TC: showResultsWithFiltersFromDataProviderTrue ===");
        assertTrue(results.applyFilter(nameFilter), "Filter could not be applied: " + nameFilter);
        assertTrue(results.IsNameHotelList(nameHotel, nameFilter),
                "Expected hotel not found in filtered list: " + nameHotel);
    }

    /* ---------------- Data Providers ---------------- */

    @DataProvider
    public Object[][] getDataFalse() {
        return new Object[][]{
                { "Jurys Inn Cork", "Free WiFi" }
        };
    }

    @DataProvider
    public Object[][] getDataTrue() {
        return new Object[][]{
                { "Cork International Hotel", "Free WiFi" }
        };
    }

    /** Close browser once per class */
    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        System.out.println("\n=== END OF SUITE ===\n");
        finish();                        // BasePage: driver.quit();
    }
}
