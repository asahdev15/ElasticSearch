package infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface AttributeHistoryRepository<T>
{

   void store(T modelObjects);

   void store(List<T> modelObjects);

   Optional<T> findByNodeInstanceId(String nodeInstanceId);

   List<T> findByNodeInstanceIds(List<String> nodeInstanceIds);

   Page<T> findByNodeInstanceIdAndAttributeName(String nodeInstanceId, String attributeName, int limit, int offset);

   Page<T> findByAttributeNameAndValue(String attributeName, String attributeValue, int limit, int offset);

   Page<T> findByAttributeNameAndTime(String attributeName, long fromTime, long toTime, int limit, int offset);

   Page<T> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, String attributeValue, long fromTime, long toTime, int limit, int offset);

   Page<T> findAttributeHistoryEntity(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime, int limit, int offset);

   void refresh();

}