package config;

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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchTemplateConfig
{

   @Profile("!test")
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

   @Bean
   public ElasticIndexSetting elasticSearchIndexSetting(
      @Value("${elasticsearch.indexes.customer}") String customerIndex,
      @Value("${elasticsearch.aliases.customer}") String customerIndexAlias)
   {
      return new ElasticIndexSetting(customerIndex, customerIndexAlias);
   }

}
