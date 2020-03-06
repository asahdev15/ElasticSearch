package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true, exclude = { "startDate", "updateDate", "endDate" })
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{elasticSearchIndexSetting.customerIndex}", type = "customers")
public class Customer extends Entity{

    @Field(type = FieldType.Text, fielddata = true)
    private String firstName;

    @Field(type = FieldType.Text, fielddata = true)
    private String lastName;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Integer, index = false)
    private Integer phone;

    @Field(type = FieldType.Text, index = false)
    private String address;

    @Field(type = FieldType.Nested)
    private List<Attribute> attributes;

}
