package io.pelle.hetzner;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertThat;

import io.pelle.hetzner.model.RecordRequest;
import io.pelle.hetzner.model.RecordType;
import io.pelle.hetzner.model.ZoneRequest;
import java.util.Optional;
import org.junit.Test;

public class HetznerDnsAPIIntegrationTest {

  private static String INTEGRATION_TEST_ZONE_NAME =
      "pelle-io-hetzner-dns-java-integration-test.de";

  @Test
  public void testDnsApi() {

    var dnsApi = new HetznerDnsAPI(System.getenv("INTEGRATION_TEST_API_TOKEN"));

    // ensure deleting an invalid zone id is handled gracefully
    assertThat(dnsApi.deleteZone("XXXXXXXSLaMsoTNyqrt4"), is(false));

    // clean up from previous tests if needed
    Optional.ofNullable(dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME))
        .ifPresent(zone -> dnsApi.deleteZone(zone.getId()));

    var totalZones = dnsApi.getZones().size();

    // ensure 404 are handled gracefully
    assertThat(dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME), nullValue());
    dnsApi.createZone(ZoneRequest.builder().name(INTEGRATION_TEST_ZONE_NAME).build());

    // we should now have one zone more in total
    assertThat(dnsApi.getZones().size(), is(totalZones + 1));

    // verify created zone
    var zone = dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME);
    assertThat(zone.getName(), is(INTEGRATION_TEST_ZONE_NAME));

    // on no, the new zone has the wrong ttl, lets fix this
    dnsApi.updateZone(
        zone.getId(), ZoneRequest.builder().name(INTEGRATION_TEST_ZONE_NAME).ttl(66).build());

    // verify updated zone
    zone = dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME);
    assertThat(zone.getTtl(), is(66));

    // check newly created zone has no records (not counting the SOA record)
    assertThat(dnsApi.getRecords(zone.getId()).size(), is(1));

    // add record to zone
    var newRecord1 =
        dnsApi.createRecord(
            RecordRequest.builder()
                .name("xxx")
                .zoneId(zone.getId())
                .type(RecordType.A)
                .value("1.1.1.1")
                .build());
    assertThat(newRecord1.getName(), is("xxx"));
    assertThat(newRecord1.getValue(), is("1.1.1.1"));

    // check for newly created record (plus the default SOA record)
    assertThat(dnsApi.getRecords(zone.getId()).size(), is(2));

    // we made a mistake, lets try to update the record
    var updatedRecord1 =
        dnsApi.updateRecord(
            newRecord1.getId(),
            RecordRequest.builder()
                .name("xxx")
                .zoneId(zone.getId())
                .type(RecordType.A)
                .value("8.8.8.8")
                .build());
    assertThat(updatedRecord1.getValue(), is("8.8.8.8"));

    // cool lets create some more records in bulk
    var newRecords =
        dnsApi.createRecords(
            RecordRequest.builder()
                .name("yyy")
                .zoneId(zone.getId())
                .type(RecordType.A)
                .value("2.2.2.2")
                .build(),
            RecordRequest.builder()
                .name("zzz")
                .zoneId(zone.getId())
                .type(RecordType.A)
                .value("3.3.3.3")
                .build());

    // check for newly created records (plus the default SOA record)
    assertThat(dnsApi.getRecords(zone.getId()).size(), is(4));

    // clean up test zone
    Optional.ofNullable(dnsApi.searchZone(INTEGRATION_TEST_ZONE_NAME))
        .ifPresent(z -> dnsApi.deleteZone(z.getId()));
  }
}
