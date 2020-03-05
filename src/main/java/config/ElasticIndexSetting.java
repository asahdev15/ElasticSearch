package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ElasticIndexSetting
{
   private String unitNodeInstanceWithAttributeHistoryIndexName;
   private String unitNodeInstanceWithAttributeHistoryAliasName;

   private String unitNodeInstanceIndexName;
   private String unitNodeInstanceAliasName;

   private String unitAttributeHistoryIndexName;
   private String unitAttributeHistoryAliasName;

   private String bookIndex;
   private String bookIndexAlias;

   private String userIndex;
   private String userIndexAlias;
}
