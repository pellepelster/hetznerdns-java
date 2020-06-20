package io.pelle.hetzner;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertThat;

import io.pelle.hetzner.model.ZoneCreateRequest;
import org.junit.Test;

public class HetznerDnsAPIIntegrationTest {

  private static String INTEGRATION_TEST_ZONE_NAME =
      "pelle-io-hetzner-dns-java-integration-test.de";

  @Test
  public void testZones() {

    var dnsApi = new HetznerDnsAPI(System.getenv("INTEGRATION_TEST_API_TOKEN"));

    // deleting invalid zone id is handles gracefully
    assertThat(dnsApi.deleteZone("6JjmDvFcySLaMsoTNyqrt4"), is(false));

    // clean up from previous tests if needed
    dnsApi
        .searchZone(INTEGRATION_TEST_ZONE_NAME)
        .ifPresent(zone -> dnsApi.deleteZone(zone.getId()));

    var totalZones = dnsApi.getZones().size();

    assertThat(dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME).isPresent(), is(false));

    dnsApi.createZone(ZoneCreateRequest.builder().name(INTEGRATION_TEST_ZONE_NAME).build());

    assertThat(dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME).isPresent(), is(true));

    assertThat(dnsApi.getZones().size(), is(totalZones + 1));

    // clean up test zone
    dnsApi
        .searchZone(INTEGRATION_TEST_ZONE_NAME)
        .ifPresent(zone -> dnsApi.deleteZone(zone.getId()));
  }
}
