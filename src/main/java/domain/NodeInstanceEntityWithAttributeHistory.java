package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(exclude = { "startDate", "updateDate" })
@Accessors(chain = true)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{elasticSearchIndexSetting.unitNodeInstanceWithAttributeHistoryIndexName}", type = "nodeinstance")
@AllArgsConstructor
@Builder
public class NodeInstanceEntityWithAttributeHistory
{

   @Id
   private String id;
   @Field(type = FieldType.Text, fielddata = true)
   private String name;
   @Field(type = FieldType.Text, fielddata = true)
   private String nodeType;
   @Field(type = FieldType.Text, fielddata = true)
   private String status;
   @DateTimeFormat(iso = ISO.DATE_TIME)
   @Field(type = FieldType.Date)
   private Date startDate;
   @DateTimeFormat(iso = ISO.DATE_TIME)
   @Field(type = FieldType.Date)
   private Date updateDate;
   private Map<String, String> metaData;
   @Field(type = FieldType.Nested)
   private List<AttributeEntity> attributes;
   @Field(type = FieldType.Text, index = false)
   private String allAttributes;

}
