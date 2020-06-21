package io.pelle.hetzner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordRequest {
  @JsonProperty("zone_id")
  private String zoneId;

  private String name;
  private String value;
  private Integer ttl;
  private RecordType type;
}
