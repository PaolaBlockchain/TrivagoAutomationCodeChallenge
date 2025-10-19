# 🧭 Trivago UI Automation Project

## Overview
This project automates functional testing of the **Trivago** website using **Java, Selenium WebDriver, and TestNG** within a **Maven** structure.  
It validates search functionality and hotel filtering features such as **Wi-Fi availability** and **Spa facilities**, demonstrating the use of **Page Object Model (POM)** and **explicit waits**.

---

## 🏗️ Project Structure

```
TrivagoTest/
│
├── pom.xml                        # Maven configuration file (dependencies, plugins)
├── src/
│   ├── main/
│   │   ├── java/PageObject/
│   │   │   ├── BasePage.java              # Initializes WebDriver and shared setup
│   │   │   ├── HomeTrivagoPageObject.java # Page Object for the home search page
│   │   │   ├── HotelsResult.java          # Page Object for results and filters
│   │   │   └── SmartWaits.java            # Custom wait utilities (explicit waits)
│   │   └── resources/
│   │       └── testngTrivago.xml          # TestNG suite configuration
│   └── test/
│       ├── java/test/
│       │   ├── TrivagoTest_WiFiFilter.java # Tests Wi-Fi filter
│       │   └── TrivagoTest_SpaFilter.java  # Tests Spa filter
│
└── screenshots/
    └── (optional evidence images)
```

---

## ⚙️ Prerequisites

Before running the tests, ensure you have:

- **Java JDK 17+** installed and configured (`java -version`)
- **Maven 3.8+** (`mvn -v`)
- **Google Chrome** (latest version)
- **ChromeDriver** automatically handled by Selenium 4
- Internet access to open [https://www.trivago.com](https://www.trivago.com)

---

## 📦 Dependencies

All dependencies are managed via `pom.xml`:

- `selenium-java:4.23.0`
- `testng:7.10.2`
- Maven Compiler Plugin → Java 17
- Maven Surefire Plugin → runs TestNG suite

(See [`pom.xml`](./pom.xml) for exact versions.)

---

## 🚀 How to Run the Tests

### Option 1 — From Terminal (Maven)
```bash
# Clean and compile
mvn clean test
```

This command automatically runs the TestNG suite defined in  
`src/main/resources/testngTrivago.xml`.

### Option 2 — From IntelliJ IDEA
1. Open the project as a **Maven Project**.
2. Wait until dependencies download.
3. Navigate to `testngTrivago.xml`.
4. Right-click → **Run 'testngTrivago.xml'**.

---

## 🧩 Test Scenarios

| Test Class | Description | Parameters |
|-------------|-------------|-------------|
| **TrivagoTest_WiFiFilter.java** | Searches for hotels in a given location and verifies that Wi-Fi filter can be applied successfully. | `location`, `sizeDescription`, `numberOfMonths` (from XML) |
| **TrivagoTest_SpaFilter.java** | Performs similar search but applies Spa filter validation. | `location`, `sizeDescription`, `numberOfMonths` |

Each test:
1. Opens Trivago homepage.
2. Searches for a location.
3. Applies the respective filter (Wi-Fi or Spa).
4. Asserts that results are displayed accordingly.

---

## 🧠 Key Concepts Demonstrated

- **Page Object Model (POM)**: Keeps locators and actions separated from test logic for maintainability.  
- **Explicit Waits (SmartWaits.java)**: Handles synchronization with dynamic page loads.  
- **Parameterized Tests** via `testng.xml`.  
- **Cross-browser ready** (can easily integrate Edge/Firefox drivers).  
- **Reusable BasePage setup** for all test cases.  

---

## 🧪 Sample TestNG XML Configuration

```xml
<suite name="Trivago Test Suite" verbose="1" parallel="false">
  <test name="WiFi Filter Test">
    <parameter name="location" value="Dublin" />
    <parameter name="sizeDescription" value="2 adults" />
    <parameter name="numberOfMonths" value="2" />
    <classes>
      <class name="test.TrivagoTest_WiFiFilter" />
    </classes>
  </test>

  <test name="Spa Filter Test">
    <parameter name="location" value="Madrid" />
    <parameter name="sizeDescription" value="2 adults" />
    <parameter name="numberOfMonths" value="3" />
    <classes>
      <class name="test.TrivagoTest_SpaFilter" />
    </classes>
  </test>
</suite>
```

---

## 🧾 Example Output

Console output shows:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running TestSuite
//******************************************START OF THE TEST SUITE***********************************************************************//
TC: Start findLocation test--------------------------------------
...
PASSED: WiFi Filter applied successfully
PASSED: Spa Filter applied successfully
[INFO] BUILD SUCCESS
```

---

## 🧰 Future Improvements

- Add screenshot capture on test failure.
- Integrate with **Allure** or **ExtentReports** for HTML reports.
- Add **Dockerized Selenium Grid** for parallel execution.
- Implement **data-driven testing** using CSV or Excel files.

---

## 👩‍💻 Author
**Paola Lucero**  
QA Automation Engineer  
> Project developed as part of a Selenium + TestNG automation exercise.
