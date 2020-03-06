package services;

//@Slf4j
//@AllArgsConstructor
//@Service
public class AttributesHistoryService
{
//   private CustomerRepository<NodeInstanceEntity> nodeInstanceRepo;
//   private AttributeHistoryRepository<AttributeHistoryEntity> attributeHistoryRepo;
//   private static final String UPDATE = "UPDATE";
//   private static final String UPDATE_VIA_PAINLESS = "UPDATE_VIA_PAINLESS";
//
//   @Override
//   public long invokeOperation(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject) {
//      long duration = 0L;
//      if(operationName.equals(UPDATE)) {
//         duration = update(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, (AttributeInfoDto)inputObject);
//      } /*
//         * else if(operationName.equals(UPDATE_VIA_PAINLESS)) { duration = updateViaPainless(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex,
//         * attributesCount, batchNumber, inputObject); }
//         */
//      log.info("Executing : " + operationName + " ; totalDuration:{}; batchNumber:{}", duration, batchNumber);
//      return duration;
//   }
//
//   private long update(String operationName, int nodeInstanceIdStartIndex, int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, AttributeInfoDto inputObject)
//   {
//      return updateAttHistory(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, inputObject);
//   }
//
//   public AttributeHistoryResponse findAttributeHistory(String nodeInstanceId, String attributeName, String attributeValue, Long fromTime, Long toTime, int limit, int offsetInput) {
//      Map<String, Map<String, List<AttributeHistoryResult>>> responseMap = Maps.newHashMap();
//      AttributeHistoryResponse respone = new AttributeHistoryResponse(responseMap);
//      long totalDuration = 0L;
//      long minDuration = Long.MAX_VALUE;
//      long maxDuration = Long.MIN_VALUE;
//      int total = limit;
//      int offset = 0;
//      for(; offset < total ; offset += 1000) {
//         log.info("Executing : getNodeInstanceWithAttributeHistory;  offset:{}; duration;{}", offset);
//         long startTime = System.nanoTime();
//         Page<AttributeHistoryEntity> pageResult = attributeHistoryRepo.findAttributeHistoryEntity(nodeInstanceId, attributeName, attributeValue, fromTime, toTime, 1000, offset);
//         List<AttributeHistoryEntity> pageContent = pageResult.getContent();
//         for(AttributeHistoryEntity attributeHistoryEntity : pageContent) {
//            String nodeInstanceIdR = attributeHistoryEntity.getNodeinstanceId();
//            Map<String, List<AttributeHistoryResult>> attributesMap = responseMap.get(nodeInstanceIdR);
//            if(attributesMap == null) {
//               attributesMap = Maps.newHashMap();
//               responseMap.put(nodeInstanceIdR, attributesMap);
//            }
//            List<AttributeHistoryResult> attributeHistoryList = attributesMap.get(attributeName);
//            if(attributeHistoryList == null) {
//               attributeHistoryList = Lists.newArrayList();
//               attributesMap.put(attributeName, attributeHistoryList);
//            }
//            List<Attribute> attributeHistory = getAttributeHistory(attributeName, attributeHistoryEntity.getAttributes());
//            Stream<Attribute> stream = attributeHistory.stream();
//            if(isValuePresent(attributeValue)) {
//               stream = stream.filter(item -> item.getValue().equals(attributeValue));
//            }
//            if(fromTime != null && toTime != null) {
//               stream = stream.filter(item -> applyTimeCondition(item, fromTime, toTime));
//            }
//            List<Attribute> result = stream.collect(Collectors.toList());
//            for(Attribute attribute : result) {
//               attributeHistoryList.add(new AttributeHistoryResult(attribute.getValue(), attribute.getDataType(), attribute.getStartDate().getTime(), attribute.getEndDate().getTime()));
//            }
//         }
//         long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//         minDuration = Math.min(minDuration, duration);
//         maxDuration = Math.max(maxDuration, duration);
//         totalDuration += duration;
//      }
//      log.info("End : SEARCH ; duration (ms):{} ; (mins):{} ; min:{} ; max:{} : TPS:{} ; totalCount:{}", totalDuration, totalDuration/1000/60, minDuration, maxDuration, (double)total/(totalDuration/1000), respone.getResponse().size());
//      return respone;
//   }
//
//   private boolean applyTimeCondition(Attribute attribute, Long fromTime, Long toTime) {
//      boolean result = false;
//      result = (attribute.getStartDate().getTime() >= fromTime) && (attribute.getStartDate().getTime() <= toTime );
//      result = result || (attribute.getEndDate().getTime() >= fromTime) && (attribute.getEndDate().getTime() <= toTime );
//      result = result || (attribute.getStartDate().getTime() < fromTime) && (attribute.getEndDate().getTime() > toTime );
//      return result;
//   }
//
//   private static boolean isValuePresent(String value) {
//      return value != null && !value.trim().isEmpty();
//   }
//
//   private List<Attribute> getAttributeHistory(String attributeName, List<AttributeHistory> attributes){
//      Optional<AttributeHistory> attributeHistory = attributes.stream().filter(item -> item.getName().equals(attributeName)).findFirst();
//      if(attributeHistory.isPresent()) {
//         return attributeHistory.get().getHistory();
//      }
//      return Lists.newArrayList();
//   }
//
//   public void updateAttributeWithHistoryNestedApproach(AttributeInfoDto attributeInfoDto) {
//      startOperation(UPDATE, attributeInfoDto.getNodeInstanceBatchCount(), attributeInfoDto.getNodeInstanceCount(), attributeInfoDto.getAttributes().size(), attributeInfoDto);
////      log.info("Start : Update of 1 M NodeInstances ; nodeInstanceBatchSize:{} ; attributesCountSize:{}", , );
////      long totalDuration = 0L;
////      long minDuration = Long.MAX_VALUE;
////      long maxDuration = Long.MIN_VALUE;
////      int nodeInstanceBatchSize = attributeInfoDto.getNodeInstanceBatchCount();
////      int totalNodeInstanceToInsert = attributeInfoDto.getNodeInstanceCount();
////      int batchSize = totalNodeInstanceToInsert / nodeInstanceBatchSize;
////      int nodeInstanceIdStartIndex = 1;
////      int nodeInstanceIdEndIndex = nodeInstanceBatchSize;
////      int batchNumber = batchSize ;
////      while(batchNumber > 0) {
////
////         long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
////         log.info("End : Update NodeInstances; count:{} ; duration:{} ; batchCount:{}", nodeInstances.size(), duration, batchNumber);
////         totalDuration = totalDuration + duration;
////         minDuration = Math.min(minDuration, duration);
////         maxDuration = Math.max(maxDuration, duration);
////         nodeInstanceIdStartIndex = nodeInstanceIdStartIndex + nodeInstanceBatchSize;
////         nodeInstanceIdEndIndex = nodeInstanceIdEndIndex + nodeInstanceBatchSize;
////         batchNumber--;
////      }
////      log.info("Finished : Update of 1 M NodeInstances ; totalDuration:{} ; minDuration:{} ; maxDuration:{} ", totalDuration, minDuration, maxDuration);
//   }
//
//   private long updateAttHistory(int nodeInstanceIdStartIndex, int nodeInstanceIdEndIndex, AttributeInfoDto attributeInfoDto) {
//      List<NodeInstanceEntity> nodeInstances =  getNodeInstanceEntities(nodeInstanceIdStartIndex, nodeInstanceIdEndIndex);
//      List<String> nodeInstanceIds = Lists.newArrayList();
//      nodeInstances.forEach(item -> nodeInstanceIds.add(item.getId()));
//      long startTime = System.nanoTime();
//      updateAttributesHistory(nodeInstances, attributeInfoDto.getAttributes(), new Date());
//      long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//      return duration;
//   }
//
//   private void updateAttributesHistory(List<NodeInstanceEntity> nodeInstanceEntities, List<AttributeDto> attributeUpdates, Date updateDate){
//      Map<String, List<AttributeEntity>> nodeInstanceClosedAttributes = Maps.newHashMap();
//      updateNodeInstanceExistingAttributes(nodeInstanceEntities, attributeUpdates, updateDate, nodeInstanceClosedAttributes);
//      updateAttributesHistory(nodeInstanceClosedAttributes);
//   }
//
//   private void updateAttributesHistory(Map<String, List<AttributeEntity>> nodeInstanceClosedAttributesMap) {
//      if(!nodeInstanceClosedAttributesMap.isEmpty()) {
//         List<String> nodeInstanceIds = Lists.newArrayList(nodeInstanceClosedAttributesMap.keySet());
//         List<AttributeHistoryEntity> existingAttributesHistory = attributeHistoryRepo.findByNodeInstanceIds(nodeInstanceIds);
//         Map<String, AttributeHistoryEntity> existingNodeInstanceAttributesHistoryMap = Maps.newHashMap();
//         existingAttributesHistory.forEach(item -> existingNodeInstanceAttributesHistoryMap.put(item.getNodeinstanceId(), item));
//         for(Entry<String, List<AttributeEntity>> entry : nodeInstanceClosedAttributesMap.entrySet()) {
//            String nodeInstanceId = entry.getKey();
//            List<AttributeEntity> nodeInstanceClosedAttributes = entry.getValue();
//            AttributeHistoryEntity nodeInstanceAttributeHistoryEntity = null;
//            if(existingNodeInstanceAttributesHistoryMap.containsKey(nodeInstanceId)) {
//               nodeInstanceAttributeHistoryEntity = existingNodeInstanceAttributesHistoryMap.get(nodeInstanceId);
//            }else{
//               nodeInstanceAttributeHistoryEntity = AttributeHistoryEntity.builder().nodeinstanceId(nodeInstanceId).attributes(Lists.newArrayList()).build();
//               existingAttributesHistory.add(nodeInstanceAttributeHistoryEntity);
//               existingNodeInstanceAttributesHistoryMap.put(nodeInstanceId, nodeInstanceAttributeHistoryEntity);
//            }
//            addClosedAttributes(nodeInstanceAttributeHistoryEntity, nodeInstanceClosedAttributes);
//         }
//         if(!existingAttributesHistory.isEmpty()) {
//            attributeHistoryRepo.store(existingAttributesHistory);
//         }
//      }
//   }
//
//   private void addClosedAttributes(AttributeHistoryEntity attributeHistoryEntity, List<AttributeEntity> closedAttributes) {
//      Map<String, List<AttributeEntity>> closedAttributesMap = buildClosedAttributesMap(closedAttributes);
//      Map<String, List<Attribute>> existingClosedAttributesMap = buildExistingAttributesMap(attributeHistoryEntity);
//      for(Entry<String, List<AttributeEntity>> entry : closedAttributesMap.entrySet()) {
//         String attributeName = entry.getKey();
//         List<Attribute> closedAttributesEntity = Lists.newArrayList();
//         if(existingClosedAttributesMap.containsKey(attributeName)) {
//            closedAttributesEntity = existingClosedAttributesMap.get(attributeName);
//         }else {
//            existingClosedAttributesMap.put(attributeName, closedAttributesEntity);
//            attributeHistoryEntity.getAttributes().add(new AttributeHistory(attributeName, closedAttributesEntity));
//         }
//         for(AttributeEntity item : entry.getValue()) {
//            closedAttributesEntity.add(new Attribute(item.getValue(), item.getDataType(), item.getStartDate(), item.getEndDate()));
//         }
//      }
//   }
//
//   private Map<String, List<Attribute>> buildExistingAttributesMap(AttributeHistoryEntity attributeHistoryEntity){
//      Map<String, List<Attribute>> closedAttributesMap = Maps.newHashMap();
//      List<AttributeHistory> existingClosedAttributes = attributeHistoryEntity.getAttributes();
//      for(AttributeHistory attributeHistory : existingClosedAttributes) {
//         closedAttributesMap.put(attributeHistory.getName(), attributeHistory.getHistory());
//      }
//      return closedAttributesMap;
//   }
//
//   private Map<String, List<AttributeEntity>> buildClosedAttributesMap(List<AttributeEntity> closedAttributes){
//      Map<String, List<AttributeEntity>> closedAttributesMap = Maps.newHashMap();
//      for(AttributeEntity attributeEntity : closedAttributes) {
//         List<AttributeEntity> closedAttributeList = Lists.newArrayList();
//         if(closedAttributesMap.containsKey(attributeEntity.getName())) {
//            closedAttributeList = closedAttributesMap.get(attributeEntity.getName());
//         }
//         closedAttributeList.add(attributeEntity);
//         closedAttributesMap.put(attributeEntity.getName(), closedAttributeList);
//      }
//      return closedAttributesMap;
//   }
//
//   private void updateNodeInstanceExistingAttributes(List<NodeInstanceEntity> nodeInstanceEntities, List<AttributeDto> attributeUpdates, Date date, Map<String, List<AttributeEntity>> nodeInstanceClosedAttributes) {
//      for(NodeInstanceEntity nodeInstanceEntity : nodeInstanceEntities) {
//         Map<String, AttributeEntity> existingAttributeEntities = getExistingAttributeEntities(nodeInstanceEntity);
//         for(AttributeDto attributeDto : attributeUpdates) {
//            if(existingAttributeEntities.containsKey(attributeDto.getName())) {
//               AttributeEntity attributeEntity = existingAttributeEntities.remove(attributeDto.getName());
//               attributeEntity.setEndDate(new Date(date.getTime()-1));
//               updateNodeInstanceClosedAttributes(nodeInstanceEntity.getId(), attributeEntity, nodeInstanceClosedAttributes);
//               if(attributeDto.getType()==null || attributeDto.getType().isEmpty()) {
//                  attributeDto.setType(attributeEntity.getDataType());
//               }
//            }
//            AttributeEntity attributeEntityNew = buildAttributeEntity(attributeDto, date);
//            existingAttributeEntities.put(attributeDto.getName(), attributeEntityNew);
//         }
//         List<AttributeEntity> updatedAttributeList = Lists.newArrayList(existingAttributeEntities.values());
//         nodeInstanceEntity.setAttributes(updatedAttributeList);
//         nodeInstanceEntity.setUpdateDate(date);
//      }
//      nodeInstanceRepo.store(nodeInstanceEntities);
//   }
//
//   private void updateNodeInstanceClosedAttributes(String nodeInstanceId, AttributeEntity attributeEntity, Map<String, List<AttributeEntity>> nodeInstanceClosedAttributes) {
//      List<AttributeEntity> closedAttributeList = Lists.newArrayList();
//      if(nodeInstanceClosedAttributes.containsKey(nodeInstanceId)) {
//         closedAttributeList = nodeInstanceClosedAttributes.get(nodeInstanceId);
//      }
//      closedAttributeList.add(attributeEntity);
//      nodeInstanceClosedAttributes.put(nodeInstanceId, closedAttributeList);
//   }
//
//   private Map<String, AttributeEntity> getExistingAttributeEntities(NodeInstanceEntity nodeInstanceEntity){
//      Map<String, AttributeEntity> existingAttributeEntities = Maps.newHashMap();
//      List<AttributeEntity> existingAttributes = nodeInstanceEntity.getAttributes();
//      for(AttributeEntity item : existingAttributes) {
//         existingAttributeEntities.put(item.getName(), item);
//      }
//      return existingAttributeEntities;
//   }
//
//   // TODO check need for this method
//   private boolean isAttributeToUpdatePresent(AttributeEntity attributeEntity, AttributeDto attributeToUpdate) {
//      return attributeEntity.getName().equals(attributeToUpdate.getName())
//         && attributeEntity.getDataType().equals(attributeToUpdate.getType())
//         && attributeEntity.getEndDate().getTime()== ConstantsUtility.NO_END_DATE_VALUE;
//   }
//
//   private List<AttributeEntity> toAttributeEntity(List<AttributeDto> attributesDtos, Date startDate)
//   {
//      return attributesDtos.stream()
//         .map(attributeDto -> buildAttributeEntity(attributeDto, startDate))
//         .collect(Collectors.toList());
//   }
//
//   private AttributeEntity buildAttributeEntity(AttributeDto attributeDto, Date startDate) {
//      return AttributeEntity.builder()
//         .name(attributeDto.getName())
//         .dataType(attributeDto.getType())
//         .value(attributeDto.getValue())
//         .startDate(startDate)
//         .endDate(ConstantsUtility.NO_END_DATE)
//         .build();
//   }
//
//   private List<NodeInstanceEntity> getNodeInstanceEntities(int startIndex, int endIndex){
//      List<String> nodeInstanceNames = Lists.newArrayList();
//      for(int i = startIndex; i <= endIndex ; i++) {
//         nodeInstanceNames.add(ConstantsUtility.getNodeInstanceName(i));
//      }
//      List<NodeInstanceEntity> nodeInstances = getNodeInstances(nodeInstanceNames);
//      return nodeInstances;
//   }
//
//   private List<NodeInstanceEntity> getNodeInstances(List<String> nodeInstanceNames){
//      return nodeInstanceRepo.findByNames(nodeInstanceNames.toArray(new String[0]));
//   }

}
