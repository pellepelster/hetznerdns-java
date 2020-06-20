package io.pelle.hetzner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ZoneResponse {

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  private class TxtVerification {
    private String name;
    private String token;
  }

  private String id;
  private String name;
  private String owner;
  private boolean paused;
  private String permission;
  private String project;
  private String registrar;
  private String status;
  private Integer ttl;
  private String created;
  private String modified;
  private String verified;

  @JsonProperty("legacy_dns_host")
  private String legacyDnsHost;

  @JsonProperty("legacy_ns")
  private List<String> legacyNs;

  @JsonProperty("records_count")
  private Integer recordscount;

  @JsonProperty("is_secondary_dns")
  private boolean isSecondaryDns;

  @JsonProperty("txt_verification")
  private TxtVerification txtVerification;
}
