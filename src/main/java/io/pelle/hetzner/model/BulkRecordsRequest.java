package io.pelle.hetzner.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkRecordsRequest {
  private List<RecordRequest> records;
}
