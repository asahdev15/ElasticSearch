package config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.geo.CustomGeoModule;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchTemplateConfig
{
   @Profile("!test")
   @SuppressWarnings("resource")
   @Bean
   public TransportClient transportClient(
      @Value("${elasticsearch.ip}") String host,
      @Value("${elasticsearch.port}") Integer port,
      @Value("${elasticsearch.cluster}") String cluster,
      @Value("${elasticsearch.transport.ping_timeout}") Integer clientPingTimeout,
      @Value("${elasticsearch.transport.nodes_sampler_interval}") Integer clientNodesSamplerInterval,
      @Value("${elasticsearch.user}") String user,
      @Value("${elasticsearch.password}") String password)
      throws UnknownHostException
   {
      Builder builder = Settings.builder()
                                   .put("cluster.name", cluster)
                                   .put("client.transport.ping_timeout", clientPingTimeout, TimeUnit.SECONDS)
                                   .put("client.transport.nodes_sampler_interval", clientNodesSamplerInterval, TimeUnit.SECONDS);
//      if (!Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(password))
//      {
//         builder.put("xpack.security.user", user + ":" + password);
//      }
      TransportClient client = new PreBuiltTransportClient(builder.build())
                                          .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
      return client;
   }

   @Bean
   public ElasticsearchTemplate elasticsearchTemplate(Client client)
   {
      ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(client, new CustomEntityMapper());
      return elasticsearchTemplate;
   }

   public static class CustomEntityMapper implements EntityMapper
   {
      private final ObjectMapper objectMapper;

      public CustomEntityMapper()
      {
         objectMapper = new ObjectMapper();
         objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
         objectMapper.registerModule(new CustomGeoModule());
         objectMapper.registerModule(new JavaTimeModule());
      }

      @Override
      public String mapToString(Object object) throws IOException
      {
         return objectMapper.writeValueAsString(object);
      }

      @Override
      public <T> T mapToObject(String source, Class<T> clazz) throws IOException
      {
         return objectMapper.readValue(source, clazz);
      }
   }

   @Bean
   public ElasticIndexSetting elasticSearchIndexSetting(
      @Value("${elasticsearch.indexes.nodeinstanceWithAttributeHistory}") String unitNodeInstanceWithAttributeHistoryIndexName,
      @Value("${elasticsearch.aliases.nodeinstanceWithAttributeHistory}") String unitNodeInstanceWithAttributeHistoryAliasName,

      @Value("${elasticsearch.indexes.nodeinstance}") String unitNodeInstanceIndexName,
      @Value("${elasticsearch.aliases.nodeinstance}") String unitNodeInstanceAliasName,

      @Value("${elasticsearch.indexes.nodeinstance_attribute_history}") String unitAttributeHistoryIndexName,
      @Value("${elasticsearch.aliases.nodeinstance_attribute_history}") String unitAttributeHistoryAliasName,

      @Value("${elasticsearch.indexes.book}") String bookIndex,
      @Value("${elasticsearch.aliases.book}") String bookIndexAlias,

      @Value("${elasticsearch.indexes.user}") String userIndex,
      @Value("${elasticsearch.aliases.user}") String userIndexAlias)
   {
      return new ElasticIndexSetting( unitNodeInstanceWithAttributeHistoryIndexName,
                                      unitNodeInstanceWithAttributeHistoryAliasName,

                                      unitNodeInstanceIndexName,
                                      unitNodeInstanceAliasName,

                                      unitAttributeHistoryIndexName,
                                      unitAttributeHistoryAliasName,

                                      bookIndex,
                                      bookIndexAlias,

                                      userIndex,
                                      userIndexAlias);
   }
}
