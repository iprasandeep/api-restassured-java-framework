package com.prasandeep.api.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thread-local auth, headers, and cookies applied by {@link RequestSpecFactory}.
 * Safe for parallel TestNG methods.
 */
public final class AuthContext {

    private static final ThreadLocal<String> BEARER = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> HEADERS =
            ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<Map<String, String>> COOKIES =
            ThreadLocal.withInitial(LinkedHashMap::new);

    private AuthContext() {
    }

    public static void setBearerToken(String token) {
        BEARER.set(token);
    }

    public static String getBearerToken() {
        return BEARER.get();
    }

    public static void addHeader(String name, String value) {
        HEADERS.get().put(name, value);
    }

    public static Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(HEADERS.get());
    }

    public static void addCookie(String name, String value) {
        COOKIES.get().put(name, value);
    }

    public static Map<String, String> getCookies() {
        return Collections.unmodifiableMap(COOKIES.get());
    }

    /** Clears all thread-local auth state (call in @AfterMethod). */
    public static void clear() {
        BEARER.remove();
        HEADERS.remove();
        COOKIES.remove();
    }
}
