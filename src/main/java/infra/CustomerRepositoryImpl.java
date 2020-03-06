package infra;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import domain.Customer;
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

public class CustomerRepositoryImpl implements CustomerRepository<Customer>
{
   private ElasticsearchTemplate elasticsearchTemplate;

   public CustomerRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate)
   {
      this.elasticsearchTemplate = elasticsearchTemplate;
   }

   @Override
   public ElasticsearchTemplate getElasticsearchTemplate() {
      return elasticsearchTemplate;
   }

   @Override
   public void refresh() {
      elasticsearchTemplate.refresh(Customer.class);
   }

   @Override
   public void save(Customer modelObjects) {
      IndexQuery indexQuery = new IndexQueryBuilder().withObject(modelObjects).build();
      elasticsearchTemplate.index(indexQuery);
   }

   @Override
   public void saveAll(List<Customer> modelObjects) {
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
   public void updateCustomers(List<UpdateQuery> updateQueries) {
      elasticsearchTemplate.bulkUpdate(updateQueries);
   }

   @Override
   public Page<Customer> searchAll(int limit, int offset){
      int pageNo = offset / limit;
      BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForPage(builder.build(), Customer.class);
   }

   @Override
   public Optional<Customer> searchById(String id) {
      GetQuery getQuery = new GetQuery();
      getQuery.setId(id);
      return Optional.ofNullable(elasticsearchTemplate.queryForObject(getQuery, Customer.class));
   }

   @Override
   public List<Customer> searchByNameOrAge(String firstName, String lastName, Integer age, long time, int limit, int offset){
      BoolQueryBuilder query = QueryBuilders.boolQuery().must(termQuery(ConstantsUtility.ES_FIELD_NAME, firstName));
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query);
      return elasticsearchTemplate.queryForList(builder.build(), Customer.class);
   }

   @Override
   public List<Customer> searchByTime(long time, int limit, int offset){
      BoolQueryBuilder query = QueryBuilders.boolQuery()
              .must(QueryBuilders.rangeQuery(ConstantsUtility.ES_FIELD_START_DATE_NAME).lte(time))
              .must(QueryBuilders.rangeQuery(ConstantsUtility.ES_FIELD_UPDATE_DATE_NAME).gte(time));
      int pageNo = offset / limit;
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForList(builder.build(), Customer.class);
   }

   @Override
   public List<Customer> searchByAttributes(Map<String, String> attributes, int limit, int offset)
   {
      BoolQueryBuilder query = QueryBuilders.boolQuery();
      attributes.forEach((k,v) ->
              query.must(QueryBuilders.nestedQuery(
                           ConstantsUtility.ES_FIELD_ATTRIBUTES,
                           QueryBuilders.boolQuery()
                              .must(termQuery(ConstantsUtility.ES_FIELD_ATTRIBUTE_NAME, k))
                              .must(termQuery(ConstantsUtility.ES_FIELD_ATTRIBUTE_VALUE, v)),
                           ScoreMode.None)
                        )
      );
      int pageNo = offset / limit;
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder().withQuery(query).withPageable(PageRequest.of(pageNo, limit));
      return elasticsearchTemplate.queryForList(builder.build(), Customer.class);
   }

   @Override
   public void deleteByIds(List<String> ids) {
      if (!ids.isEmpty()) {
         for (List<String> idsToBeDeleted : Iterables.partition(ids, ConstantsUtility.MAX_FETCH_SIZE))
         {
            CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria(ConstantsUtility.ES_FIELD_ID).in(idsToBeDeleted));
            elasticsearchTemplate.delete(criteriaQuery, Customer.class);
//            elasticsearchTemplate.delete(criteriaQuery, Attribute.class);
         }
      }
   }

}
