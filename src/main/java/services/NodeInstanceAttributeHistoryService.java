package services;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import domain.AttributeEntity;
import domain.NodeInstanceEntityWithAttributeHistory;
import infra.NodeInstanceAttributeHistoryRepositoryImpl;
import infra.UnitElasticConstants;
import rest.AttributeDto;
import rest.AttributeHistoryResponse;
import rest.AttributeHistoryResult;
import rest.AttributeInfoDto;
import rest.DatasetInfoDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class NodeInstanceAttributeHistoryService extends AbstractOperation
{

   private NodeInstanceAttributeHistoryRepositoryImpl repo;

   private static final String INSERT = "INSERT";
   private static final String UPDATE = "UPDATE";
   private static final String UPDATE_VIA_PAINLESS = "UPDATE_VIA_PAINLESS";
   private static final String TERMINATE = "TERMINATE";
   private static final String TERMINATE_VIA_PAINLESS = "TERMINATE_VIA_PAINLESS";
   private static final String PURGE_VIA_ID = "PURGE_VIA_ID";
   private static final String PURGE_VIA_TIME = "PURGE_VIA_TIME";

   @Override
   public long invokeOperation(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      long duration = 0L;
      if(operationName.equals(INSERT)) {
         duration = insert(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(UPDATE)) {
         duration = update(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(UPDATE_VIA_PAINLESS)) {
         duration = updateViaPainless(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(TERMINATE)) {
         duration = terminate(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(TERMINATE_VIA_PAINLESS)) {
         duration = terminateViaPainless(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(PURGE_VIA_ID)) {
         duration = purgeViaID(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }else if(operationName.equals(PURGE_VIA_TIME)) {
         duration = purgeViaTime(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
      }
      log.info("Executing : " + operationName + " ; totalDuration:{}; batchNumber:{}", duration, batchNumber);
      return duration;
   }

   private long insert(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      List<NodeInstanceEntityWithAttributeHistory> nodeInstances =  generateDataSet(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount);
      return storeNodeInstances(nodeInstances);
   }

   private long update(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      List<NodeInstanceEntityWithAttributeHistory> nodeInstances =  getNodeInstanceEntities(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex);
      return storeNodeInstanceUpdateAttributes(nodeInstances, ((AttributeInfoDto) inputObject).getAttributes());
   }

   private long updateViaPainless(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      String scriptText = buildUpdateNodeInstanceScriptText();
      Map<String, List<AttributeDto>> nodeInstanceIDAttributeMap = Maps.newHashMap();
      for(int i = nodeInstanceIdStartIndex; i <=nodeInstanceIdEndIndex ; i++) {
         nodeInstanceIDAttributeMap.put(String.valueOf(i), ((AttributeInfoDto) inputObject).getAttributes());
      }
      return updateNodeInstanceViaPainlessScript(nodeInstanceIDAttributeMap, (new Date()).getTime(), scriptText);
   }

   private long terminate(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      List<String> nodeInstanceNames = Lists.newArrayList();
      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
      {
         nodeInstanceNames.add(UnitElasticConstants.getNodeInstanceName(i));
      }
      long duration = invalidateNodeInstances(nodeInstanceNames);
      return duration;
   }

   private long terminateViaPainless(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      StringBuilder scriptText = new StringBuilder();
      scriptText.append("ctx._source.status = params.status;");
      scriptText.append("ctx._source.updateDate = params.endDate;");
      String scriptTextStr = scriptText.toString();
      Map<String, Long> nodeInstanceEndDate = Maps.newHashMap();
      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
      {
         nodeInstanceEndDate.put(String.valueOf(i), new Date().getTime());
      }
      return terminatedNodeInstanceViaPainlessScript(nodeInstanceEndDate, scriptTextStr);
   }

   private long purgeViaID(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      List<String> nodeInstanceIds = Lists.newArrayList();
      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
      {
         nodeInstanceIds.add(""+i);
      }
      long startTime = System.nanoTime();
      repo.delete(nodeInstanceIds);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   private long purgeViaTime(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
      long startTime = System.nanoTime();
      Page<NodeInstanceEntityWithAttributeHistory> result = repo.findTerminatedNodeInstances(nodeInstanceIdEndIndex-nodeInstanceIdStartIndex+1, 0, ((Long)inputObject).longValue());
      List<String> nodeInstanceIds = result.getContent().stream().map(item -> item.getId()).collect(Collectors.toList());
      repo.delete(nodeInstanceIds);
      repo.refresh();
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   public void store(DatasetInfoDto dtoInput)
   {
      startOperation(INSERT, dtoInput.getNodeInstanceBatchCount(),  dtoInput.getNodeInstanceCount(), dtoInput.getAttributesCount(), dtoInput);
   }


   public void updateAttributeWithHistoryNestedApproach(AttributeInfoDto dtoInput) {
      startOperation(UPDATE, dtoInput.getNodeInstanceBatchCount(),  dtoInput.getNodeInstanceCount(), dtoInput.getAttributes().size(), dtoInput);
   }

   public void callUpdateNodeInstanceViaPainlessScript(AttributeInfoDto dtoInput) {
      startOperation(UPDATE_VIA_PAINLESS, dtoInput.getNodeInstanceBatchCount(),  dtoInput.getNodeInstanceCount(), dtoInput.getAttributes().size(), dtoInput);
   }

   public void invalidateAllNodeInstancesViaPainLessScript(int batchCount) {
      startOperation(TERMINATE_VIA_PAINLESS, batchCount, 1000000, 0, null);
   }


   public void invalidateAllNodeInstances(int batchCount) {
      startOperation(TERMINATE, batchCount, 1000000, 0, null);
   }

   public void purgeNodeInstancesByIds(int batchCount) {
      startOperation(PURGE_VIA_ID, batchCount, 1000000, 0, null);
   }

   public void purgeNodeInstancesByTime(int batchCount, long endTime) {
      startOperation(PURGE_VIA_TIME, batchCount, 1000000, 0, endTime);
//      long totalDuration = 0L;
//      int count = 0;
//      long minDuration = Long.MAX_VALUE;
//      long maxDuration = Long.MIN_VALUE;
//      int batchNumber = 1000000 / batchCount;
//      while(batchNumber > 0) {
//         long startTime = System.nanoTime();
//         Page<NodeInstanceEntityWithAttributeHistory> result = repo.findTerminatedNodeInstances(batchCount, 0, endTime);
//         List<String> nodeInstanceIds = result.getContent().stream().map(item -> item.getId()).collect(Collectors.toList());
//         repo.delete(nodeInstanceIds);
//         long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//         log.info("End : purgeNodeInstancesByTerminateStatusAndEndDate; count:{}; duration:{}; batchNumber:{}", nodeInstanceIds.size(), duration, batchNumber);
//         minDuration = Math.min(minDuration, duration);
//         maxDuration = Math.max(maxDuration, duration);
//         count += nodeInstanceIds.size();
//         totalDuration += duration;
//         batchNumber--;
//      }
//      log.info("Finish : purgeNodeInstancesByTerminateStatusAndEndDate; count:{}; minDuration:{} ; maxDuration:{} ; totalDuration:{}", count, minDuration, maxDuration, totalDuration);
   }


   public AttributeHistoryResponse getNodeInstanceWithAttributeHistory(
      String nodeInstanceId,
      String attributeName,
      String attributeValue,
      Long fromTime,
      Long toTime,
      int limit,
      int offsetInput)
   {

      AttributeHistoryResponse result = new AttributeHistoryResponse();
      Map<String, Map<String, List<AttributeHistoryResult>>> responseMap = new HashMap<String, Map<String,List<AttributeHistoryResult>>>();
      result.setResponse(responseMap);
      long totalDuration = 0L;
      long minDuration = Long.MAX_VALUE;
      long maxDuration = Long.MIN_VALUE;

      int total = limit;
      int offset = 0;
      for(; offset < total ; offset += 1000) {
         long startTime = System.nanoTime();
         log.info("Executing : getNodeInstanceWithAttributeHistory;  offset:{}; duration;{}", offset);
         Page<NodeInstanceEntityWithAttributeHistory> page = repo.searchAttributeHistory(nodeInstanceId, attributeName, attributeValue, fromTime, toTime, 1000, offset);
         List<NodeInstanceEntityWithAttributeHistory>  content = page.getContent();
         for(NodeInstanceEntityWithAttributeHistory item : content) {
            String node_instance_id = item.getId();
            Map<String, List<AttributeHistoryResult>> attributeHistoryMap = Maps.newHashMap();
            if(responseMap.containsKey(node_instance_id)) {
               attributeHistoryMap = responseMap.get(node_instance_id);
            }else {
               responseMap.put(node_instance_id, attributeHistoryMap);
            }
            List<AttributeEntity> nodeInstanceAttributes = item.getAttributes();
            for(AttributeEntity attributeEntity : nodeInstanceAttributes) {
               if(attributeEntity.getName().equals(attributeName)) {
                  List<AttributeHistoryResult> attributeHistoryList = Lists.newArrayList();
                  if(attributeHistoryMap.containsKey(attributeName)) {
                     attributeHistoryList = attributeHistoryMap.get(attributeName);
                  }else {
                     attributeHistoryMap.put(attributeName, attributeHistoryList);
                  }
                  if(fromTime!=null && toTime!=null) {
                     if(applyTimeCondition(attributeEntity, fromTime, toTime)) {
                        attributeHistoryList.add(new AttributeHistoryResult(attributeEntity.getValue(), attributeEntity.getDataType(), attributeEntity.getStartDate().getTime(), attributeEntity.getEndDate().getTime()));
                     }
                  }else {
                     attributeHistoryList.add(new AttributeHistoryResult(attributeEntity.getValue(), attributeEntity.getDataType(), attributeEntity.getStartDate().getTime(), attributeEntity.getEndDate().getTime()));
                  }
               }
            }
         }
         long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
         minDuration = Math.min(minDuration, duration);
         maxDuration = Math.max(maxDuration, duration);
         totalDuration += duration;
      }
      log.info("End : SEARCH ; duration (ms):{} ; (mins):{} ; min:{} ; max:{} : TPS:{} ; totalCount:{}", totalDuration, totalDuration/1000/60, minDuration, maxDuration, (double)total/(totalDuration/1000), result.getResponse().size());
      return result;
   }

   private boolean applyTimeCondition(AttributeEntity attributeData, Long fromTime, Long toTime) {
      boolean result = false;
      result = (attributeData.getStartDate().getTime() >= fromTime) && (attributeData.getStartDate().getTime() <= toTime );
      result = result || (attributeData.getEndDate().getTime() >= fromTime) && (attributeData.getEndDate().getTime() <= toTime );
      result = result || (attributeData.getStartDate().getTime() < fromTime) && (attributeData.getEndDate().getTime() > toTime );
      return result;
   }

   public List<NodeInstanceEntityWithAttributeHistory> searchNodeInstancesWithActiveAttributesByName(int limit){
      List<String> nodeInstanceNames = Lists.newArrayList();
      for (int i = 1; i <= limit; i++)
      {
         nodeInstanceNames.add(UnitElasticConstants.getNodeInstanceName(i));
      }
      long startTime = System.nanoTime();
      List<NodeInstanceEntityWithAttributeHistory> nodeInstances = repo.findByNames(nodeInstanceNames.toArray(new String[nodeInstanceNames.size()]));
      for(NodeInstanceEntityWithAttributeHistory nodeInstance : nodeInstances) {
         Iterator<AttributeEntity> nodeInstanceAttributesIT = nodeInstance.getAttributes().iterator();
         while(nodeInstanceAttributesIT.hasNext()) {
            AttributeEntity attributeEntity = nodeInstanceAttributesIT.next();
            if(attributeEntity.getEndDate().getTime() < UnitElasticConstants.NO_END_DATE_VALUE ) {
               nodeInstanceAttributesIT.remove();
            }
         }
      }
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      log.info("End : searchNodeInstancesWithActiveAttributesByName;  nodeInstancesCount:{}; duration;{}", nodeInstances.size(), duration);
      return nodeInstances;
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByAttributes(Map<String, String> attributes, int limit, int offset)
   {
      log.info("Start : findByAttributes;attributes Count;{}", attributes.size());
      long startTime = System.nanoTime();
      Page<NodeInstanceEntityWithAttributeHistory> result = repo.findByAttributes(attributes, limit, offset);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      log.info("End : findByAttributes;attributes Count{};duration;{}", result.getSize(), duration);
      return result;
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByAttributeAndTime(String attributeName, long time, int limit, int offset)
   {
      log.info("Start : findByAttributeAndTime; size requested;{}", limit);
      long startTime = System.nanoTime();
      Page<NodeInstanceEntityWithAttributeHistory> result = repo.findByAttributeAndTime(attributeName, time, limit, offset);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      log.info("End : findByAttributeAndTime; result size:{} ; time taken:{}", result.getSize(), duration);
      return result;
   }

   public Page<NodeInstanceEntityWithAttributeHistory> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, long time, int limit, int offset)
   {
      log.info("Start : findByNodeInstanceIdAndAttributeNameAndTime; size requested;{}", limit);
      long startTime = System.nanoTime();
      Page<NodeInstanceEntityWithAttributeHistory> result = repo.findByNodeInstanceIdAndAttributeNameAndTime(nodeInstanceId, attributeName, time, limit, offset);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      log.info("End : findByNodeInstanceIdAndAttributeNameAndTime; result size:{} ; time taken:{}", result.getSize(), duration);
      return result;
   }

   private long terminatedNodeInstanceViaPainlessScript(Map<String, Long> nodeInstanceEndDate, String scriptText) {
      long startTime = System.nanoTime();
      List<UpdateQuery> updateQueries = Lists.newArrayList();
      for(Entry<String, Long> item : nodeInstanceEndDate.entrySet()) {
         HashMap<String, Object> scriptParams = Maps.newHashMap();
         scriptParams.put("endDate", item.getValue());
         scriptParams.put("status", "Terminated");
         UpdateQuery updateQuery = buildUpdateQuery(item.getKey(), scriptText, scriptParams);
         updateQueries.add(updateQuery);
      }
      repo.updateNodeInstances(updateQueries);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   private long updateNodeInstanceViaPainlessScript(Map<String, List<AttributeDto>> nodeInstanceIDAttributeMap, long attributeEndDateUpdate, String scriptText) {
      long startTime = System.nanoTime();
      List<UpdateQuery> updateQueries = Lists.newArrayList();
      for(Entry<String, List<AttributeDto>> item : nodeInstanceIDAttributeMap.entrySet()) {
         HashMap<String, Object> scriptParams = buildScriptParams(item.getValue());
         UpdateQuery updateQuery = buildUpdateQuery(item.getKey(), scriptText, scriptParams);
         updateQueries.add(updateQuery);
      }
      repo.updateNodeInstances(updateQueries);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   private UpdateQuery buildUpdateQuery(String nodeInstanceId, String scriptText, HashMap<String, Object> scriptParams) {
      UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
      updateQueryBuilder.withClass(NodeInstanceEntityWithAttributeHistory.class);
      updateQueryBuilder.withId(nodeInstanceId);
      UpdateRequestBuilder updateRequestBuilder = UpdateAction.INSTANCE.newRequestBuilder(repo.getElasticsearchTemplate().getClient());
      updateRequestBuilder.setScript(new Script(ScriptType.INLINE, "painless", scriptText , scriptParams));
      updateQueryBuilder.withUpdateRequest(updateRequestBuilder.request());
      return updateQueryBuilder.build();
   }

   private HashMap<String, Object> buildScriptParams(List<AttributeDto> attributesToUpdate){
      HashMap<String, Object> params = Maps.newHashMap();
      params.put("endDate", UnitElasticConstants.NO_END_DATE_VALUE);
      HashMap<String, Object> attributes = Maps.newHashMap();
      attributesToUpdate.forEach(item -> {
         HashMap<String, Object> attMap = Maps.newHashMap();
         attMap.put("name", item.getName());
         attMap.put("value", item.getValue());
         attMap.put("dataType", item.getType());
         attMap.put("startDate", item.getStartDate().getTime());
         attMap.put("endDate", item.getEndDate().getTime());
         attributes.put(item.getName(), attMap);
      });
      params.put("attributes", attributes);
      return params;
   }

   private String buildUpdateNodeInstanceScriptText() {
      StringBuilder scriptText = new StringBuilder();
      scriptText.append("for (int i = 0; i < ctx._source.attributes.length; ++i) {");
      scriptText.append("String attName = ctx._source.attributes[i].name;");
      scriptText.append("long endDate = ctx._source.attributes[i].endDate;");
      scriptText.append("Map attMap = params.get(\"attributes\").get(attName);");
      scriptText.append("if(attMap!=null &&  endDate == params.endDate) {");
      scriptText.append("ctx._source.attributes[i].endDate = attMap.get(\"startDate\")-1;");
      scriptText.append("}");
      scriptText.append("}");
      scriptText.append("Map attMap = params.get(\"attributes\");");
      scriptText.append("for(String item : attMap.keySet()){");
      scriptText.append("ctx._source.attributes.add(attMap.get(item));");
      scriptText.append("}");
      return scriptText.toString();
   }

   private long storeNodeInstanceUpdateAttributes(List<NodeInstanceEntityWithAttributeHistory> nodeInstances, List<AttributeDto> attributesToUpdate) {
      long startTime = System.nanoTime();
      Date updateDate = new Date(startTime);
      nodeInstances.forEach(item -> updateAttributes(item, attributesToUpdate, updateDate));
      repo.store(nodeInstances);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   private long storeNodeInstances(List<NodeInstanceEntityWithAttributeHistory> nodeInstances) {
      long startTime = System.nanoTime();
      repo.store(nodeInstances);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }

   private List<NodeInstanceEntityWithAttributeHistory> getNodeInstanceEntities(int startIndex, int endIndex){
      List<String> nodeInstanceNames = Lists.newArrayList();
      for(int i = startIndex; i <= endIndex ; i++) {
         nodeInstanceNames.add(UnitElasticConstants.getNodeInstanceName(i));
      }
      List<NodeInstanceEntityWithAttributeHistory> nodeInstances = getNodeInstances(nodeInstanceNames);
      return nodeInstances;
   }

   private List<NodeInstanceEntityWithAttributeHistory> getNodeInstances(List<String> nodeInstanceNames){
      return repo.findByNames(nodeInstanceNames.toArray(new String[0]));
   }

   private void updateAttributes(NodeInstanceEntityWithAttributeHistory nodeInstanceEntity, List<AttributeDto> attributesToUpdate, Date date) {

      List<AttributeEntity> existingAttributes = nodeInstanceEntity.getAttributes();
      attributesToUpdate.forEach(attributeToUpdate -> invalidateAttribute(existingAttributes, attributeToUpdate, date));
      existingAttributes.addAll(toAttributeEntity(attributesToUpdate, date));
      nodeInstanceEntity.setUpdateDate(date);
   }

   private void invalidateAttribute(List<AttributeEntity> existingAttributes, AttributeDto attributeToUpdate, Date endDate) {
      existingAttributes.stream()
      .filter(attributeEntity -> isAttributeToUpdatePresent(attributeEntity, attributeToUpdate))
      .forEach(attributeEntity -> attributeEntity.setEndDate(endDate));
   }

   private boolean isAttributeToUpdatePresent(AttributeEntity attributeEntity, AttributeDto attributeToUpdate) {
      return attributeEntity.getName().equals(attributeToUpdate.getName())
         && attributeEntity.getDataType().equals(attributeToUpdate.getType())
         && attributeEntity.getEndDate().getTime()==UnitElasticConstants.NO_END_DATE_VALUE;
   }


   private long invalidateNodeInstances(List<String> names)
   {
      long startTime = System.nanoTime();
      List<NodeInstanceEntityWithAttributeHistory> nodeInstances = repo.findByNames(names.toArray(new String[names.size()]));
      Date endDate = new Date();
      nodeInstances.forEach(entry ->
      {
         entry.setUpdateDate(endDate);
         entry.setStatus("Terminated");
      });
      repo.store(nodeInstances);
      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
      return duration;
   }


// *********** Generate Data Set
   private List<NodeInstanceEntityWithAttributeHistory> generateDataSet(int nodeInstanceIdStartIndex, int nodeInstanceIdEndIndex, int attributesCount)
   {
      List<NodeInstanceEntityWithAttributeHistory> dataSet = Lists.newArrayList();
      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
      {
         dataSet.add(buildModel(i, attributesCount));
      }
      return dataSet;
   }

   private NodeInstanceEntityWithAttributeHistory buildModel(int nodeInstanceId, int attributesCount)
   {
      Date startDate = new Date();
      List<AttributeEntity> attributes = buildAttributeList(startDate, attributesCount);
      NodeInstanceEntityWithAttributeHistory nodeInstanceEntity = buildNodeInstanceEntity(nodeInstanceId, startDate, attributes);
      return nodeInstanceEntity;
   }

   private List<AttributeEntity> buildAttributeList(Date startDate, int attributesCount){
      List<AttributeEntity> attributeEntities = Lists.newArrayList();
      for (int i = 1; i <= attributesCount; i++)
      {
         attributeEntities.add(buildAttributeEntity(i, startDate));
      }
      return attributeEntities;
   }

   private NodeInstanceEntityWithAttributeHistory buildNodeInstanceEntity(int nodeInstanceId,  Date startDate, List<AttributeEntity> attributeEntities) {
      String nodeInstanceName = UnitElasticConstants.getNodeInstanceName(nodeInstanceId);
      NodeInstanceEntityWithAttributeHistory nodeInstanceEntity = NodeInstanceEntityWithAttributeHistory.builder()
                                                                     .id(String.valueOf(nodeInstanceId))
                                                                     .name(nodeInstanceName)
                                                                     .nodeType("nodeType")
                                                                     .startDate(startDate)
                                                                     .updateDate(UnitElasticConstants.NO_END_DATE)
                                                                     .attributes(attributeEntities)
                                                                     .allAttributes(getAllAttributes(attributeEntities))
                                                                     .metaData(getMetadata(nodeInstanceName))
                                                                     .status("ACTIVE")
                                                                     .build();
      return nodeInstanceEntity;
   }

   private Map<String, String> getMetadata(String nodeInstanceName){
      Map<String, String> metadata = Maps.newHashMap();
      metadata.put("NodeInstanceMapping" + "-PM", nodeInstanceName + "-PM");
      metadata.put("NodeInstanceMapping" + "-FM", nodeInstanceName + "-FM");
      metadata.put("NodeInstanceMapping" + "-TT", nodeInstanceName + "-TT");
      metadata.put("NodeInstanceMapping" + "-EA", nodeInstanceName + "-EA");
      return metadata;
   }

   private String getAllAttributes(List<AttributeEntity> attributeEntities) {
      StringBuilder sb = new StringBuilder();
      attributeEntities.forEach(item -> sb.append(getAttributeNameValue(item)));
      return sb.toString();
   }

   private String getAttributeNameValue(AttributeEntity attributeEntity) {
      StringBuilder sb = new StringBuilder();
      sb.append(attributeEntity.getName()).append("\u00B6").append(attributeEntity.getValue()).append("\u00A7");
      return sb.toString();
   }

   private AttributeEntity buildAttributeEntity(int attributeNumber, Date startDate) {
      return AttributeEntity.builder()
                                 .name(UnitElasticConstants.getAttributeName(attributeNumber))
                                 .value(UnitElasticConstants.getAttributeValue(attributeNumber))
                                 .dataType("String")
                                 .startDate(startDate)
                                 .endDate(UnitElasticConstants.NO_END_DATE)
                                 .build();
   }

    private static List<AttributeEntity> toAttributeEntity(List<AttributeDto> attributesDtos, Date startDate)
    {
       return attributesDtos.stream()
          .map(attributeDto -> buildAttributeEntity(attributeDto, startDate))
          .collect(Collectors.toList());
    }

    private static  AttributeEntity buildAttributeEntity(AttributeDto attributeDto, Date startDate) {
       return AttributeEntity.builder()
          .name(attributeDto.getName())
          .dataType(attributeDto.getType())
          .value(attributeDto.getValue())
          .startDate(attributeDto.getStartDate() != null ? attributeDto.getStartDate() : startDate)
          .endDate(attributeDto.getEndDate() != null ? attributeDto.getEndDate() : UnitElasticConstants.NO_END_DATE)
          .build();
    }


}
