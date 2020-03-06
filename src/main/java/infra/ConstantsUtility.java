package infra;

import java.util.Date;

public class ConstantsUtility
{
   private ConstantsUtility()
   {}

   public static final int MAX_FETCH_SIZE = 1000;
   public static final int DEFAULT_BATCH_SIZE = 1000;
   public static final String ES_FIELD_NAME = "name";
   public static final String ES_FIELD_ATTRIBUTES = "attributes";
   public static final String ES_FIELD_ATTRIBUTE_NAME = "attributes.name";
   public static final String ES_FIELD_ATTRIBUTE_VALUE = "attributes.value";
   public static final String ES_FIELD_ID = "_id";
   public static final String ES_FIELD_START_DATE_NAME = "startDate";
   public static final String ES_FIELD_UPDATE_DATE_NAME = "updateDate";
   public static final String DEFAULT_LIMIT = "10";
   public static final String DEFAULT_OFFSET = "0";
   public static final long NO_END_DATE_VALUE = 95617584000000L;
   public static final Date NO_END_DATE = new Date(NO_END_DATE_VALUE);

}
