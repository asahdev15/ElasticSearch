package infra;

import java.util.List;
import java.util.Optional;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import domain.AttributeHistoryEntity;

public class AttributeHistoryRepositoryImpl implements AttributeHistoryRepository<AttributeHistoryEntity>
{

   private ElasticsearchTemplate elasticsearchTemplate;

   public AttributeHistoryRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate) {
      this.elasticsearchTemplate = elasticsearchTemplate;
   }

   @Override
   public void refresh()
   {
      elasticsearchTemplate.refresh(AttributeHistoryEntity.class);
   }

   @Override
   public void store(AttributeHistoryEntity modelObjects)
   {
      IndexQuery indexQuery = new IndexQueryBuilder().withObject(modelObjects).build();
      elasticsearchTemplate.index(indexQuery);
   }

   @Override
   public void store(List<AttributeHistoryEntity> modelObjects)
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

   public void delete(List<String> ids)
   {
      if (!ids.isEmpty()) {
         for (List<String> idsToBeDeleted : Iterables.partition(ids, UnitElasticConstants.MAX_FETCH_SIZE))
         {
            CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria(UnitElasticConstants.ES_FIELD_ID).in(idsToBeDeleted));
            elasticsearchTemplate.delete(criteriaQuery, AttributeHistoryEntity.class);
         }
      }
   }

   @Override
   public Optional<AttributeHistoryEntity> findByNodeInstanceId(String nodeInstanceId)
   {
      GetQuery getQuery = new GetQuery();
      getQuery.setId(nodeInstanceId);
      return Optional.ofNullable(elasticsearchTemplate.queryForObject(getQuery, AttributeHistoryEntity.class));
   }

   @Override
   public List<AttributeHistoryEntity> findByNodeInstanceIds(List<String> nodeInstanceIds)
   {
      List<AttributeHistoryEntity> elements = Lists.newArrayList();
      int limit = nodeInstanceIds.size();
      if(limit > 0) {
         int offset = 0;
         long total = 1;
         for (; offset < total; offset = offset + limit){
            BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
                                                              .filter(QueryBuilders.termsQuery("nodeinstanceId", Lists.newArrayList(nodeInstanceIds)));
            Page<AttributeHistoryEntity> page = executeQuery(query, limit, offset);
            elements.addAll(page.getContent());
            total = page.getTotalElements();
         }
      }
      return elements;
   }

   @Override
   public Page<AttributeHistoryEntity> findByNodeInstanceIdAndAttributeName(String nodeInstanceId, String attributeName, int limit, int offset)
   {
      return findAttributeHistoryEntity(nodeInstanceId, attributeName, null, null, null, limit, offset);
   }

   @Override
   public Page<AttributeHistoryEntity> findByAttributeNameAndValue(String attributeName, String attributeValue, int limit, int offset)
   {
      return findAttributeHistoryEntity(null, attributeName, attributeValue, null, null, limit, offset);
//      BoolQueryBuilder query = QueryBuilders.boolQuery();
//      attributes.forEach((name, value) -> query.must(QueryBuilders.nestedQuery("attributes",
//                                                                               QueryBuilders.boolQuery().must(termQuery("attributes.name", name))
//                                                                                                        .must(QueryBuilders.nestedQuery("history",
//                                                                                                                                        QueryBuilders.boolQuery().must(termQuery("attributes.value", name)),
//                                                                                                                                        ScoreMode.None)
//                                                                                                              ),
//                                                                               ScoreMode.None)
//                                                     )
//                        );
//      return executeQuery(query, limit, offset);
   }

   @Override
   public Page<AttributeHistoryEntity> findByAttributeNameAndTime(String attributeName, long fromTime, long toTime, int limit, int offset)
   {
      return findAttributeHistoryEntity(null, attributeName, null, fromTime, toTime, limit, offset);
   }

   @Override
   public
      Page<AttributeHistoryEntity>
      findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, String attributeValue, long fromTime, long toTime, int limit, int offset)
   {
      return findAttributeHistoryEntity(nodeInstanceId, attributeName, attributeValue, fromTime, toTime, limit, offset);
   }

   @Override
   public Page<AttributeHistoryEntity> findAttributeHistoryEntity(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime, int limit, int offset){
      QueryBuilder query = buildQuery(nodeInstanceId, attributeName, attributeValue, fromTime, toTime);
      return executeQuery(query, limit, offset);
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

   private Page<AttributeHistoryEntity> executeQuery(QueryBuilder query, int limit, int offset)
   {
      int pageNo = offset / limit;
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), AttributeHistoryEntity.class);
   }


}
