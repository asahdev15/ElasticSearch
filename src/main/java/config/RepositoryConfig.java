package config;

import domain.Customer;
import infra.CustomerRepository;
import infra.CustomerRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@Configuration
public class RepositoryConfig
{

   @Bean
   public CustomerRepository<Customer> getCustomerRepository(ElasticsearchTemplate elasticsearchTemplate)
   {
      return new CustomerRepositoryImpl(elasticsearchTemplate);
   }

}
