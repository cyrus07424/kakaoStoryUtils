package constants;

import org.openqa.selenium.Cookie;

import enums.BrowserType;

/**
 * 環境設定.
 *
 * @author cyrus
 */
public interface Configurations {

	/**
	 * PhantomJSの実行ファイルのパス.
	 */
	String PHANTOMJS_EXECUTABLE_PATH = "./drivers/phantomjs.exe";

	/**
	 * GeckoDriverの実行ファイルのパス.
	 */
	String GECKO_DRIVER_EXECUTABLE_PATH = "./drivers/geckodriver.exe";

	/**
	 * ChromeDriverの実行ファイルのパス.
	 */
	String CHROME_DRIVER_EXECUTABLE_PATH = "./drivers/chromedriver.exe";

	/**
	 * 使用するブラウザの種類.
	 */
	BrowserType USE_BROWSER_TYPE = BrowserType.PHANTOMJS;

	/**
	 * ヘッドレスモードを使用するかどうか.
	 */
	boolean USE_HEADLESS_MODE = true;

	/**
	 * 使用するユーザーエージェント.
	 */
	String USE_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36";

	/**
	 * カカオストーリーのメールアドレス.
	 */
	String KAKAO_STORY_EMAIL = "";

	/**
	 * カカオストーリーのパスワード.
	 */
	String KAKAO_STORY_PASSWORD = "";

	/**
	 * カカオストーリーのログイン機能を使用するかどうか.
	 */
	boolean USE_KAKAOSTORY_LOGIN = false;

	/**
	 * カカオストーリーのログイン済みクッキー情報を使用するかどうか.
	 */
	boolean USE_KAKAOSTORY_LOGIN_COOKIE = true;

	/**
	 * カカオストーリーのログイン済みクッキー情報一覧.
	 */
	Cookie[] KAKAOSTORY_LOGIN_COOKIE_ARRAY = new Cookie[] {
			new Cookie("TIARA",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
			new Cookie("_kadu",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
			new Cookie("_kawlt",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
			new Cookie("_karmt",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
			new Cookie("_kawltea",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
			new Cookie("_karmtea",
					"CHANGE ME",
					".kakao.com", "", null, false, true),
	};

	/**
	 * 使用するダウンロード対象のユーザー名一覧.<br>
	 * 空の場合は動的に取得.
	 */
	String[] USE_TARGET_USESRNAME_ARRAY = new String[] {};
}