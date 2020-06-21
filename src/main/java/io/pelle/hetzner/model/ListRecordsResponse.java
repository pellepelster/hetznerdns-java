package io.pelle.hetzner.model;

import java.util.List;
import lombok.Data;

@Data
public class ListRecordsResponse {
  private List<RecordResponse> records;
}
