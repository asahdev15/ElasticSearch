package services;

import domain.NodeInstanceEntity;
import domain.NodeInstanceEntityWithAttributeHistory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import rest.AttributeHistoryResponse;
import rest.AttributeInfoDto;
import rest.DatasetInfoDto;

import java.util.List;

@Service
@AllArgsConstructor
public class AttributeHistoryEntryPoint
{

   private NodeInstanceService nodeInstanceService;
   private AttributesHistoryService attributesHistoryService;
   private NodeInstanceAttributeHistoryService nodeInstanceAttributeHistoryService;


   public void addNodeInstancesWithAttributes(DatasetInfoDto dto) throws Exception
   {
      nodeInstanceAttributeHistoryService.store(dto);
   }

   public void updateNodeInstanceWithAttributes(AttributeInfoDto attributeInfoDto) throws Exception
   {
      nodeInstanceAttributeHistoryService.updateAttributeWithHistoryNestedApproach(attributeInfoDto);
   }

   public void callUpdateNodeInstanceViaPainlessScript(AttributeInfoDto attributeInfoDto) throws Exception
   {
      nodeInstanceAttributeHistoryService.callUpdateNodeInstanceViaPainlessScript(attributeInfoDto);
   }

   public AttributeHistoryResponse getNodeInstanceWithAttributeHistory(
      String nodeInstanceId,
      String attributeName,
      String attributeValue,
      Long fromTime,
      Long toTime,
      int limit,
      int offset)
   {
      return nodeInstanceAttributeHistoryService.getNodeInstanceWithAttributeHistory(nodeInstanceId, attributeName, attributeValue, fromTime, toTime, limit, offset);
   }

   public List<NodeInstanceEntityWithAttributeHistory> getActiveNodeInstances(Integer limit)
   {
      return nodeInstanceAttributeHistoryService.searchNodeInstancesWithActiveAttributesByName(limit);
   }

   public void terminatenodeinstancesattributeHistory(Integer batchCount) throws Exception
   {
      nodeInstanceAttributeHistoryService.invalidateAllNodeInstances(batchCount);
   }

   public void invalidateAllNodeInstancesViaPainLessScript(Integer batchCount) throws Exception
   {
      nodeInstanceAttributeHistoryService.invalidateAllNodeInstancesViaPainLessScript(batchCount);
   }

   public void purgeByIdnodeinstancesattributeHistory(Integer batchCount) throws Exception
   {
      nodeInstanceAttributeHistoryService.purgeNodeInstancesByIds(batchCount);
   }

   public void purgeByTimenodeinstancesattributeHistory(Integer batchCount, Long endTime) throws Exception
   {
      nodeInstanceAttributeHistoryService.purgeNodeInstancesByTime(batchCount, endTime);
   }


   public void addNodeInstances(DatasetInfoDto dto)
   {
      nodeInstanceService.store(dto);
   }

   public void invalidateAllNodeInstances(int batchCount)
   {
      nodeInstanceService.invalidateAllNodeInstances(batchCount);
   }

   public void purgeNodeInstancesByIds(int batchCount)
   {
      nodeInstanceService.purgeNodeInstancesByIds(batchCount);
   }

   public void purgeNodeInstancesByTerminateStatusAndEndDate(int batchCount, long endTime)
   {
      nodeInstanceService.purgeNodeInstancesByTerminateStatusAndEndDate(batchCount, endTime);
   }

   public void updateAttributes(AttributeInfoDto dto)
   {
//      attributeService.updateAttributeWithHistoryNestedApproach(dto);
      attributesHistoryService.updateAttributeWithHistoryNestedApproach(dto);
   }

   public AttributeHistoryResponse getAttributeHistory(
      String nodeInstanceId,
      String attributeName,
      String attributeValue,
      Long fromTime,
      Long toTime,
      int limit,
      int offset){
      return attributesHistoryService.findAttributeHistory(nodeInstanceId, attributeName, attributeValue, fromTime, toTime, limit, offset);
   }

   public Page<NodeInstanceEntity> findByTime(long time, int limit, int offset)
   {
      return nodeInstanceService.findByTime(time, limit, offset);
   }

//   public Page<NodeInstanceEntity> findByAttributes(Map<String, String> attributes, int limit, int offset)
//   {
//      return attributeService.findByAttributes(attributes, limit, offset);
//   }
//
//   public Page<NodeInstanceEntity> findByAttributeAndTime(String attributeName, long time, int limit, int offset)
//   {
//      return attributeService.findByAttributeAndTime(attributeName, time, limit, offset);
//   }
//
//   public Page<NodeInstanceEntity> findByNodeInstanceIdAndAttributeNameAndTime(String nodeInstanceId, String attributeName, long time, int limit, int offset)
//   {
//      return attributeService.findByNodeInstanceIdAndAttributeNameAndTime(nodeInstanceId, attributeName, time, limit, offset);
//   }


}
