package io.pelle.hetzner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordResponse {
  private String id;

  @JsonProperty("zone_id")
  private String zoneId;

  private String name;
  private String value;
  private Integer ttl;
  private RecordType type;
  private String created;
  private String modified;
}
