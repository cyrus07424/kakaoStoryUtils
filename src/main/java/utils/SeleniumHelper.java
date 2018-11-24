package utils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
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
				// FIXME ヘッドレス対応
				System.setProperty("webdriver.chrome.driver", Configurations.CHROME_DRIVER_EXECUTABLE_PATH);
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setHeadless(true);
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
				System.setProperty("webdriver.gecko.driver", Configurations.GECKO_DRIVER_EXECUTABLE_PATH);
				FirefoxProfile firefoxProfile = new FirefoxProfile();
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setHeadless(Configurations.USE_HEADLESS_MODE);
				firefoxOptions.setProfile(firefoxProfile);
				driver = new FirefoxDriver(firefoxOptions);
				break;
			}
			default: {
				if (!new File(Configurations.PHANTOMJS_EXECUTABLE_PATH).exists()) {
					downloadPhantomJs();
				}
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setCapability(
						PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
						Configurations.PHANTOMJS_EXECUTABLE_PATH);
				capabilities.setJavascriptEnabled(true);
				driver = new PhantomJSDriver(capabilities);
				break;
			}
		}

		// タイムアウトを設定
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

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
	 * PhantomJSをダウンロード.<br>
	 * windows用.
	 */
	private static void downloadPhantomJs() {
		try {
			System.out.println("downloading phantomjs");

			// 一時ファイルを作成
			File tempZipFile = File.createTempFile("prefix", "suffix");

			// 一時ファイルに保存
			FileUtils.copyURLToFile(
					new URL("https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-windows.zip"),
					tempZipFile);

			// ZIPを展開
			System.out.println("inflating phantomjs");
			try (ZipInputStream zipInputStream = new ZipInputStream(FileUtils.openInputStream(tempZipFile))) {
				ZipEntry zipEntry;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					if (StringUtils.contains(zipEntry.getName(), "phantomjs.exe")) {
						File outputFile = new File(Configurations.PHANTOMJS_EXECUTABLE_PATH);
						Files.createDirectories(outputFile.getParentFile().toPath());
						FileUtils.copyToFile(zipInputStream, outputFile);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}