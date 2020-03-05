package rest;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDto
{
   private String name;
   private String value;
   private String type;
   private Date startDate;
   private Date endDate;

}
