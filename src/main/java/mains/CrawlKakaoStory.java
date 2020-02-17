package mains;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import constants.Configurations;
import utils.FileHelper;
import utils.SeleniumHelper;

/**
 * CrawlKakaoStory.
 *
 * @author cyrus
 */
public class CrawlKakaoStory {

	/**
	 * WebDriver.
	 */
	private static WebDriver webDriver;

	/**
	 * ログイン中であるかどうか.
	 */
	private static boolean loggedIn = false;

	/**
	 * main.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
		try {

			// WebDriverを取得
			webDriver = SeleniumHelper.getWebDriver();

			// ログイン
			if (Configurations.USE_KAKAOSTORY_LOGIN) {
				login();
			} else if (Configurations.USE_KAKAOSTORY_LOGIN_COOKIE) {
				webDriver.get("https://story.kakao.com/");
				SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);
				loggedIn = true;
			}

			// ログイン失敗の場合は終了
			if (!loggedIn) {
				throw new RuntimeException("login failed");
			}

			// タイムラインをクロール
			for (String username : getTargetUsernameSet()) {
				crawlUserTimeline(username);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// WebDriverを終了
			if (webDriver != null) {
				webDriver.quit();
			}
		}
		System.out.println("done");
	}

	/**
	 * ログイン.
	 */
	@Deprecated
	private static void login() {
		System.out.println("login");

		// ログイン画面を表示
		webDriver.get("https://accounts.kakao.com/login/kakaostory");
		SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);

		// ログイン情報を入力
		WebElement loginEmail = webDriver.findElement(By.id("id_email_2"));
		WebElement loginPw = webDriver.findElement(By.id("id_password_3"));
		loginEmail.sendKeys(Configurations.KAKAO_STORY_EMAIL);
		loginPw.sendKeys(Configurations.KAKAO_STORY_PASSWORD);

		// TODO captcha認証

		// ログインボタンをクリック
		webDriver.findElement(By.cssSelector("#login-form button.submit")).click();
		SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);

		if (StringUtils.equals(webDriver.getCurrentUrl(), "https://story.kakao.com/")) {
			loggedIn = true;
		}
	}

	/**
	 * 指定したユーザーのタイムラインをクロール.
	 *
	 * @param username
	 */
	private static void crawlUserTimeline(String username) {
		System.out.println("crawlUserTimeline : " + username);

		// ユーザーのタイムラインを表示
		webDriver.get("https://story.kakao.com/" + username);
		SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);

		// 処理済みモデル一覧
		Set<String> processedModel = new HashSet<>();

		// 無限ループ
		while (true) {
			// 処理済みフラグ
			boolean processed = false;

			// 全てのセクションに対して実行
			for (WebElement sectionWebElement : webDriver.findElements(By.cssSelector("div.section._activity"))) {
				// モデルを取得
				String model = sectionWebElement.getAttribute("data-model");

				// 処理済みでない場合
				if (!processedModel.contains(model)) {
					// セクション内の全ての画像に対して実行
					for (WebElement imageWebElement : sectionWebElement
							.findElements(By.cssSelector("img._mediaImage"))) {
						// 画像のURLを取得
						String imageUrl = imageWebElement.getAttribute("src");
						if (StringUtils.isNotEmpty(imageUrl)) {
							System.out.println(imageUrl);

							// 画像を保存
							FileHelper.saveContent(username, imageUrl);
						}
					}
					// 処理済みモデル一覧に追加
					processedModel.add(model);

					// 処理済みフラグを変更
					processed = true;
				}
			}

			// 一番下までスクロール
			executeJavascript("window.scrollTo(0, document.body.scrollHeight);");

			// スリープ
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 処理されなかった場合
			if (!processed) {
				// 処理を終了
				break;
			}
		}
	}

	/**
	 * ダウンロード対象のユーザー名一覧を取得.
	 *
	 * @return
	 */
	private static Set<String> getTargetUsernameSet() {
		Set<String> usernameSet = new HashSet<>();
		if (Configurations.USE_TARGET_USESRNAME_ARRAY != null && 0 < Configurations.USE_TARGET_USESRNAME_ARRAY.length) {
			usernameSet.addAll(Arrays.asList(Configurations.USE_TARGET_USESRNAME_ARRAY));
		} else {
			List<WebElement> friendWebElementList = webDriver
					.findElements(By.cssSelector(".tabcont_friend ul.list_myfriend > li"));
			for (WebElement friendWebElement : friendWebElementList) {
				usernameSet.add(friendWebElement.getAttribute("data-model"));
			}
		}
		return usernameSet;
	}

	/**
	 * Javascriptを実行.
	 *
	 * @param javascript
	 */
	private static void executeJavascript(String javascript) {
		((JavascriptExecutor) webDriver).executeScript(javascript);
	}
}
