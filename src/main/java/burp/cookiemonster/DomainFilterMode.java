package burp.cookiemonster;

/**
 * Defines the scope for domain filtering when removing cookies.
 */
public enum DomainFilterMode {
    /**
     * Remove cookies from all domains.
     */
    ALL_DOMAINS("All Domains"),

    /**
     * Remove cookies only from domains that are in Burp's scope.
     */
    IN_SCOPE_ONLY("In-Scope Only"),

    /**
     * Remove cookies only from specified domains in the custom list.
     */
    CUSTOM_DOMAINS("Custom Domain List");

    private final String displayName;

    DomainFilterMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
