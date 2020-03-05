package infra;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import domain.NodeInstanceEntityWithAttributeHistory;

@Service
public class NodeInstanceAttributeHistoryRepositoryImpl
{

   private ElasticsearchTemplate elasticsearchTemplate;

   public ElasticsearchTemplate getElasticsearchTemplate() {
      return elasticsearchTemplate;
   }

   public NodeInstanceAttributeHistoryRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate)
   {
      this.elasticsearchTemplate = elasticsearchTemplate;
   }

   public void refresh() {
      elasticsearchTemplate.refresh(NodeInstanceEntityWithAttributeHistory.class);
   }

   public void store(List<NodeInstanceEntityWithAttributeHistory> modelObjects)
   {
      if (!modelObjects.isEmpty())
      {
         List<IndexQuery> list = Lists.newArrayList();
         modelObjects.forEach(entry ->
         {
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(entry).build();
            list.add(indexQuery);
         });
         elasticsearchTemplate.bulkIndex(list);
      }
   }

   public void updateNodeInstances(List<UpdateQuery> updateQueries) {
      elasticsearchTemplate.bulkUpdate(updateQueries);
   }

   public void delete(List<String> ids)
   {
      if (!ids.isEmpty()) {
         for (List<String> idsToBeDeleted : Iterables.partition(ids, UnitElasticConstants.MAX_FETCH_SIZE))
         {
            CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria(UnitElasticConstants.ES_FIELD_ID).in(idsToBeDeleted));
            elasticsearchTemplate.delete(criteriaQuery, NodeInstanceEntityWithAttributeHistory.class);
         }
      }
   }

   public List<NodeInstanceEntityWithAttributeHistory> findByNames(String... names)
   {
      List<NodeInstanceEntityWithAttributeHistory> elements = Lists.newArrayList();
      if (names.length == 0)
         return elements;
      int offset = 0;
      long total = 1;
      for (; offset < total; offset = offset + names.length)
      {
         Page<NodeInstanceEntityWithAttributeHistory> page = findByNames(offset, names.length, names);
         elements.addAll(page.getContent());
         total = page.getTotalElements();
      }
      return elements;
   }

   private Page<NodeInstanceEntityWithAttributeHistory> findByNames(int offset, int limit, String... names)
   {
      int pageNo = offset / limit;
      BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
         .filter(QueryBuilders.termsQuery(UnitElasticConstants.ES_FIELD_NAME, Lists.newArrayList(names)));
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), NodeInstanceEntityWithAttributeHistory.class);
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findTerminatedNodeInstances(int limit, int offset, long endTime)
   {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      query.must(QueryBuilders.rangeQuery("updateDate").lte(endTime));
      query.must(QueryBuilders.termQuery("status", "Terminated"));
      return executeQuery(query, limit, offset);
   }

   public Page<NodeInstanceEntityWithAttributeHistory> searchAttributeHistory(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime, int limit, int offset)
   {
      QueryBuilder query = buildQuery(nodeInstanceId, attributeName, attributeValue, fromTime, toTime);
      return executeQuery(query, limit, offset);
   }

   private QueryBuilder buildQuery(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime) {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      if(isValuePresent(nodeInstanceId)) {
         query.must(QueryBuilders.termQuery("id", nodeInstanceId));
      }
      if(isValuePresent(attributeName) && ( isValuePresent(attributeValue) || (fromTime != null && toTime !=null) )) {
         BoolQueryBuilder attributeNestedBooleanQuery = QueryBuilders.boolQuery();
         attributeNestedBooleanQuery.must(QueryBuilders.termQuery("attributes.name", attributeName));
         if(isValuePresent(attributeValue)) {
            attributeNestedBooleanQuery.must(QueryBuilders.termQuery("attributes.value", attributeValue));
         }
         if(fromTime != null && toTime !=null) {
            attributeNestedBooleanQuery.should(QueryBuilders.rangeQuery("attributes.startDate").gte(fromTime).lte(toTime));
            attributeNestedBooleanQuery.should(QueryBuilders.rangeQuery("attributes.endDate").gte(fromTime).lte(toTime));
            attributeNestedBooleanQuery.should(QueryBuilders.boolQuery()
                                                                       .must(QueryBuilders.rangeQuery("attributes.startDate").lte(fromTime))
                                                                       .must(QueryBuilders.rangeQuery("attributes.endDate").gte(toTime)));
         }
         query.must(QueryBuilders.nestedQuery("attributes", attributeNestedBooleanQuery, ScoreMode.None));
      }
      return query;
   }

   private boolean isValuePresent(String value) {
      return value !=null && !value.isEmpty();
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, long time, int limit, int offset)
   {
    BoolQueryBuilder query = QueryBuilders.boolQuery()
                                                 .must(QueryBuilders.termQuery("id", nodeInstanceId))
                                                 .must(QueryBuilders.nestedQuery("attributes",
                                                                                  QueryBuilders.boolQuery()
                                                                                                       .must(termQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_NAME, attributeName))
                                                                                                       .must(QueryBuilders.boolQuery()
                                                                                                                                     .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_START_TIME).lte(time))
                                                                                                                                     .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_UPDATE_TIME).gte(time)))
                                                                                 ,ScoreMode.None)
                                                    );
      return executeQuery(query, limit, offset);
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByAttributeAndTime(String attributeName, long time, int limit, int offset)
   {
    BoolQueryBuilder query = QueryBuilders.boolQuery()
                                                 .must(QueryBuilders.nestedQuery("attributes",
                                                                                  QueryBuilders.boolQuery()
                                                                                                       .must(termQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_NAME, attributeName))
                                                                                                       .must(QueryBuilders.boolQuery()
                                                                                                                                     .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_START_TIME).lte(time))
                                                                                                                                     .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_UPDATE_TIME).gte(time)))
                                                                                 ,ScoreMode.None)
                                                    );
      return executeQuery(query, limit, offset);
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByAttributes(Map<String, String> attributes, int limit, int offset)
   {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      attributes.forEach((k,v) -> query.must(QueryBuilders.nestedQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTES,
                                                                       QueryBuilders.boolQuery().must(termQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_NAME, k))
                                                                                                .must(termQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_VALUE, v))
                                                                                                .must(termQuery(UnitElasticConstants.ES_FIELD_ATTRIBUTE_UPDATE_TIME, UnitElasticConstants.NO_END_DATE_VALUE)),
                                                                       ScoreMode.None)
                                            )
                        );
      return executeQuery(query, limit, offset);
   }


   private Page<NodeInstanceEntityWithAttributeHistory> executeQuery(QueryBuilder query, int limit, int offset)
   {
      int pageNo = offset / limit;
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), NodeInstanceEntityWithAttributeHistory.class);
   }

}
