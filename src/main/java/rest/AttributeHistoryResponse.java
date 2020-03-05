package rest;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeHistoryResponse
{

   // nodeInstanceId - attributeName - AttributeHistory - value, start, end
   private Map<String, Map<String, List<AttributeHistoryResult>>> response;

}
