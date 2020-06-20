package io.pelle.hetzner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

public class HetznerDnsAPITest {

  @Rule public WireMockRule wireMockRule = new WireMockRule(8888);

  @Test
  public void testGetZone() {

    stubFor(
        get(urlEqualTo("/api/v1/zones/1234"))
            .willReturn(
                okJson(
                    "{\n"
                        + "  \"zone\": {\n"
                        + "    \"id\": \"1234\",\n"
                        + "    \"created\": \"2020-06-20T19:12:49Z\",\n"
                        + "    \"modified\": \"2020-06-20T19:12:49Z\",\n"
                        + "    \"legacy_dns_host\": \"string\",\n"
                        + "    \"legacy_ns\": [\n"
                        + "      \"string\"\n"
                        + "    ],\n"
                        + "    \"name\": \"string\",\n"
                        + "    \"ns\": [\n"
                        + "      \"string\"\n"
                        + "    ],\n"
                        + "    \"owner\": \"string\",\n"
                        + "    \"paused\": true,\n"
                        + "    \"permission\": \"string\",\n"
                        + "    \"project\": \"string\",\n"
                        + "    \"registrar\": \"string\",\n"
                        + "    \"status\": \"verified\",\n"
                        + "    \"ttl\": 0,\n"
                        + "    \"verified\": \"2020-06-20T19:12:49Z\",\n"
                        + "    \"records_count\": 0,\n"
                        + "    \"is_secondary_dns\": true,\n"
                        + "    \"txt_verification\": {\n"
                        + "      \"name\": \"string\",\n"
                        + "      \"token\": \"string\"\n"
                        + "    }\n"
                        + "  }\n"
                        + "}")));

    var dnsApi = new HetznerDnsAPI("api-token", "http://localhost:8888/api/v1");
    dnsApi.getZone("1234");

    verify(
        getRequestedFor(urlMatching("/api/v1/zones/1234"))
            .withHeader("Auth-API-Token", matching("api-token")));
  }
}
