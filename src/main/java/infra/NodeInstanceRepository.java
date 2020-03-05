package infra;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

public interface NodeInstanceRepository<T>
{
   ElasticsearchTemplate getElasticsearchTemplate();

   void store(T modelObjects);

   void store(List<T> modelObjects);

   void updateNodeInstances(List<UpdateQuery> updateQueries);

   void delete(List<String> ids);

   Optional<T> findById(String id);

   List<T> findByName(String name);

   List<T> findByNames(String... names);

   Page<T> findTerminatedNodeInstances(int limit, int offset, long endTime);

   Page<T> findByAttributes(Map<String, String> attributes, int limit, int offset);

   Page<T> findByAttributeAndTime(String attributeName, long time, int limit, int offset);

   Page<T> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, long time, int limit, int offset);

   Page<T> findByTime(long time, int limit, int offset);

   void refresh();
}
