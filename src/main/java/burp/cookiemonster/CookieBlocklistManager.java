package burp.cookiemonster;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Thread-safe manager for the cookie blocklist and domain filtering settings.
 * Maintains a set of cookie names that should be removed from requests,
 * along with domain filtering configuration.
 */
public class CookieBlocklistManager {
    private final Set<String> blockedCookies;
    private final Set<String> customDomains;
    private volatile DomainFilterMode filterMode;

    public CookieBlocklistManager() {
        this.blockedCookies = new CopyOnWriteArraySet<>();
        this.customDomains = new CopyOnWriteArraySet<>();
        this.filterMode = DomainFilterMode.ALL_DOMAINS;
    }

    /**
     * Add a cookie name to the blocklist.
     *
     * @param cookieName The name of the cookie to block
     * @return true if the cookie was added, false if it was already in the blocklist
     */
    public boolean addCookie(String cookieName) {
        if (cookieName == null || cookieName.trim().isEmpty()) {
            return false;
        }
        return blockedCookies.add(cookieName.trim());
    }

    /**
     * Remove a cookie name from the blocklist.
     *
     * @param cookieName The name of the cookie to unblock
     * @return true if the cookie was removed, false if it wasn't in the blocklist
     */
    public boolean removeCookie(String cookieName) {
        return blockedCookies.remove(cookieName);
    }

    /**
     * Check if a cookie name is in the blocklist.
     *
     * @param cookieName The name of the cookie to check
     * @return true if the cookie is blocked, false otherwise
     */
    public boolean isBlocked(String cookieName) {
        return blockedCookies.contains(cookieName);
    }

    /**
     * Get all blocked cookie names.
     *
     * @return A set of all blocked cookie names
     */
    public Set<String> getBlockedCookies() {
        return Set.copyOf(blockedCookies);
    }

    /**
     * Clear all blocked cookies.
     */
    public void clear() {
        blockedCookies.clear();
    }

    /**
     * Get the number of blocked cookies.
     *
     * @return The count of blocked cookies
     */
    public int size() {
        return blockedCookies.size();
    }

    // Domain filtering methods

    /**
     * Get the current domain filter mode.
     *
     * @return The current filter mode
     */
    public DomainFilterMode getFilterMode() {
        return filterMode;
    }

    /**
     * Set the domain filter mode.
     *
     * @param mode The new filter mode
     */
    public void setFilterMode(DomainFilterMode mode) {
        if (mode != null) {
            this.filterMode = mode;
        }
    }

    /**
     * Add a domain to the custom domain list.
     *
     * @param domain The domain to add (e.g., "example.com")
     * @return true if the domain was added, false if it was already in the list
     */
    public boolean addCustomDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        return customDomains.add(domain.trim().toLowerCase());
    }

    /**
     * Remove a domain from the custom domain list.
     *
     * @param domain The domain to remove
     * @return true if the domain was removed, false if it wasn't in the list
     */
    public boolean removeCustomDomain(String domain) {
        return customDomains.remove(domain.toLowerCase());
    }

    /**
     * Check if a domain is in the custom domain list.
     *
     * @param domain The domain to check
     * @return true if the domain is in the list, false otherwise
     */
    public boolean isCustomDomain(String domain) {
        return customDomains.contains(domain.toLowerCase());
    }

    /**
     * Get all custom domains.
     *
     * @return A set of all custom domains
     */
    public Set<String> getCustomDomains() {
        return Set.copyOf(customDomains);
    }

    /**
     * Clear all custom domains.
     */
    public void clearCustomDomains() {
        customDomains.clear();
    }

    /**
     * Get the number of custom domains.
     *
     * @return The count of custom domains
     */
    public int customDomainsSize() {
        return customDomains.size();
    }

    /**
     * Check if a host matches any custom domain.
     * Supports exact matches and wildcard subdomains.
     *
     * @param host The host to check (e.g., "www.example.com")
     * @return true if the host matches a custom domain, false otherwise
     */
    public boolean matchesCustomDomain(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }

        String lowerHost = host.toLowerCase();

        // Check for exact match first
        if (customDomains.contains(lowerHost)) {
            return true;
        }

        // Check if host ends with any custom domain (subdomain matching)
        for (String domain : customDomains) {
            if (lowerHost.equals(domain) || lowerHost.endsWith("." + domain)) {
                return true;
            }
        }

        return false;
    }
}
