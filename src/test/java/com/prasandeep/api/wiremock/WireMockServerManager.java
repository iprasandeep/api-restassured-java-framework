package com.prasandeep.api.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

/** Starts a dynamic-port WireMock server and registers common auth/retry stubs. */
public final class WireMockServerManager {

    private WireMockServer server;

    public void start() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        stubProtectedResource();
        stubSetCookie();
        stubFlakyEndpoint();
        stubLogin();
    }

    public void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    public String baseUri() {
        return "http://localhost:" + server.port();
    }

    public int port() {
        return server.port();
    }

    private void stubLogin() {
        server.stubFor(post(urlEqualTo("/api/login"))
                .atPriority(1)
                .withRequestBody(containing("eve.holt@reqres.in"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"wiremock-token-123\"}")));

        server.stubFor(post(urlEqualTo("/api/login"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Missing password\"}")));
    }

    private void stubProtectedResource() {
        server.stubFor(get(urlPathEqualTo("/secure/profile"))
                .atPriority(1)
                .withHeader("Authorization", equalTo("Bearer wiremock-token-123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"Interview User\"}")));

        server.stubFor(get(urlPathEqualTo("/secure/profile"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Unauthorized\"}")));
    }

    private void stubSetCookie() {
        server.stubFor(get(urlEqualTo("/session"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Set-Cookie", "sessionId=abc-xyz; Path=/; HttpOnly")
                        .withBody("{\"ok\":true}")));

        server.stubFor(get(urlEqualTo("/session/me"))
                .atPriority(1)
                .withCookie("sessionId", equalTo("abc-xyz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"user\":\"cookie-user\"}")));

        server.stubFor(get(urlEqualTo("/session/me"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"No session\"}")));
    }

    /** First two calls 503, then 200 — exercises RetryFilter. */
    private void stubFlakyEndpoint() {
        server.stubFor(get(urlEqualTo("/flaky"))
                .inScenario("flaky")
                .whenScenarioStateIs("Started")
                .willSetStateTo("attempt-1")
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"unavailable\"}")));

        server.stubFor(get(urlEqualTo("/flaky"))
                .inScenario("flaky")
                .whenScenarioStateIs("attempt-1")
                .willSetStateTo("attempt-2")
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"unavailable\"}")));

        server.stubFor(get(urlEqualTo("/flaky"))
                .inScenario("flaky")
                .whenScenarioStateIs("attempt-2")
                .willSetStateTo("Started")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"recovered\"}")));
    }
}
