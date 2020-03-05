package rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeHistoryResult
{

   private String value;
   private String dataType;
   private long startDate;
   private long endDate;

}
