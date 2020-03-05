package services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractOperation
{


   public void startOperation(String operationName, int nodeInstanceBatchSizeInput, int nodeInstanceCountInput, int attributesCountInput, Object inputObject)
   {
      log.info("Start : "+ operationName + " ; nodeInstanceBatchSize:{} ; nodeInstanceCount:{}; attributesCount:{}", nodeInstanceBatchSizeInput, nodeInstanceCountInput, attributesCountInput);
      long totalDuration = 0L;
      long minDuration = Long.MAX_VALUE;
      long maxDuration = Long.MIN_VALUE;
      int nodeInstanceBatchSize = nodeInstanceBatchSizeInput;
      int attributesCount = attributesCountInput;
      int totalNodeInstanceToInsert = nodeInstanceCountInput;
      int batchSize = totalNodeInstanceToInsert / nodeInstanceBatchSize;
      int nodeInstanceIdStartIndex = 1;
      int nodeInstanceIdEndIndex = nodeInstanceBatchSize;
      int batchNumber = batchSize ;
      while(batchNumber > 0) {
         long duration = invokeOperation(operationName, nodeInstanceIdStartIndex, nodeInstanceIdEndIndex, attributesCount, batchNumber, inputObject);
         minDuration = Math.min(minDuration, duration);
         maxDuration = Math.max(maxDuration, duration);
         totalDuration = totalDuration + duration;
         nodeInstanceIdStartIndex = nodeInstanceIdStartIndex + nodeInstanceBatchSize;
         nodeInstanceIdEndIndex = nodeInstanceIdEndIndex + nodeInstanceBatchSize;
         batchNumber--;
      }
      log.info("Finished : "+ operationName +"  ; duration (ms) :{} ; (mins) : {} ; min:{} ; max:{} : TPS:{}", totalDuration, totalDuration/1000/60, minDuration, maxDuration, (double)totalNodeInstanceToInsert/(totalDuration/1000));
   }

   public abstract long invokeOperation(String operationName, int nodeInstanceIdStartIndex,int nodeInstanceIdEndIndex, int attributesCount, int batchNumber, Object inputObject);


}
