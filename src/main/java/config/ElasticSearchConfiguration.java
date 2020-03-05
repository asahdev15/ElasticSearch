package config;

import domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.AliasQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableElasticsearchRepositories(basePackages = "repo")
public class ElasticSearchConfiguration
{
   private static final int MAX_ELASTIC_CONNECTION_RETRY = 5;
   private static final int CONNECTION_RETRY_WAIT_IN_MILLIS = 10000;
   private ElasticsearchTemplate elasticsearchTemplate;
   private ElasticIndexSetting elasticIndexSetting;

   @PostConstruct
   public void createIndexWithAlias() throws InterruptedException
   {
      String indexSetting = ElasticsearchTemplate.readFileFromClasspath("index-settings.json");
      for (int i = 1; i <= MAX_ELASTIC_CONNECTION_RETRY; i++)
      {
         try
         {
            createIndexWithAlias(elasticsearchTemplate, indexSetting);
         }
         catch (NoNodeAvailableException e)
         {
            log.error("Error while doing query to elasticsearch retrying;{};after;{};ms", e.getMessage(), CONNECTION_RETRY_WAIT_IN_MILLIS);
            Thread.sleep(CONNECTION_RETRY_WAIT_IN_MILLIS);
            if (i == MAX_ELASTIC_CONNECTION_RETRY)
               throw new RuntimeException("Max Retry Reached for connection to Elastic;" + MAX_ELASTIC_CONNECTION_RETRY, e);
            continue;
         }
         break;
      }
   }

   private void createIndexWithAlias(ElasticsearchTemplate elasticsearchTemplate, String indexSetting)
   {
      createIndexWithAlias(elasticsearchTemplate,
                           elasticIndexSetting.getUnitNodeInstanceWithAttributeHistoryIndexName(),
                           elasticIndexSetting.getUnitNodeInstanceWithAttributeHistoryAliasName(),
                           indexSetting,
                           NodeInstanceEntityWithAttributeHistory.class);

      createIndexWithAlias(elasticsearchTemplate,
                           elasticIndexSetting.getUnitNodeInstanceIndexName(),
                           elasticIndexSetting.getUnitNodeInstanceAliasName(),
                           indexSetting,
                           NodeInstanceEntity.class);

      createIndexWithAlias(elasticsearchTemplate,
                           elasticIndexSetting.getUnitAttributeHistoryIndexName(),
                           elasticIndexSetting.getUnitAttributeHistoryAliasName(),
                           indexSetting,
                           AttributeHistoryEntity.class);

      createIndexWithAlias(elasticsearchTemplate,
              elasticIndexSetting.getBookIndex(),
              elasticIndexSetting.getBookIndexAlias(),
              indexSetting,
              Book.class);

      createIndexWithAlias(elasticsearchTemplate,
              elasticIndexSetting.getUserIndex(),
              elasticIndexSetting.getUserIndexAlias(),
              indexSetting,
              User.class);
   }

   private void createIndexWithAlias(ElasticsearchTemplate elasticsearchTemplate, String indexName, String aliasName, String indexSetting, Class<?> clazz)
   {
      if (!elasticsearchTemplate.indexExists(aliasName))
      {
         if (!elasticsearchTemplate.indexExists(indexName))
         {
            elasticsearchTemplate.createIndex(indexName, indexSetting);
         }
         AliasQuery query = new AliasQuery();
         query.setIndexName(indexName);
         query.setAliasName(aliasName);
         elasticsearchTemplate.addAlias(query);
      }
      if (!elasticsearchTemplate.typeExists(elasticsearchTemplate.getPersistentEntityFor(clazz).getIndexName(),
         elasticsearchTemplate.getPersistentEntityFor(clazz).getIndexType()))
      {
         elasticsearchTemplate.putMapping(clazz);
      }
   }
}
