package infra;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import domain.AttributeHistoryEntity;
import domain.NodeInstanceEntity;

public class NodeInstanceRepositoryImpl implements NodeInstanceRepository<NodeInstanceEntity>
{
   private ElasticsearchTemplate elasticsearchTemplate;

   @Override
   public ElasticsearchTemplate getElasticsearchTemplate() {
      return elasticsearchTemplate;
   }

   public NodeInstanceRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate)
   {
      this.elasticsearchTemplate = elasticsearchTemplate;
   }

   @Override
   public void store(NodeInstanceEntity modelObjects)
   {
      IndexQuery indexQuery = new IndexQueryBuilder().withObject(modelObjects).build();
      elasticsearchTemplate.index(indexQuery);
   }

   @Override
   public void store(List<NodeInstanceEntity> modelObjects)
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

   @Override
   public void updateNodeInstances(List<UpdateQuery> updateQueries) {
      elasticsearchTemplate.bulkUpdate(updateQueries);
   }

   @Override
   public void delete(List<String> ids)
   {
      if (!ids.isEmpty()) {
         for (List<String> idsToBeDeleted : Iterables.partition(ids, UnitElasticConstants.MAX_FETCH_SIZE))
         {
            CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria(UnitElasticConstants.ES_FIELD_ID).in(idsToBeDeleted));
            elasticsearchTemplate.delete(criteriaQuery, NodeInstanceEntity.class);
            elasticsearchTemplate.delete(criteriaQuery, AttributeHistoryEntity.class);
         }
      }
   }

   @Override
   public List<NodeInstanceEntity> findByName(String name)
   {
      BoolQueryBuilder query = QueryBuilders.boolQuery().must(termQuery(UnitElasticConstants.ES_FIELD_NAME, name));
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query);
      return elasticsearchTemplate.queryForList(builder.build(), NodeInstanceEntity.class);
   }

   @Override
   public Optional<NodeInstanceEntity> findById(String id)
   {
      GetQuery getQuery = new GetQuery();
      getQuery.setId(id);
      return Optional.ofNullable(elasticsearchTemplate.queryForObject(getQuery, NodeInstanceEntity.class));
   }

   @Override
   public Page<NodeInstanceEntity> findByTime(long time, int limit, int offset){
      BoolQueryBuilder query = QueryBuilders.boolQuery()
                                                      .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_START_DATE_NAME).lte(time))
                                                      .must(QueryBuilders.rangeQuery(UnitElasticConstants.ES_FIELD_UPDATE_DATE_NAME).gte(time));
      return executeQuery(query, limit, offset);
   }

   @Override
   public void refresh()
   {
      elasticsearchTemplate.refresh(NodeInstanceEntity.class);
   }

   @Override
   public Page<NodeInstanceEntity> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, long time, int limit, int offset)
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

   @Override
   public Page<NodeInstanceEntity> findByAttributeAndTime(String attributeName, long time, int limit, int offset)
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

   @Override
   public Page<NodeInstanceEntity> findByAttributes(Map<String, String> attributes, int limit, int offset)
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



   @Override
   public List<NodeInstanceEntity> findByNames(String... names)
   {
      List<NodeInstanceEntity> elements = Lists.newArrayList();
      if (names.length == 0)
         return elements;
      int offset = 0;
      long total = 1;
      for (; offset < total; offset = offset + names.length)
      {
         Page<NodeInstanceEntity> page = findByNames(offset, names.length, names);
         elements.addAll(page.getContent());
         total = page.getTotalElements();
      }
      return elements;
   }

   private Page<NodeInstanceEntity> findByNames(int offset, int limit, String... names)
   {
      int pageNo = offset / limit;
      BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
         .filter(QueryBuilders.termsQuery(UnitElasticConstants.ES_FIELD_NAME, Lists.newArrayList(names)));
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), NodeInstanceEntity.class);
   }

   @Override
   public Page<NodeInstanceEntity> findTerminatedNodeInstances(int limit, int offset, long endTime)
   {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      query.must(QueryBuilders.rangeQuery("updateDate").lte(endTime));
      query.must(QueryBuilders.termQuery("status", "Terminated"));
      return executeQuery(query, limit, offset);
   }

   private Page<NodeInstanceEntity> executeQuery(BoolQueryBuilder query, int limit, int offset)
   {
      int pageNo = offset / limit;
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), NodeInstanceEntity.class);
   }

}
