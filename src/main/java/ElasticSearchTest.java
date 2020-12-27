
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Date;

public class ElasticSearchTest
{

   public static void main(String[] args) {

//      User customerNew = User.builder()
//              .id("1")
//              .firstName("Ashish")
//              .lastName("Sahdev")
//              .address("Delhi")
//              .startDate(new Date())
//              .endDate(new Date())
//              .updateDate(new Date())
//              .attributes(Lists.newArrayList(Attribute.builder().dataType("String").value("x1").build(),
//                      Attribute.builder().dataType("String").value("x2").build()))
//              .build();
//
//
//
////      BoolQueryBuilder query = QueryBuilders.boolQuery();
////      query.must(QueryBuilders.rangeQuery("updateDate").lte(95617584000000L));
////      query.must(QueryBuilders.termQuery("status", "Terminated"));
//      System.out.print(JsonUtils.convertToJson(customerNew));
   }

   private static void printQuery_NodeInstanceID_AttName_AttValue_Time() {
      System.out.print(buildQuery("1", "AttributePOC_5", "AttributePOCValue_5", 1561358901837L, 1561447542790L));
   }

   private static void printQuery_NodeInstanceID() {
      System.out.print(buildQuery("1", null, null, null, null));
   }

   private static void printQuery_NodeInstanceID_AttName() {
      System.out.print(buildQuery("1", "AttributePOC_5", null, null, null));
   }

   private static void printQuery_NodeInstanceID_AttName_AttValue() {
      System.out.print(buildQuery("1", "AttributePOC_5", "AttributePOCValue_5", null, null));
   }

   private static void printQuery_NodeInstanceID_AttName_Time() {
      System.out.print(buildQuery("1", "AttributePOC_5", null, 1561358901837L, 1561447542790L));
   }

   private static void printQuery_AttName_AttValue() {
      System.out.print(buildQuery(null, "AttributePOC_5", "AttributePOCValue_5", null, null));
   }

   private static void printQuery_AttName_Time() {
      System.out.print(buildQuery("1", "AttributePOC_5", "AttributePOCValue_5", 1561358901837L, 1561447542790L));
   }

   private static QueryBuilder buildQuery(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime) {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      if(nodeInstanceId != null && !nodeInstanceId.trim().isEmpty()) {
         query.must(QueryBuilders.termQuery("nodeinstanceId", nodeInstanceId));
      }
      if(isValuePresent(attributeName) || isValuePresent(attributeValue) || (fromTime != null && toTime !=null)) {
         BoolQueryBuilder attributeNestedBooleanQuery = QueryBuilders.boolQuery();
         if(isValuePresent(attributeName)) {
            attributeNestedBooleanQuery.must(QueryBuilders.termQuery("attributes.name", attributeName));
         }
         if(isValuePresent(attributeValue) || (fromTime != null && toTime !=null)) {
            BoolQueryBuilder attributeNestedBooleanQueryValue = QueryBuilders.boolQuery();
            if(isValuePresent(attributeValue)) {
               attributeNestedBooleanQueryValue.must(QueryBuilders.termQuery("attributes.history.value", attributeValue));
            }
            if(fromTime != null && toTime !=null) {
               attributeNestedBooleanQueryValue.should(QueryBuilders.rangeQuery("attributes.history.startDate").gte(fromTime).lte(toTime));
               attributeNestedBooleanQueryValue.should(QueryBuilders.rangeQuery("attributes.history.endDate").gte(fromTime).lte(toTime));
               attributeNestedBooleanQueryValue.should(QueryBuilders.boolQuery()
                                                                              .must(QueryBuilders.rangeQuery("attributes.history.startDate").lte(fromTime))
                                                                              .must(QueryBuilders.rangeQuery("attributes.history.endDate").gte(toTime)));
            }
            attributeNestedBooleanQuery.must(QueryBuilders.nestedQuery("attributes.history", attributeNestedBooleanQueryValue, ScoreMode.None));
         }
         query.must(QueryBuilders.nestedQuery("attributes", attributeNestedBooleanQuery, ScoreMode.None));
      }
      return query;
   }

   private static boolean isValuePresent(String value) {
      return value != null && !value.trim().isEmpty();
   }

//   public static void main(String[] args) {
//      System.out.print(buildQuery("1", "AttributePOC_5", "AttributePOCValue_5", 1561358901837L, 1561447542790L));
//
//      BoolQueryBuilder query = QueryBuilders.boolQuery()
//                                                   .must(QueryBuilders.termQuery("nodeInstanceID", "nodeInstanceIDValue"))
//                                                   .must(QueryBuilders.nestedQuery("attributes",
//                                                                                    QueryBuilders.boolQuery()
//                                                                                                         .must(termQuery("name", "AttributePOC_1"))
//                                                                                                         .must(termQuery("value", "AttributeValuePOC_1_NEW"))
//                                                                                                         .must(termQuery("updateDate", "184916236645168")),
//                                                                                    ScoreMode.None)
//                                                         );

//      BoolQueryBuilder query = QueryBuilders.boolQuery()
//         .must(QueryBuilders.rangeQuery("startDate").lte("TIME"))
//         .must(QueryBuilders.rangeQuery("startDate").gte("TIME"))
//         .must(QueryBuilders.nestedQuery("attributes",
//                                          QueryBuilders.boolQuery()
//                                                               .must(termQuery("attributeName", "Name"))
//                                                               .must(QueryBuilders.boolQuery()
//                                                                                             .must(QueryBuilders.rangeQuery("startDate").lte("TIME"))
//                                                                                             .must(QueryBuilders.rangeQuery("startDate").gte("TIME")))
//                                         ,ScoreMode.None
//                                        ));


//      BoolQueryBuilder query = QueryBuilders.boolQuery()
//                                                      .must(QueryBuilders.termQuery("nodeInstanceID", "nodeInstanceIDValue"))
//                                                      .must(QueryBuilders.rangeQuery("startDate").lte("TIME"))
//                                                      .must(QueryBuilders.boolQuery()
//                                                                                 .should(QueryBuilders.rangeQuery("endDate").gte("TIME"))
//                                                                                 .should(QueryBuilders.termQuery("endDate", "NO_END_DATE")));

//    }

//   public static void main(String[] args) {
//      BoolQueryBuilder query = QueryBuilders.boolQuery()
//                                                      .must(QueryBuilders.termQuery(ConstantsUtility.ES_FIELD_NODE_TEMP_INSTANCES_NODE_INS_ID, "1"))
//                                                      .must(QueryBuilders.rangeQuery(ConstantsUtility.ES_FIELD_CREATION_TIME_NAME).lte("11"))
//                                                      .must(QueryBuilders.boolQuery()
//                                                                                    .should(QueryBuilders.rangeQuery(ConstantsUtility.ES_FIELD_END_TIME_NAME).gte("11"))
//                                                                                    .should(QueryBuilders.termQuery(ConstantsUtility.ES_FIELD_END_TIME_NAME, UnitConstants.NO_END_DATE))
//                                                                                    .minimumShouldMatch(1))
//                                                      .should(QueryBuilders.boolQuery()
//                                                                                    .must(QueryBuilders.termQuery(ConstantsUtility.ES_FIELD_TOPOLOGY_NAME, "TOPONAME"))
//                                                                                    .must(QueryBuilders.termQuery(ConstantsUtility.ES_FIELD_TOPOLOGY_TEMPLATE_VERSION, "TOPOVERSION")));
//      System.out.print(query);
//   }






}