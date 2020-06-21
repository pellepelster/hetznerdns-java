package io.pelle.hetzner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.pelle.hetzner.model.ZoneRequest;
import org.junit.Rule;
import org.junit.Test;

public class HetznerDnsAPITest {

  @Rule public WireMockRule wireMockRule = new WireMockRule(8888);

  @Test
  public void testApiTokenIsProvided() {

    stubFor(
        get(urlEqualTo("/api/v1/zones/1234"))
            .willReturn(
                okJson(
                    "{\n"
                        + "  \"zone\": {\n"
                        + "    \"id\": \"1234\""
                        + "    }\n"
                        + "  }\n"
                        + "}")));

    var dnsApi = new HetznerDnsAPI("api-token", "http://localhost:8888/api/v1");
    dnsApi.getZone("1234");

    verify(
        getRequestedFor(urlMatching("/api/v1/zones/1234"))
            .withHeader("Auth-API-Token", matching("api-token")));
  }

  @Test
  public void testUnknownFieldsAreIgnored() {

    stubFor(
        get(urlEqualTo("/api/v1/zones/1234"))
            .willReturn(
                okJson(
                    "{\n"
                        + "  \"zone\": {\n"
                        + "    \"id\": \"1234\","
                        + "    \"xxx\": \"yyy\""
                        + "    }\n"
                        + "  }\n"
                        + "}")));

    var dnsApi = new HetznerDnsAPI("api-token", "http://localhost:8888/api/v1");
    dnsApi.getZone("1234");

    verify(
        getRequestedFor(urlMatching("/api/v1/zones/1234"))
            .withHeader("Auth-API-Token", matching("api-token")));
  }

  @Test
  public void testNullFieldsAreNotSent() {

    stubFor(
        post(urlEqualTo("/api/v1/zones"))
            .willReturn(
                okJson(
                    "{\n"
                        + "  \"zone\": {\n"
                        + "    \"id\": \"1234\","
                        + "    \"xxx\": \"yyy\""
                        + "    }\n"
                        + "  }\n"
                        + "}")));

    var dnsApi = new HetznerDnsAPI("api-token", "http://localhost:8888/api/v1");
    dnsApi.createZone(ZoneRequest.builder().name("xxx").ttl(null).build());

    verify(postRequestedFor(urlMatching("/api/v1/zones")).withRequestBody(notMatching(".*ttl.*")));
  }
}
