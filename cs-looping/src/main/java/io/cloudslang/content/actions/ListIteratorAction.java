package io.cloudslang.content.actions;

import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.GlobalSessionObject;
import io.cloudslang.content.utils.Constants;
import io.cloudslang.content.utils.InputsUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.cloudslang.content.utils.Constants.OutputNames.*;
import static io.cloudslang.content.utils.Constants.ResponseNames.*;
import static io.cloudslang.content.utils.Constants.InputNames.*;
import static io.cloudslang.content.utils.Constants.ReturnCodes.*;

/**
 * Created by ursan on 7/28/2016.
 */
public class ListIteratorAction {

    @Action(name = "Iterator",
            outputs = {
                    @Output(RESPONSE),
                    @Output(RETURN_RESULT),
                    @Output(RETURN_CODE)},
            responses = {
                    @Response(text = HAS_MORE, field = RETURN_RESULT, value = RETURN_CODE_SUCCESS, matchType = MatchType.COMPARE_EQUAL),
                    @Response(text = NO_MORE, field = RETURN_RESULT, value = RETURN_CODE_SUCCESS, matchType = MatchType.COMPARE_EQUAL),
                    @Response(text = FAILURE, field = RETURN_RESULT, value = RETURN_CODE_FAILURE, matchType = MatchType.COMPARE_EQUAL, isDefault = true, isOnFail = true)})
    public Map<String, String> listIterator(@Param(value = LIST,  required = true) String list,
                                            @Param(value = DELIMITER, required = true) String delimiter,
                                            @Param(value = SESSION_ITERATOR) GlobalSessionObject sessionIterator) {
        Map<String, String> result = new HashMap<>();
        delimiter = InputsUtils.getInputDefaultValue(delimiter, Constants.DEFAULT_DELIMITER);
        List<String> listStrings = Arrays.asList(list.split(delimiter));

        return result;
    }
}
