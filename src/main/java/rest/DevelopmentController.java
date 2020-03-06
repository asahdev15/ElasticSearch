package rest;

import infra.ConstantsUtility;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@AllArgsConstructor
public class DevelopmentController
{

//   @Autowired
//   private AttributeHistoryEntryPoint entryPoint;
//
//// insert
//   @RequestMapping(value = "/nodeinstancesattributeHistory/poc/insert", consumes = { "application/json" }, method = RequestMethod.POST)
//   public void addNodeInstancesWithAttributes(@RequestBody DatasetInfoDto dto) throws Exception
//   {
//      entryPoint.addNodeInstancesWithAttributes(dto);
//   }
//
//   // update
//   @RequestMapping(value = "/nodeinstancesattributeHistory/poc/update", consumes = { "application/json" }, method = RequestMethod.POST)
//   public void updateNodeInstanceWithAttributes(@RequestBody AttributeInfoDto dto) throws Exception
//   {
//      entryPoint.updateNodeInstanceWithAttributes(dto);
//   }
//
//// update
//   @RequestMapping(value = "/nodeinstancesattributeHistoryPainLess/poc/update", consumes = { "application/json" }, method = RequestMethod.POST)
//   public void callUpdateNodeInstanceViaPainlessScript(@RequestBody AttributeInfoDto dto) throws Exception
//   {
//      entryPoint.callUpdateNodeInstanceViaPainlessScript(dto);
//   }
//
//   // find
//   @RequestMapping(value = "/nodeinstancesattributeHistory/search", produces = { "application/json" }, method = RequestMethod.GET)
//   public ResponseEntity<AttributeHistoryResponse> getNodeInstanceWithAttributeHistory(
//      @RequestParam (required = false) String nodeInstanceId,
//      @RequestParam (required = true) String attributeName,
//      @RequestParam (required = false) String attributeValue,
//      @RequestParam (required = false) String fromTime,
//      @RequestParam (required = false) String toTime,
//      @RequestParam (required = false, defaultValue = ConstantsUtility.DEFAULT_LIMIT) String limit,
//      @RequestParam (required = false, defaultValue = ConstantsUtility.DEFAULT_OFFSET) String offset)
//   {
//      if(fromTime == null || fromTime.isEmpty()) {
//         fromTime = String.valueOf(0);
//      }
//      if(toTime == null || toTime.isEmpty()) {
//         toTime = String.valueOf(ConstantsUtility.NO_END_DATE_VALUE);
//      }
//      AttributeHistoryResponse result = entryPoint.getNodeInstanceWithAttributeHistory(nodeInstanceId, attributeName, attributeValue, Long.valueOf(fromTime), Long.valueOf(toTime), Integer.valueOf(limit), Integer.valueOf(offset));
//      return new ResponseEntity<AttributeHistoryResponse>(result, HttpStatus.OK);
//   }
//
//   @RequestMapping(value = "/nodeinstancesActive/search", produces = { "application/json" }, method = RequestMethod.GET)
//   public ResponseEntity<List<NodeInstanceEntityWithAttributeHistory>> getActiveNodeInstances(@RequestParam (required = true) String count)
//   {
//      return new ResponseEntity<List<NodeInstanceEntityWithAttributeHistory>>(entryPoint.getActiveNodeInstances(Integer.valueOf(count)), HttpStatus.OK);
//   }
//
//   // invalidate
//   @RequestMapping(value = "/nodeinstancesattributeHistory/poc/terminate", method = RequestMethod.DELETE)
//   public void terminatenodeinstancesattributeHistory(@RequestParam(required = true) String batchCount) throws Exception
//   {
//      entryPoint.terminatenodeinstancesattributeHistory(Integer.valueOf(batchCount));
//   }
//
//   @RequestMapping(value = "/nodeinstancesattributeHistoryPainLess/poc/terminate", method = RequestMethod.DELETE)
//   public void invalidateAllNodeInstancesViaPainLessScript(@RequestParam(required = true) String batchCount) throws Exception
//   {
//      entryPoint.invalidateAllNodeInstancesViaPainLessScript(Integer.valueOf(batchCount));
//   }
//
//   // purge
//   @RequestMapping(value = "/nodeinstancesattributeHistory/poc/purgeByIds", method = RequestMethod.DELETE)
//   public void purgeByIdnodeinstancesattributeHistory(@RequestParam(required = true) String batchCount) throws Exception
//   {
//      entryPoint.purgeByIdnodeinstancesattributeHistory(Integer.valueOf(batchCount));
//   }
//
//   @RequestMapping(value = "/nodeinstancesattributeHistory/poc/purgeByTime", method = RequestMethod.DELETE)
//   public void purgeByTimenodeinstancesattributeHistory(@RequestParam(required = true) String batchCount, @RequestParam(required = false) String endTime) throws Exception
//   {
//      if(endTime == null || endTime.isEmpty()) {
//         endTime = String.valueOf(ConstantsUtility.NO_END_DATE_VALUE);
//      }
//      entryPoint.purgeByTimenodeinstancesattributeHistory(Integer.valueOf(batchCount), Long.valueOf(endTime));
//   }
//
//
//   // insert
//   @RequestMapping(value = "/nodeinstances/poc/insert", consumes = { "application/json" }, method = RequestMethod.POST)
//   public void addNodeInstances(@RequestBody DatasetInfoDto dto) throws Exception
//   {
//      entryPoint.addNodeInstances(dto);
//   }
//
//   // update
//   @RequestMapping(value = "/attributes/poc/update", consumes = { "application/json" }, method = RequestMethod.POST)
//   public void updateAttributes(@RequestBody AttributeInfoDto dto) throws Exception
//   {
//      entryPoint.updateAttributes(dto);
//   }
//
//   // find
//   @RequestMapping(value = "/attributes/search", produces = { "application/json" }, method = RequestMethod.GET)
//   public ResponseEntity<AttributeHistoryResponse> getAttributeHistory(
//      @RequestParam (required = false) String nodeInstanceId,
//      @RequestParam (required = true) String attributeName,
//      @RequestParam (required = false) String attributeValue,
//      @RequestParam (required = false) String fromTime,
//      @RequestParam (required = false) String toTime,
//      @RequestParam (required = false, defaultValue = ConstantsUtility.DEFAULT_LIMIT) String limit,
//      @RequestParam (required = false, defaultValue = ConstantsUtility.DEFAULT_OFFSET) String offset)
//   {
//      if(fromTime == null || fromTime.isEmpty()) {
//         fromTime = String.valueOf(0);
//      }
//      if(toTime == null || toTime.isEmpty()) {
//         toTime = String.valueOf(ConstantsUtility.NO_END_DATE_VALUE);
//      }
//      AttributeHistoryResponse result = entryPoint.getAttributeHistory(nodeInstanceId, attributeName, attributeValue, getLongValue(fromTime), getLongValue(toTime), getIntegerValue(limit), getIntegerValue(offset));
//      return new ResponseEntity<AttributeHistoryResponse>(result, HttpStatus.OK);
//   }
//
//   // invalidate
//   @RequestMapping(value = "/nodeinstances/poc/invalidateAll", method = RequestMethod.DELETE)
//   public void invalidateAllNodeInstances(@RequestParam(required = true) String batchCount) throws Exception
//   {
//      entryPoint.invalidateAllNodeInstances(Integer.valueOf(batchCount));
//   }
//
//   // purge
//   @RequestMapping(value = "/nodeinstances/poc/purgeByIds", method = RequestMethod.DELETE)
//   public void purgeNodeInstancesByIds(@RequestParam(required = true) String batchCount) throws Exception
//   {
//      entryPoint.purgeNodeInstancesByIds(Integer.valueOf(batchCount));
//   }
//
//   @RequestMapping(value = "/nodeinstances/poc/purgeByTime", method = RequestMethod.DELETE)
//   public void purgeNodeInstancesByTerminateStatusAndEndDate(@RequestParam(required = true) String batchCount, @RequestParam(required = false) String endTime) throws Exception
//   {
//      if(endTime == null || endTime.isEmpty()) {
//         endTime = String.valueOf(ConstantsUtility.NO_END_DATE_VALUE);
//      }
//      entryPoint.purgeNodeInstancesByTerminateStatusAndEndDate(Integer.valueOf(batchCount), Long.valueOf(endTime));
//   }
//
//   // Other
////   @RequestMapping(value = "/attributes/poc/insert", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
////   public void addAttributes(@Valid @RequestBody AttributeInfoDto dto) throws Exception
////   {
////      entryPoint.addAttributes(dto);
////   }
//
//   @RequestMapping(value = "/nodeinstances/poc/search", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
//   public ResponseEntity<Page<NodeInstanceEntity>> getNodeInstancesByTime(
//      @RequestParam String time,
//      @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_LIMIT) String limit,
//      @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_OFFSET) String offset)
//   {
//      Page<NodeInstanceEntity> result = entryPoint.findByTime(Long.valueOf(time), Integer.valueOf(limit), Integer.valueOf(offset));
//      return new ResponseEntity<Page<NodeInstanceEntity>>(result, HttpStatus.OK);
//   }
//
////   @RequestMapping(value = "/attributes/poc/search", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
////   public ResponseEntity<Page<NodeInstanceEntity>> getNodeInstancesByAttributes(
////      @RequestBody AttributeQueryDto dto,
////      @ApiParam(value = "limit", required = false) @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_LIMIT) String limit,
////      @ApiParam(value = "offset", required = false) @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_OFFSET) String offset)
////   {
////      Page<NodeInstanceEntity> result = entryPoint.findByAttributes(dto.getAttributes(), Integer.valueOf(limit), Integer.valueOf(offset));
////      return new ResponseEntity<Page<NodeInstanceEntity>>(result, HttpStatus.OK);
////   }
//
////   @RequestMapping(value = "/attributes/poc/search/", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.GET)
////   public ResponseEntity<Page<NodeInstanceEntity>> getNodeInstancesByAttributeAndTime(
////      @RequestParam String nodeInstanceId,
////      @RequestParam String attribute,
////      @RequestParam String time,
////      @ApiParam(value = "limit", required = false) @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_LIMIT) String limit,
////      @ApiParam(value = "offset", required = false) @RequestParam(required = false, defaultValue = ConstantsUtility.DEFAULT_OFFSET) String offset)
////   {
////      if(nodeInstanceId !=null && !nodeInstanceId.isEmpty()) {
////         Page<NodeInstanceEntity> result = entryPoint.findByNodeInstanceIdAndAttributeNameAndTime(nodeInstanceId, attribute, Long.valueOf(time), Integer.valueOf(limit), Integer.valueOf(offset));
////         return new ResponseEntity<Page<NodeInstanceEntity>>(result, HttpStatus.OK);
////      }
////      Page<NodeInstanceEntity> result = entryPoint.findByAttributeAndTime(attribute, Long.valueOf(time), Integer.valueOf(limit), Integer.valueOf(offset));
////      return new ResponseEntity<Page<NodeInstanceEntity>>(result, HttpStatus.OK);
////   }
//
//   private Long getLongValue(String value) {
//      Long result = null;
//      if(value!=null && !value.isEmpty()) {
//         result = Long.valueOf(value);
//      }
//      return result;
//   }
//
//   private Integer getIntegerValue(String value) {
//      Integer result = null;
//      if(value!=null && !value.isEmpty()) {
//         result = Integer.valueOf(value);
//      }
//      return result;
//   }


}
