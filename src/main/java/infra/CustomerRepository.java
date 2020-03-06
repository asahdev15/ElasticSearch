package infra;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

public interface CustomerRepository<T>
{

   ElasticsearchTemplate getElasticsearchTemplate();

   void refresh();

   void save(T model);

   void saveAll(List<T> models);

   Page<T> searchAll(int limit, int offset);

   Optional<T> searchById(String id);

   List<T> searchByNameOrAge(String firstName, String lastName, Integer age, long time, int limit, int offset);

   List<T> searchByTime(long time, int limit, int offset);

   List<T> searchByAttributes(Map<String, String> attributes, int limit, int offset);

   void updateCustomers(List<UpdateQuery> updateQueries);

   void deleteByIds(List<String> ids);

}

