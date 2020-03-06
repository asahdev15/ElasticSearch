package services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import infra.ConstantsUtility;
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
import infra.CustomerRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@AllArgsConstructor
//@Service
public class NodeInstanceService
{

//   private CustomerRepository<NodeInstanceEntity> repo;
//   private static final String INSERT = "INSERT";
//   private static final String UPDATE = "UPDATE";
//   private static final String UPDATE_VIA_PAINLESS = "UPDATE_VIA_PAINLESS";
//   private static final String TERMINATE = "TERMINATE";
//   private static final String TERMINATE_VIA_PAINLESS = "TERMINATE_VIA_PAINLESS";
//   private static final String PURGE_VIA_ID = "PURGE_VIA_ID";
//   private static final String PURGE_VIA_TIME = "PURGE_VIA_TIME";
//
//
//   @Override
//   public long invokeOperation(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      long duration = 0L;
//      if(operationName.equals(INSERT)) {
//         duration = insert(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
//      }
//      /*
//       * else if(operationName.equals(UPDATE)) { duration = update(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount,
//       * batchNumber, inputObject); }else if(operationName.equals(UPDATE_VIA_PAINLESS)) { duration = updateViaPainless(operationName, nodeInstanceIdStartIndex,
//       * nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject); }
//       */
//      else if(operationName.equals(TERMINATE)) {
//         duration = terminate(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
//      }else if(operationName.equals(TERMINATE_VIA_PAINLESS)) {
//         duration = terminateViaPainless(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
//      }else if(operationName.equals(PURGE_VIA_ID)) {
//         duration = purgeViaID(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
//      }else if(operationName.equals(PURGE_VIA_TIME)) {
//         duration = purgeViaTime(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
//      }
//      log.info("Executing : " + operationName + " ; totalDuration:{}; batchNumber:{}", duration, batchNumber);
//      return duration;
//   }
//
//   private long insert(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      List<NodeInstanceEntity> nodeInstances =  generateDataSet(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount);
//      return storeNodeInstances(nodeInstances);
//   }
//
////   private long update(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
////      List<NodeInstanceEntityWithAttributeHistory> nodeInstances =  getNodeInstanceEntities(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex);
////      return storeNodeInstanceUpdateAttributes(nodeInstances, ((AttributeInfoDto) inputObject).getAttributes());
////   }
////
////   private long updateViaPainless(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
////      String scriptText = buildUpdateNodeInstanceScriptText();
////      Map<String, List<AttributeDto>> nodeInstanceIDAttributeMap = Maps.newHashMap();
////      for(int i = nodeInstanceIdStartIndex; i <=nodeInstanceIdEndIndex ; i++) {
////         nodeInstanceIDAttributeMap.put(String.valueOf(i), ((AttributeInfoDto) inputObject).getAttributes());
////      }
////      return updateNodeInstanceViaPainlessScript(nodeInstanceIDAttributeMap, (new Date()).getTime(), scriptText);
////   }
//
//   private long terminate(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      List<String> nodeInstanceNames = Lists.newArrayList();
//      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
//      {
//         nodeInstanceNames.add(ConstantsUtility.getNodeInstanceName(i));
//      }
//      long duration = invalidateNodeInstances(nodeInstanceNames);
//      return duration;
//   }
//
//   private long terminateViaPainless(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      StringBuilder scriptText = new StringBuilder();
//      scriptText.append("ctx._source.status = params.status;");
//      scriptText.append("ctx._source.updateDate = params.endDate;");
//      String scriptTextStr = scriptText.toString();
//      Map<String, Long> nodeInstanceEndDate = Maps.newHashMap();
//      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
//      {
//         nodeInstanceEndDate.put(String.valueOf(i), new Date().getTime());
//      }
//      return terminatedNodeInstanceViaPainlessScript(nodeInstanceEndDate, scriptTextStr);
//   }
//
//   private long terminatedNodeInstanceViaPainlessScript(Map<String, Long> nodeInstanceEndDate, String scriptText) {
//      long startTime = System.nanoTime();
//      List<UpdateQuery> updateQueries = Lists.newArrayList();
//      for(Entry<String, Long> item : nodeInstanceEndDate.entrySet()) {
//         HashMap<String, Object> scriptParams = Maps.newHashMap();
//         scriptParams.put("endDate", item.getValue());
//         scriptParams.put("status", "Terminated");
//         UpdateQuery updateQuery = buildUpdateQuery(item.getKey(), scriptText, scriptParams);
//         updateQueries.add(updateQuery);
//      }
//      repo.updateNodeInstances(updateQueries);
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//   private UpdateQuery buildUpdateQuery(String nodeInstanceId, String scriptText, HashMap<String, Object> scriptParams) {
//      UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
//      updateQueryBuilder.withClass(NodeInstanceEntity.class);
//      updateQueryBuilder.withId(nodeInstanceId);
//      UpdateRequestBuilder updateRequestBuilder = UpdateAction.INSTANCE.newRequestBuilder(repo.getElasticsearchTemplate().getClient());
//      updateRequestBuilder.setScript(new Script(ScriptType.INLINE, "painless", scriptText , scriptParams));
//      updateQueryBuilder.withUpdateRequest(updateRequestBuilder.request());
//      return updateQueryBuilder.build();
//   }
//
//   private long purgeViaID(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      List<String> nodeInstanceIds = Lists.newArrayList();
//      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
//      {
//         nodeInstanceIds.add(""+i);
//      }
//      long startTime = System.nanoTime();
//      repo.delete(nodeInstanceIds);
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//   private long purgeViaTime(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      long startTime = System.nanoTime();
//      Page<NodeInstanceEntity> result = repo.findTerminatedNodeInstances(nodeInstanceIdEndIndex-nodeInstanceIdStartIndex+1, 0, ((Long)inputObject).longValue());
//      List<String> nodeInstanceIds = result.getContent().stream().map(item -> item.getId()).collect(Collectors.toList());
//      repo.delete(nodeInstanceIds);
//      repo.refresh();
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//
//   public void store(DatasetInfoDto dtoInput)
//   {
//      startOperation(INSERT, dtoInput.getNodeInstanceBatchCount(),  dtoInput.getNodeInstanceCount(), dtoInput.getAttributesCount(), dtoInput);
//   }
//
//   private long storeNodeInstances(List<NodeInstanceEntity> nodeInstances) {
//      long startTime = System.nanoTime();
//      repo.store(nodeInstances);
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//   public Page<NodeInstanceEntity> findByTime(long time, int limit, int offset){
//      long startTime = System.nanoTime();
//      log.info("Start : Find NodeInstance by Time");
//      Page<NodeInstanceEntity> result = repo.findByTime(time, limit, offset);
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      log.info("Start : Find NodeInstance by Time; nodeinstance count:{}; duration:{}", result.getSize(), duration);
//      return result;
//   }
//
//   public void invalidateAllNodeInstances(int batchCount) {
//      startOperation(TERMINATE, batchCount, 1000000, 0, null);
//   }
//
//   private long invalidateNodeInstances(List<String> names)
//   {
//      long startTime = System.nanoTime();
//      List<NodeInstanceEntity> nodeInstances = repo.findByNames(names.toArray(new String[names.size()]));
//      Date endDate = new Date();
//      nodeInstances.forEach(entry ->
//      {
//         entry.setUpdateDate(endDate);
//         entry.setStatus("Terminated");
//      });
//      repo.store(nodeInstances);
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//   public void purgeNodeInstancesByIds(int batchCount) {
//      startOperation(PURGE_VIA_ID, batchCount, 1000000, 0, null);
//   }
//
//   public void purgeNodeInstancesByTerminateStatusAndEndDate(int nodeInstanceBatchSizeInput, long endTime) {
//      startOperation(PURGE_VIA_TIME, nodeInstanceBatchSizeInput, 1000000, 0, endTime);
////      log.info("Start : PURGE_VIA_TIME ; nodeInstanceBatchSize:{} ; nodeInstanceCount:{}; attributesCount:{}", nodeInstanceBatchSizeInput, 1000000, 0);
////      List<String> nodeInstanceIds = Lists.newArrayList();
////      long totalDuration = 0L;
////      long minDuration = Long.MAX_VALUE;
////      long maxDuration = Long.MIN_VALUE;
////      int nodeInstanceBatchSize = nodeInstanceBatchSizeInput;
////      int totalNodeInstanceToInsert = 1000000;
////      int batchSize = totalNodeInstanceToInsert / nodeInstanceBatchSize;
////      int batchNumber = batchSize ;
////      boolean terminatedNodeInstancesPresent = true;
////      while(terminatedNodeInstancesPresent) {
////         long startTime = System.nanoTime();
////         Page<NodeInstanceEntity> result = repo.findTerminatedNodeInstances(nodeInstanceBatchSizeInput, 0, endTime, nodeInstanceIds);
////         if(result.getContent() != null && result.getContent().size() > 0) {
////            List<String> nodeInstanceIdsNew = result.getContent().stream().map(item -> item.getId()).collect(Collectors.toList());
////            repo.delete(nodeInstanceIdsNew);
////            nodeInstanceIds.addAll(nodeInstanceIdsNew);
////            log.info("DELETING  : PURGE_VIA_TIME; nodeInstanceIdsNew SIZE:{}; batchNumber:{}", nodeInstanceIdsNew.size(), batchNumber);
////         }else {
////            terminatedNodeInstancesPresent = false;
////         }
////         long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
////         log.info("EXECUTING  : PURGE_VIA_TIME; duration:{}; batchNumber:{}", duration, batchNumber);
////         minDuration = Math.min(minDuration, duration);
////         maxDuration = Math.max(maxDuration, duration);
////         totalDuration = totalDuration + duration;
////         batchNumber--;
////      }
////      log.info("Finished : PURGE_VIA_TIME ; duration (ms) :{} ; (mins) : {} ; min:{} ; max:{} : TPS:{}", totalDuration, totalDuration/1000/60, minDuration, maxDuration, (double)totalNodeInstanceToInsert/(totalDuration/1000));
//   }
//
//   // *********** Generate Data Set
//   private List<NodeInstanceEntity> generateDataSet(int nodeInstanceIdStartIndex, int nodeInstanceIdEndIndex, int attributesCount)
//   {
//      List<NodeInstanceEntity> dataSet = Lists.newArrayList();
//      for (int i = nodeInstanceIdStartIndex; i <= nodeInstanceIdEndIndex; i++)
//      {
//         dataSet.add(buildModel(i, attributesCount));
//      }
//      return dataSet;
//   }
//
//   private NodeInstanceEntity buildModel(int nodeInstanceId, int attributesCount)
//   {
//      Date startDate = new Date();
//      List<AttributeEntity> attributes = buildAttributeList(startDate, attributesCount);
//      NodeInstanceEntity nodeInstanceEntity = buildNodeInstanceEntity(nodeInstanceId, startDate, attributes);
//      return nodeInstanceEntity;
//   }
//
//   private List<AttributeEntity> buildAttributeList(Date startDate, int attributesCount){
//      List<AttributeEntity> attributeEntities = Lists.newArrayList();
//      for (int i = 1; i <= attributesCount; i++)
//      {
//         attributeEntities.add(buildAttributeEntity(i, startDate));
//      }
//      return attributeEntities;
//   }
//
//   private NodeInstanceEntity buildNodeInstanceEntity(int nodeInstanceId,  Date startDate, List<AttributeEntity> attributeEntities) {
//      String nodeInstanceName = ConstantsUtility.getNodeInstanceName(nodeInstanceId);
//      NodeInstanceEntity nodeInstanceEntity = NodeInstanceEntity.builder()
//                                                                     .id(String.valueOf(nodeInstanceId))
//                                                                     .name(nodeInstanceName)
//                                                                     .nodeType("nodeType")
//                                                                     .startDate(startDate)
//                                                                     .updateDate(ConstantsUtility.NO_END_DATE)
//                                                                     .attributes(attributeEntities)
//                                                                     .allAttributes(getAllAttributes(attributeEntities))
//                                                                     .metaData(getMetadata(nodeInstanceName))
//                                                                     .status("ACTIVE")
//                                                                     .build();
//      return nodeInstanceEntity;
//   }
//
//   private Map<String, String> getMetadata(String nodeInstanceName){
//      Map<String, String> metadata = Maps.newHashMap();
//      metadata.put("NodeInstanceMapping" + "-PM", nodeInstanceName + "-PM");
//      metadata.put("NodeInstanceMapping" + "-FM", nodeInstanceName + "-FM");
//      metadata.put("NodeInstanceMapping" + "-TT", nodeInstanceName + "-TT");
//      metadata.put("NodeInstanceMapping" + "-EA", nodeInstanceName + "-EA");
//      return metadata;
//   }
//
//   private String getAllAttributes(List<AttributeEntity> attributeEntities) {
//      StringBuilder sb = new StringBuilder();
//      attributeEntities.forEach(item -> sb.append(getAttributeNameValue(item)));
//      return sb.toString();
//   }
//
//   private String getAttributeNameValue(AttributeEntity attributeEntity) {
//      StringBuilder sb = new StringBuilder();
//      sb.append(attributeEntity.getName()).append("\u00B6").append(attributeEntity.getValue()).append("\u00A7");
//      return sb.toString();
//   }
//
//   private AttributeEntity buildAttributeEntity(int attributeNumber, Date startDate) {
//      return AttributeEntity.builder()
//                                 .name(ConstantsUtility.getAttributeName(attributeNumber))
//                                 .value(ConstantsUtility.getAttributeValue(attributeNumber))
//                                 .dataType("String")
//                                 .startDate(startDate)
//                                 .endDate(ConstantsUtility.NO_END_DATE)
//                                 .build();
//   }

}
