# Hetzner DNS API

Java implementation of the Hetzner [DNS API](https://dns.hetzner.com/api-docs/) 

## Usage

Add dependency to you Maven/Gradle project

```

```


```
var dnsApi = new HetznerDnsAPI("${API_TOKEN}");

dnsApi.createZone(ZoneRequest.builder()
    .name("my-domain.de").build());

var zone = dnsApi.searchZone("my-domain.de");

dnsApi.updateZone(
        zone.getId(), ZoneRequest.builder()
        .name("my-domain.de").ttl(66).build());

var record = dnsApi.createRecord(
    RecordRequest.builder()
        .name("www")
        .zoneId(zone..getId())
        .type(RecordType.A)
        .value("1.1.1.1")
        .build());

dnsApi.updateRecord(
    record.getId(),
    RecordRequest.builder()
        .name("www")
        .zoneId(zone.get().getId())
        .type(RecordType.A)
        .value("8.8.8.8")
        .build());
```

## Development
 
### Run Integration Tests

The majority of the test coverage comes from a integration test against the real API. This can be started with

`
INTEGRATION_TEST_API_TOKEN=${HETZNER_API_TOKEN} ./gradlew check
