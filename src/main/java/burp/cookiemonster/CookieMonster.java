package burp.cookiemonster;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

/**
 * Cookie Monster - A Burp Suite extension that dynamically removes particular cookies from all requests.
 *
 * This extension provides:
 * - Dynamic cookie filtering for all requests passing through Burp Suite
 * - User-friendly interface to manage blocked cookie names
 * - Thread-safe operation across all Burp tools
 *
 * @author Cookie Monster Extension
 * @version 1.0.0
 */
public class CookieMonster implements BurpExtension {
    private static final String EXTENSION_NAME = "Cookie Monster";
    private static final String TAB_TITLE = "Cookie Monster";

    @Override
    public void initialize(MontoyaApi api) {
        // Set extension name
        api.extension().setName(EXTENSION_NAME);

        // Log initialization
        api.logging().logToOutput(EXTENSION_NAME + " is starting...");

        // Create the cookie blocklist manager
        CookieBlocklistManager blocklistManager = new CookieBlocklistManager();

        // Register HTTP handler to intercept and filter cookies
        CookieFilterHandler filterHandler = new CookieFilterHandler(blocklistManager, api);
        api.http().registerHttpHandler(filterHandler);
        api.logging().logToOutput(EXTENSION_NAME + ": HTTP handler registered");

        // Create and register the UI
        CookieMonsterUI ui = new CookieMonsterUI(blocklistManager);
        api.userInterface().registerSuiteTab(TAB_TITLE, ui);
        api.logging().logToOutput(EXTENSION_NAME + ": UI tab registered");

        // Log successful initialization
        api.logging().logToOutput(EXTENSION_NAME + " loaded successfully!");
        api.logging().logToOutput("Use the '" + TAB_TITLE + "' tab to manage blocked cookies");
    }
}
