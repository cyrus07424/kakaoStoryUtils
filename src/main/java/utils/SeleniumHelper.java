package utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import constants.Configurations;

/**
 * Seleniumヘルパー.
 *
 * @author cyrus
 */
public class SeleniumHelper {

	/**
	 * WebDriverを取得.
	 *
	 * @return
	 */
	public static WebDriver getWebDriver() {
		WebDriver driver = null;
		switch (Configurations.USE_BROWSER_TYPE) {
		case CHROME: {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
					Configurations.CHROME_DRIVER_EXECUTABLE_PATH);
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.setHeadless(Configurations.USE_HEADLESS_MODE);
			chromeOptions.addArguments("--disable-dev-shm-usage");
			chromeOptions.addArguments("--no-sandbox");
			chromeOptions.addArguments("--user-agent=" + Configurations.USE_UA);
			Map<String, Object> chromePrefs = new HashMap<>();
			chromePrefs.put("download.prompt_for_download", false);
			chromeOptions.setExperimentalOption("prefs", chromePrefs);
			driver = new ChromeDriver(chromeOptions);
			break;
		}
		case FIREFOX: {
			System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
					Configurations.GECKO_DRIVER_EXECUTABLE_PATH);
			FirefoxProfile firefoxProfile = new FirefoxProfile();
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.setHeadless(Configurations.USE_HEADLESS_MODE);
			firefoxOptions.setProfile(firefoxProfile);
			driver = new FirefoxDriver(firefoxOptions);
			break;
		}
		case EDGE: {
			System.setProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY,
					Configurations.EDGE_DRIVER_EXECUTABLE_PATH);
			driver = new EdgeDriver();
			break;
		}
		default: {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					Configurations.PHANTOMJS_EXECUTABLE_PATH);
			driver = new PhantomJSDriver(capabilities);
			break;
		}
		}

		// タイムアウトを設定
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		// クッキーを設定
		if (Configurations.USE_KAKAOSTORY_LOGIN_COOKIE) {
			// エラー防止の為画面遷移
			driver.get("https://story.kakao.com/");
			for (Cookie cookie : Configurations.KAKAOSTORY_LOGIN_COOKIE_ARRAY) {
				try {
					driver.manage().addCookie(cookie);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}

		return driver;
	}

	/**
	 * ページが完全に読み込まれるまで待機.<br>
	 * https://code-examples.net/ja/q/598b97
	 *
	 * @param webDriver
	 */
	public static void waitForBrowserToLoadCompletely(WebDriver webDriver) {
		String state;
		String oldstate;
		try {
			System.out.println("Waiting for browser loading to complete");
			int i = 0;
			while (i < 5) {
				Thread.sleep(1000);
				state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState;").toString();
				System.out.println("." + Character.toUpperCase(state.charAt(0)) + ".");
				if (state.equals("interactive") || state.equals("loading")) {
					break;
				}

				// If browser in 'complete' state since last X seconds. Return.
				if (i == 1 && state.equals("complete")) {
					System.out.println("complete");
					return;
				}
				i++;
			}
			i = 0;
			oldstate = null;
			Thread.sleep(2000);

			// Now wait for state to become complete
			while (true) {
				state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState;").toString();
				System.out.println("." + state.charAt(0) + ".");
				if (state.equals("complete")) {
					System.out.println("complete");
					break;
				}

				if (state.equals(oldstate)) {
					i++;
				} else {
					i = 0;
				}

				// If browser state is same (loading/interactive) since last 60
				// secs. Refresh the page.
				if (i == 15 && state.equals("loading")) {
					System.out.println("Browser in " + state + " state since last 60 secs. So refreshing browser.");
					webDriver.navigate().refresh();
					System.out.println("Waiting for browser loading to complete");
					i = 0;
				} else if (i == 6 && state.equals("interactive")) {
					System.out
							.println("Browser in " + state + " state since last 30 secs. So starting with execution.");
					return;
				}
				Thread.sleep(4000);
				oldstate = state;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}