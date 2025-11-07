# Cookie Monster - Burp Suite Extension

A Burp Suite extension that dynamically removes specific cookies from all HTTP requests passing through Burp Suite and its modules.

## Features

- **Dynamic Cookie Filtering**: Automatically removes blocked cookies from all requests
- **Domain-Based Filtering**: Choose to filter cookies on all domains, in-scope domains only, or custom domain lists
- **User-Friendly Interface**: Simple split-pane GUI to manage blocked cookie names and domain filtering
- **Works Across All Burp Tools**: Filters cookies in Proxy, Scanner, Intruder, Repeater, and all other Burp modules
- **Thread-Safe**: Handles concurrent requests safely
- **Real-Time Updates**: Add or remove blocked cookies on the fly without restarting
- **Subdomain Support**: Custom domain filtering automatically includes subdomains

## Installation

1. Open Burp Suite
2. Go to the **Extensions** tab
3. Click **Add** under the Extensions section
4. Set **Extension type** to "Java"
5. Click **Select file** and choose `target/cookie-monster.jar`
6. Click **Next** to load the extension

The extension should load successfully and you'll see "Cookie Monster" appear in the extensions list.

## Usage

The extension interface is divided into two main panels:

### Left Panel: Blocked Cookies

#### Adding Cookies to Block

1. Navigate to the **Cookie Monster** tab in Burp Suite
2. In the left panel, enter the name of the cookie you want to block in the "Cookie Name" field
3. Click **Add Cookie** or press Enter
4. The cookie will appear in the blocked cookies list

#### Removing Blocked Cookies

1. Select one or more cookies from the blocked cookies list
2. Click **Remove Selected**
3. The selected cookies will no longer be filtered

#### Clearing All Blocked Cookies

1. Click **Clear All**
2. Confirm the action when prompted
3. All blocked cookies will be removed from the list

### Right Panel: Domain Filtering

The extension offers three domain filtering modes:

#### 1. All Domains (Default)

- Removes blocked cookies from **all requests** regardless of domain
- No additional configuration required
- Best for general testing scenarios

#### 2. In-Scope Only

- Removes blocked cookies **only from domains in Burp's scope**
- Uses Burp's built-in scope settings
- Ideal when you want to focus on specific targets
- Configure scope in Burp's **Target** → **Scope** tab

#### 3. Custom Domain List

- Removes blocked cookies **only from specified domains**
- Supports subdomain matching (e.g., adding `example.com` will match `www.example.com`, `api.example.com`, etc.)
- When this mode is selected, the custom domain list becomes active

##### Managing Custom Domains

1. Select **Custom Domain List** radio button
2. Enter a domain name (e.g., `example.com`) in the Domain field
3. Click **Add Domain** or press Enter
4. To remove domains, select them and click **Remove Selected**
5. Use **Clear All** to remove all custom domains

**Note**: Enter domains without protocol (use `example.com`, not `https://example.com`)

## How It Works

The extension registers an HTTP handler that intercepts all requests before they are sent. For each request:

1. Checks if the request should be processed based on the domain filtering mode:
   - **All Domains**: Process all requests
   - **In-Scope Only**: Check if request is in Burp's scope
   - **Custom Domain List**: Check if request domain matches any custom domain
2. If domain filtering passes, retrieves all cookie parameters from the request
3. Checks if any cookie names match those in the blocklist
4. Removes matching cookies from the request
5. Logs the removal action to Burp's output console
6. Forwards the modified request

## Logging

The extension logs all cookie removal actions to Burp's output console. To view these logs:

1. Go to the **Extensions** tab
2. Select **Cookie Monster** from the extensions list
3. View the **Output** tab to see removal logs

Log format: `Cookie Monster: Removing cookie 'cookie_name' from request to https://example.com/path`

## Building from Source

### Prerequisites

- Java 11 or higher
- Maven (optional, for Maven build)
- Burp Suite Montoya API interface files

### Compilation

The project includes compiled source code. To rebuild:

```bash
# Compile Burp API interfaces
cd /path/to/burp_files/interface
find burp -name "*.java" > /tmp/burp_sources.txt
javac -d /path/to/cookie_monster/target/classes @/tmp/burp_sources.txt

# Compile Cookie Monster extension
cd /path/to/cookie_monster
javac -cp target/classes -d target/classes src/main/java/burp/cookiemonster/*.java

# Create JAR file
cd target/classes
jar cvf ../cookie-monster.jar burp/cookiemonster/*.class
```

## Project Structure

```
cookie_monster/
├── src/main/java/burp/cookiemonster/
│   ├── CookieMonster.java           # Main extension entry point
│   ├── CookieBlocklistManager.java  # Thread-safe blocklist and domain manager
│   ├── CookieFilterHandler.java     # HTTP request interceptor with domain filtering
│   ├── CookieMonsterUI.java         # Swing-based user interface
│   └── DomainFilterMode.java        # Enum for domain filtering modes
├── target/
│   └── cookie-monster.jar           # Compiled extension JAR
├── pom.xml                          # Maven build configuration
├── .gitignore                       # Git ignore file
└── README.md                        # This file
```

## Use Cases

- **Testing Authentication**: Remove session cookies to test unauthenticated access
- **Privacy Testing**: Block tracking cookies during security assessments
- **Session Management Testing**: Test application behavior without specific cookies
- **Multi-Domain Testing**: Use custom domain filtering to test different cookie behaviors across multiple targets
- **Scope-Limited Testing**: Combine with Burp's scope to only filter cookies for in-scope targets
- **Performance Testing**: Reduce request size by removing unnecessary cookies
- **Debugging**: Isolate issues related to specific cookies on specific domains

## Technical Details

- **API**: Built using Burp Suite Montoya API
- **Thread Safety**: Uses CopyOnWriteArraySet for concurrent access
- **UI Framework**: Java Swing
- **Scope**: Affects all HTTP/HTTPS requests through any Burp tool

## Troubleshooting

### Extension doesn't load
- Ensure you're using Java 11 or higher
- Check the **Errors** tab in the Extensions section for stack traces

### Cookies not being removed
- Verify the exact cookie name (case-sensitive)
- Check the Output tab to see if the extension is processing requests
- Ensure the extension is enabled in the Extensions list

### UI not appearing
- Reload the extension
- Check for errors in the Burp error console

## Version History

- **1.1.0** (Current)
  - Added domain-based filtering (All Domains, In-Scope Only, Custom Domain List)
  - Enhanced UI with split-pane layout
  - Subdomain matching support for custom domains
  - Domain validation
  - Git repository initialization

- **1.0.0** (Initial Release)
  - Dynamic cookie filtering
  - GUI for managing blocked cookies
  - Support for all Burp tools
  - Real-time logging

## License

This extension is provided as-is for use with Burp Suite Community Edition and Burp Suite Professional, in accordance with PortSwigger's extension license terms.

## Support

For issues, feature requests, or contributions, please refer to the project repository or contact the extension author.
