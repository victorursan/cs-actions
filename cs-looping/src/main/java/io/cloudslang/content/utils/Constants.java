package io.cloudslang.content.utils;

/**
 * Created by ursan on 7/28/2016.
 */
public class Constants {

    public static final String DEFAULT_DELIMITER = ",";
    public static final String EMPTY_STRING = "";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final class OutputNames {
        public static final String RETURN_RESULT = "returnResult";
        public static final String RETURN_CODE = "returnCode";
        public static final String RESPONSE_TEXT = "response";
        public static final String EXCEPTION = "exception";
        public static final String RESPONSE = "response";

        public static final String RESULT_TEXT = "result";
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
}