package io.hexaforce.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import io.github.bonigarcia.wdm.OperaDriverManager;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;

// https://github.com/bonigarcia/webdrivermanager
// http://junit.org/junit5/docs/current/user-guide/#overview
// http://junit.org/junit5/docs/current/api/overview-summary.html
@RunWith(JUnitPlatform.class)
class StandardTests {

	private WebDriver driver;

	@BeforeAll
	static void initAll() {

		// System.setProperty("webdriver.chrome.driver",
		// "/absolute/path/to/binary/chromedriver");
		// System.setProperty("webdriver.gecko.driver",
		// "/absolute/path/to/binary/geckodriver");
		// System.setProperty("webdriver.opera.driver",
		// "/absolute/path/to/binary/operadriver");
		// System.setProperty("phantomjs.binary.path",
		// "/absolute/path/to/binary/phantomjs");
		// System.setProperty("webdriver.edge.driver",
		// "C:/absolute/path/to/binary/MicrosoftWebDriver.exe");
		// System.setProperty("webdriver.ie.driver",
		// "C:/absolute/path/to/binary/IEDriverServer.exe");

		ChromeDriverManager.getInstance().setup();
		// FirefoxDriverManager.getInstance().setup();
		// EdgeDriverManager.getInstance().setup();
		// InternetExplorerDriverManager.getInstance().setup();
		// OperaDriverManager.getInstance().setup();
		// PhantomJsDriverManager.getInstance().setup();

	}

	@BeforeEach
	void init() {
		driver = new ChromeDriver();
		// driver = new FirefoxDriver();
		// driver = new OperaDriver();
		// driver = new ChromeDriver();
		// driver = new EdgeDriver();
		// driver = new InternetExplorerDriver();

	}

	@Step
	public void checkThat2is2() {
		assertThat(2, is(2));
	}

	@Test
	public void simpleTestWithSteps() throws Exception {
		checkThat2is2();
	}

	@Step
	public void firstStep() {

	}

	@Test
	@Feature("Some feature")
	// @Severity(SeverityLevel.CRITICAL)
	public void testOutput() {
		firstStep();
	}

	@Test
	void succeedingTest() {

	}

	@Test
	void failingTest() {
		fail("a failing test");
	}

	@Test
	@Disabled("for demonstration purposes")
	void skippedTest() {
		// not executed
	}

	@AfterEach
	void tearDown() {

	}

	@AfterAll
	static void tearDownAll() {

	}

}
