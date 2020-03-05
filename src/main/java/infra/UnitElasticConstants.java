package infra;

import java.util.Date;

public class UnitElasticConstants
{
   private UnitElasticConstants()
   {}

   public static final int MAX_FETCH_SIZE = 1000;
   public static final int DEFAULT_BATCH_SIZE = 1000;
   public static final String TO = "to.";
   public static final String FROM = "from.";
   public static final String NODE_INSTACE_NAME = "nodeInstaceName.name";
   public static final String DOT = ".";
   public static final String ES_FIELD_EXTERNAL_MAJOR_VERSION = "externalVersion.majorVersion";
   public static final String ES_FIELD_EXTERNAL_MINOR_VERSION = "externalVersion.minorVersion";
   public static final String ES_FIELD_EXTERNAL_FIX_VERSION = "externalVersion.fixVersion";
   public static final String ES_FIELD_EXTERNAL_QUALIFIER = "externalVersion.qualifier";
   public static final String ES_FIELD_EXTERNAL_BUILD_VERSION = "externalVersion.buildVersion";
   public static final String ES_FIELD_INTERNAL_VERSION = "internalVersion.version";
   public static final String ES_FIELD_VERSION = "version";
   public static final String ES_FIELD_NAME = "name";
   public static final String ES_FIELD_DATASOURCE = "datasource";
   public static final String ES_FIELD_SUBMITTER_ID = "submitterid";
   public static final String ES_FIELD_NODE_TYPE = "nodeType";
   public static final String ES_FIELD_NODE_TYPE_NAME = "nodeType.name";
   public static final String ES_FIELD_RELATION_TYPE_NAME = "relationType.name";
   public static final String ES_FIELD_VENDOR_NAME = "vendorName";
   public static final String ES_FIELD_ATTRIBUTES = "attributes";
   public static final String ES_FIELD_ATTRIBUTE_NAME = "attributes.name";
   public static final String ES_FIELD_ATTRIBUTE_VALUE = "attributes.value";
   public static final String ES_FIELD_ATTRIBUTE_START_TIME = "attributes.startDate";
   public static final String ES_FIELD_ATTRIBUTE_UPDATE_TIME = "attributes.updateDate";
   public static final String ES_FIELD_STATUS = "status";
   public static final String ES_FIELD_MODEL_REFERENCE_NODE_TYPE = "nodeType.name";
   public static final String ES_FIELD_FROM = "from.name";
   public static final String ES_FIELD_TO = "to.name";
   public static final String ES_FIELD_TOPOLOGY_NAME = "topologyName";
   public static final String ES_FIELD_TOPOLOGY_TEMPLATE_VERSION = "topologyTemplateVersion";
   public static final String ES_FIELD_ID = "_id";
   public static final String ES_FIELD_METADATA_VALUES = "metadata.values";
   public static final String ES_FIELD_METADATA_VALUES_WITH_DOT = ES_FIELD_METADATA_VALUES + DOT;
   public static final String ES_FIELD_NODETEMPLATES_NODETYPE_NAME = "nodeTemplates.nodeType.name";
   public static final String ES_FIELD_NODETEMPLATES = "nodeTemplates";
   public static final String ES_FIELD_METADATA = "metaData";
   public static final String ES_FIELD_MAPPING_BEFORE = "mappings.before";
   public static final String ES_FIELD_MAPPING_AFTER = "mappings.after";
   public static final String ES_FIELD_NODE_TEMP_INSTANCES_NODE_INS_ID = "nodeTemplateInstances.nodeInstanceId";
   public static final String ES_FIELD_NODE_TEMP_INSTANCES_NODE_INS_NAME = "nodeTemplateInstances.nodeInstanceName";
   public static final String ES_FIELD_FROM_NODE_INS_ID = "from.nodeInstanceId";
   public static final String ES_FIELD_FROM_NODE_INS_NAME = "from.nodeInstaceName.name";
   public static final String ES_FIELD_DERIVED_FROM = "derivedFrom.name";
   public static final String ES_FIELD_TIME_STAMP = "timestamp";
   public static final String ES_FIELD_TO_NODE_INS_ID = "to.nodeInstanceId";
   public static final String ES_FIELD_TO_NODE_INS_NAME = "to.nodeInstaceName.name";
   public static final String ES_FIELD_LAST_UPDATE_TIME = "lastUpdateTime";
   public static final String ES_FIELD_NAME_SUBSCRIBER_ID = "subscriberId";
   public static final String ES_FIELD_FROM_NODE_TYPE_NAME = "from.nodeType.name";
   public static final String ES_FIELD_TO_NODE_TYPE_NAME = "to.nodeType.name";
   public static final String ES_FIELD_OBJECT_TYPE = "objectType";
   public static final String ES_FIELD_REFERENCE_ID = "referenceId";
   public static final String ES_FIELD_MAPPING_CONTEXTS = "mappingContexts";
   public static final String ES_FIELD_MAPPING_CONTEXTS_DATASOURCE = "mappingContexts.datasource";
   public static final String ES_FIELD_MAPPING_CONTEXTS_FIELD_NAME = "mappingContexts.fieldName";
   public static final String ES_FIELD_MAPPING_CONTEXTS_FIELD_VALUE = "mappingContexts.fieldValue";
   public static final String ES_FIELD_MAPPING_CONTEXTS_END_DATE = "mappingContexts.endDate";
   public static final String ES_FIELD_RELATIONSHIPTEMPLATES_FROM_NAME = "relationshipTemplates.from.name";
   public static final String ES_FIELD_RELATIONSHIPTEMPLATES_TO_NAME = "relationshipTemplates.to.name";
   public static final String ES_FIELD_RELATIONSHIPTEMPLATES_NAME = "relationshipTemplates.name";
   public static final String ES_FIELD_END_DATE_NAME = "endDate";
   public static final String ES_FIELD_END_TIME_NAME = "endTime";
   public static final String ES_FIELD_CREATION_TIME_NAME = "creationTime";
   public static final String ES_FIELD_START_DATE_NAME = "startDate";
   public static final String ES_FIELD_UPDATE_DATE_NAME = "updateDate";

   public static final String DEFAULT_LIMIT = "10";
   public static final String DEFAULT_OFFSET = "0";
   public static final long NO_END_DATE_VALUE = 95617584000000L;
   public static final Date NO_END_DATE = new Date(NO_END_DATE_VALUE);
   public static final String nodeInstanceNamePrefix = "NodeInstancePOC_";
   public static final String attributeNamePrefix = "AttributePOC_";
   public static final String attributeValuePrefix = "AttributePOCValue_";

   public static final String getAttributeName(int attributeNumber) {
      return attributeNamePrefix + attributeNumber;
   }

   public static final String getAttributeValue(int attributeNumber) {
      return attributeValuePrefix + attributeNumber;
   }

   public static final String getNodeInstanceName(int nodeInstanceIndex) {
      return nodeInstanceNamePrefix + nodeInstanceIndex;
   }



}
