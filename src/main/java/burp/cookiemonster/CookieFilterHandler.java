package burp.cookiemonster;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP handler that intercepts requests and removes blocked cookies based on domain filtering rules.
 */
public class CookieFilterHandler implements HttpHandler {
    private final CookieBlocklistManager blocklistManager;
    private final MontoyaApi api;
    private final Logging logging;

    public CookieFilterHandler(CookieBlocklistManager blocklistManager, MontoyaApi api) {
        this.blocklistManager = blocklistManager;
        this.api = api;
        this.logging = api.logging();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            // Get all cookie parameters from the request
            List<ParsedHttpParameter> cookieParameters = requestToBeSent.parameters(HttpParameterType.COOKIE);

            // If there are no cookies or no blocked cookies, return original request
            if (cookieParameters.isEmpty() || blocklistManager.size() == 0) {
                return RequestToBeSentAction.continueWith(requestToBeSent);
            }

            // Check if this request should be processed based on domain filtering mode
            if (!shouldProcessRequest(requestToBeSent)) {
                return RequestToBeSentAction.continueWith(requestToBeSent);
            }

            // Find cookies that should be removed
            List<HttpParameter> cookiesToRemove = new ArrayList<>();
            for (ParsedHttpParameter cookie : cookieParameters) {
                if (blocklistManager.isBlocked(cookie.name())) {
                    cookiesToRemove.add(cookie);
                    logging.logToOutput("Cookie Monster: Removing cookie '" + cookie.name() +
                                      "' from request to " + requestToBeSent.url());
                }
            }

            // If no cookies need to be removed, return original request
            if (cookiesToRemove.isEmpty()) {
                return RequestToBeSentAction.continueWith(requestToBeSent);
            }

            // Create a modified request with the blocked cookies removed
            HttpRequest modifiedRequest = requestToBeSent.withRemovedParameters(cookiesToRemove);
            return RequestToBeSentAction.continueWith(modifiedRequest);

        } catch (Exception e) {
            logging.logToError("Cookie Monster error: " + e.getMessage());
            // On error, return the original request to avoid breaking functionality
            return RequestToBeSentAction.continueWith(requestToBeSent);
        }
    }

    /**
     * Determine if the request should be processed based on the domain filtering mode.
     *
     * @param request The request to check
     * @return true if the request should be processed, false otherwise
     */
    private boolean shouldProcessRequest(HttpRequestToBeSent request) {
        DomainFilterMode mode = blocklistManager.getFilterMode();

        switch (mode) {
            case ALL_DOMAINS:
                // Process all requests regardless of domain
                return true;

            case IN_SCOPE_ONLY:
                // Only process requests that are in Burp's scope
                return request.isInScope();

            case CUSTOM_DOMAINS:
                // Only process requests to domains in the custom list
                String host = request.httpService().host();
                return blocklistManager.matchesCustomDomain(host);

            default:
                // Default to processing all requests
                return true;
        }
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // We don't need to modify responses, just pass them through
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
