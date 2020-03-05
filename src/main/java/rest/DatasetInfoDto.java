package rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetInfoDto
{
   private int nodeInstanceBatchCount;
   private int nodeInstanceCount;
   private int attributesCount;
}
