package io.pelle.hetzner.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZoneRequest {
  private String name;
  private Integer ttl;
}
