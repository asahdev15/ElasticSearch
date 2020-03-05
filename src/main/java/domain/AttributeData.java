package domain;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeData
{
   private String value;
   private String dataType;
   @DateTimeFormat(iso = ISO.DATE_TIME)
   @Field(type = FieldType.Date)
   private Date startDate;
   @DateTimeFormat(iso = ISO.DATE_TIME)
   @Field(type = FieldType.Date)
   private Date endDate;
}
