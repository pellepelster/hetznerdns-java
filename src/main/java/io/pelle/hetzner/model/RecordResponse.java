package io.pelle.hetzner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordResponse {
  @JsonProperty("zone_id")
  private String zoneId;

  private String name;
  private String value;
  private Integer ttl;
  private RecordType type;
  private Date created;
  private Date modified;
}
