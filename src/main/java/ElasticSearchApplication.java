import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = { "config", "infra", "rest", "services", "repo" })
public class ElasticSearchApplication
{
   public static void main(String[] args)
   {
      SpringApplication.run(ElasticSearchApplication.class, args);
   }

   @PostConstruct
   public void logStartMessage()
   {
      log.info("Application Started.......");
   }

}
