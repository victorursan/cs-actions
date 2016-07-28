package io.cloudslang.content.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by ursan on 7/28/2016.
 */
public class InputsUtils {
    public static String getInputDefaultValue(String input, String defaultValue) {
        return StringUtils.isEmpty(input) ? defaultValue : input;
    }
}