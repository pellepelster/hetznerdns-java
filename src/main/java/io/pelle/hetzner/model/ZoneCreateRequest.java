package io.pelle.hetzner.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZoneCreateRequest {
  private String name;
  private Integer ttl;
}
