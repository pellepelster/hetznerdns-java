package io.pelle.hetzner;

import io.pelle.hetzner.model.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class HetznerDnsAPI {

  public boolean deleteZone(String zoneId) {
    return request("/zones/" + zoneId, HttpMethod.DELETE, defaultHttpEntity, Object.class)
        .isPresent();
  }

  private static final String DEFAULT_API_URL = "https://dns.hetzner.com/api/v1";

  private final String token;

  private final String apiUrl;

  private HttpEntity<String> defaultHttpEntity;
  private HttpHeaders httpHeaders;
  private RestTemplate restTemplate;

  private List<HttpMessageConverter<?>> messageConverters;
  private MappingJackson2HttpMessageConverter converter;

  public HetznerDnsAPI(String token) {
    this(token, DEFAULT_API_URL);
  }

  public HetznerDnsAPI(String token, String apiUrl) {
    this.token = token;
    this.apiUrl = apiUrl;

    messageConverters = new ArrayList<>();
    converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    messageConverters.add(converter);

    this.httpHeaders = new HttpHeaders();
    this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    this.httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    this.httpHeaders.add("Auth-API-Token", token);
    this.defaultHttpEntity = new HttpEntity<>("parameters", httpHeaders);

    restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(messageConverters);
  }

  public ZoneResponse getZone(String zoneId) {
    return restTemplate
        .exchange(
            this.apiUrl + "/zones/" + zoneId, HttpMethod.GET, defaultHttpEntity, ZoneResponse.class)
        .getBody();
  }

  public ZoneResponse createZone(ZoneCreateRequest request) {
    var entity = new HttpEntity<>(request, httpHeaders);
    return restTemplate
        .exchange(this.apiUrl + "/zones", HttpMethod.POST, entity, ZoneResponse.class)
        .getBody();
  }

  public RecordResponse createRecord(RecordCreateRequest request) {
    var entity = new HttpEntity<>(request, httpHeaders);
    return request(this.apiUrl + "/records", HttpMethod.POST, entity, RecordResponseWrapper.class)
        .map(RecordResponseWrapper::getRecord)
        .orElse(null);
  }

  public Optional<ZoneResponse> searchZone(String name) {
    return request(
            "/zones?name=" + name, HttpMethod.GET, defaultHttpEntity, ListZonesResponse.class)
        .map(ListZonesResponse::getZones)
        .flatMap(t -> t.stream().findFirst());
  }

  public List<ZoneResponse> getZones() {
    return request("/zones", HttpMethod.GET, defaultHttpEntity, ListZonesResponse.class)
        .map(ListZonesResponse::getZones)
        .orElse(Collections.emptyList());
  }

  private <T> Optional<T> request(
      String url, HttpMethod method, HttpEntity<?> httpEntity, Class<T> responseClass) {
    try {
      return Optional.of(
          restTemplate.exchange(this.apiUrl + url, method, httpEntity, responseClass).getBody());
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return Optional.empty();
      }

      throw new RuntimeException(e);
    }
  }
}
