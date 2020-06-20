package io.pelle.hetzner;

import io.pelle.hetzner.model.ListZonesResponse;
import io.pelle.hetzner.model.ZoneCreateRequest;
import io.pelle.hetzner.model.ZoneResponse;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class HetznerDnsAPI {

  public boolean deleteZone(String zoneId) {
    return request("/zones/" + zoneId, HttpMethod.DELETE, Object.class).response.isPresent();
  }

  @Getter
  @AllArgsConstructor
  private static class ApiResult<T> {
    private Optional<T> response;
    private boolean error;

    public static <T> ApiResult<T> of(T response) {
      return new ApiResult<T>(Optional.of(response), false);
    }

    public static <T> ApiResult<T> empty() {
      return new ApiResult<T>(Optional.empty(), false);
    }

    public static <T> ApiResult<T> error() {
      return new ApiResult<T>(Optional.empty(), true);
    }
  }

  private static final String DEFAULT_API_URL = "https://dns.hetzner.com/api/v1";

  private final String token;

  private final String apiUrl;

  private HttpEntity<String> httpEntity;
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
    this.httpEntity = new HttpEntity<>("parameters", httpHeaders);

    restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(messageConverters);
  }

  public ZoneResponse getZone(String zoneId) {
    return restTemplate
        .exchange(this.apiUrl + "/zones/" + zoneId, HttpMethod.GET, httpEntity, ZoneResponse.class)
        .getBody();
  }

  public ZoneResponse createZone(ZoneCreateRequest request) {
    var r = new HttpEntity<>(request, httpHeaders);
    return restTemplate
        .exchange(this.apiUrl + "/zones", HttpMethod.POST, r, ZoneResponse.class)
        .getBody();
  }

  public Optional<ZoneResponse> searchZone(String name) {
    return request("/zones?name=" + name, HttpMethod.GET, ListZonesResponse.class)
        .response
        .map(ListZonesResponse::getZones)
        .flatMap(t -> t.stream().findFirst());
  }

  public List<ZoneResponse> getZones() {
    return request("/zones", HttpMethod.GET, ListZonesResponse.class)
        .response
        .map(ListZonesResponse::getZones)
        .orElse(Collections.emptyList());
  }

  private <T> ApiResult<T> request(String url, HttpMethod method, Class<T> responseClass) {

    try {
      var response =
          restTemplate.exchange(this.apiUrl + url, method, httpEntity, responseClass).getBody();
      return ApiResult.of(response);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return ApiResult.empty();
      }

      return ApiResult.error();
    }
  }
}
