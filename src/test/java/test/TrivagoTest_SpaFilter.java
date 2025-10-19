package test;

import org.testng.annotations.*;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import PageObject.BasePage;
import PageObject.HomeTrivagoPageObject;
import PageObject.HotelsResult;

/**
 * Trivago SPA Filter E2E tests.
 * - Uses BasePage for WebDriver lifecycle.
 * - Reads default URL/browser from global.properties (via BasePage).
 * - Parameters come from testngTrivago.xml; @Optional provides sane defaults if you run a single test from IDE.
 */
public class TrivagoTest_SpaFilter extends BasePage {

    private HomeTrivagoPageObject home;
    private HotelsResult results;

    /** Open browser and navigate once per class */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception {
        setup();                         // BasePage: initializes driver, opens URL, maximizes, waits, etc.
        home = new HomeTrivagoPageObject(driver);
        results = new HotelsResult(driver);
    }

    /**
     * Receives parameters from XML and performs the initial search.
     */
    @Parameters({ "location", "sizeDescription", "numberOfMonths" })
    @Test
    public void findLocation(
            @Optional("Cork") String location,
            @Optional("Double room") String sizeDescription,
            @Optional("1") int numberOfMonths
    ) {
        System.out.println("\n=== TC: findLocation ===");
        home.imputLocation(location);
        home.selectCheckInDate();
        home.selectCheckOutDate(numberOfMonths);
        home.selectRoomSize(sizeDescription);
        assertTrue(home.searchButton(), "Search button was not clicked / not visible");
    }

    /**
     * Shows all hotel results (no filter yet).
     */
    @Test(dependsOnMethods = "findLocation")
    public void showResultsWithOutFilters() {
        System.out.println("\n=== TC: showResultsWithOutFilters ===");
        results.showResult();
    }

    /**
     * Applies the SPA filter.
     */
    @Parameters({ "nameFilter" })
    @Test(dependsOnMethods = "showResultsWithOutFilters")
    public void applyFilters(@Optional("Spa") String nameFilter) {
        System.out.println("\n=== TC: applyFilters ===");
        System.out.println("...before filter");
        results.showResult();

        assertTrue(results.applyFilter(nameFilter), "Filter could not be applied: " + nameFilter);

        System.out.println("...after filter");
        results.showResult();
    }

    /**
     * Verifies a hotel NOT present after applying the filter.
     */
    @Parameters({ "nameHotel2Spa", "nameFilter" })
    @Test(dependsOnMethods = "applyFilters")
    public void showResultsWithFiltersFromXMLFileFalse(
            @Optional("Jurys Inn Cork") String nameHotel2Spa,
            @Optional("Spa") String nameFilter
    ) {
        System.out.println("\n=== TC: showResultsWithFiltersFromXMLFileFalse ===");
        assertFalse(results.IsNameHotelList(nameHotel2Spa, nameFilter),
                "Unexpectedly found hotel in filtered list: " + nameHotel2Spa);
    }

    /**
     * Verifies a hotel IS present after applying the filter.
     */
    @Parameters({ "nameHotel1Spa", "nameFilter" })
    @Test(dependsOnMethods = "applyFilters")
    public void showResultsWithFiltersFromXMLFileTrue(
            @Optional("The River Lee") String nameHotel1Spa,
            @Optional("Spa") String nameFilter
    ) {
        System.out.println("\n=== TC: showResultsWithFiltersFromXMLFileTrue ===");
        assertTrue(results.IsNameHotelList(nameHotel1Spa, nameFilter),
                "Expected hotel not found in filtered list: " + nameHotel1Spa);
    }

    /** Close browser once per class */
    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        System.out.println("\n=== END OF SUITE ===\n");
        finish();                        // BasePage: driver.quit();
    }
}
