package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{elasticSearchIndexSetting.unitAttributeHistoryIndexName}", type = "nodeinstance")
public class AttributeHistoryEntity
{
   @Id
   private String nodeinstanceId;
   @Field(type = FieldType.Nested)
   private List<AttributeHistory> attributes;
}
