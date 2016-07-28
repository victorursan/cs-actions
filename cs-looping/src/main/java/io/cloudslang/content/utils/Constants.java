package io.cloudslang.content.utils;

/**
 * Created by ursan on 7/28/2016.
 */
public class Constants {

    public static final String DEFAULT_DELIMITER = ",";

    public static final class OutputNames {
        public static final String RETURN_RESULT = "returnResult";
        public static final String RETURN_CODE = "returnCode";
        public static final String RESPONSE = "response";
    }

    public static final class ResponseNames {
        public static final String HAS_MORE = "has more";
        public static final String NO_MORE = "no more";
        public static final String FAILURE = "failure";
    }

    public static final class ReturnCodes {
        public static final String RETURN_CODE_FAILURE = "-1";
        public static final String RETURN_CODE_SUCCESS = "0";
    }

    public static final class InputNames {
        public static final String LIST = "list";
        public static final String DELIMITER = "delimiter";
        public static final String SESSION_ITERATOR = "sessionIterator";
    }

}