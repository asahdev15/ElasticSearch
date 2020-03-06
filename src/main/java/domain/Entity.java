package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Entity {

    @Id
    private String id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field(type = FieldType.Date)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field(type = FieldType.Date)
    private Date endDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field(type = FieldType.Date)
    private Date updateDate;

}
