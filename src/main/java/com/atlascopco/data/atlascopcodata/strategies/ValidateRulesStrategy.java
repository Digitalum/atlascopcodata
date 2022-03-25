/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dto.SynonymDto;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.rules.dto.LineWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component("validateRulesStrategy")
public class ValidateRulesStrategy {

    @Autowired
    private DefaultRulesService rulesService;





/*
    private void synonym(TranslationDocument document, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        String template = dataRuleDto.getTemplate();
        String result = template;
        for (String extractKey : extractKeys(template)) {
            result = result.replace("[" + extractKey + "]", document.getAttr(extractKey));
        }
        document.setNew_name(result);
    }


*/

}