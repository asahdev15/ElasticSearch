package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ElasticIndexSetting
{
   private String customerIndex;
   private String customerIndexAlias;
}
