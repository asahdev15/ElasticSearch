package config;

import infra.AttributeHistoryRepository;
import infra.AttributeHistoryRepositoryImpl;
import infra.NodeInstanceRepository;
import infra.NodeInstanceRepositoryImpl;
import domain.AttributeHistoryEntity;
import domain.NodeInstanceEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class RepositoryConfig
{

   @Bean
   public NodeInstanceRepository<NodeInstanceEntity> getNodeInstanceRepository(ElasticsearchTemplate elasticsearchTemplate)
   {
      return new NodeInstanceRepositoryImpl(elasticsearchTemplate);
   }

   @Bean
   public AttributeHistoryRepository<AttributeHistoryEntity> getAttributeHistoryRepository(ElasticsearchTemplate elasticsearchTemplate)
   {
      return new AttributeHistoryRepositoryImpl(elasticsearchTemplate);
   }

}
