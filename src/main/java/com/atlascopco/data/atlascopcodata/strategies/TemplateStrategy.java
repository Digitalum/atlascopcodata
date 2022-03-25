/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.dto.LineWrapper;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TemplateStrategy extends CleaningStrategy {

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "TEMPLATE".equals(type.getStrategy());
    }

    @Override
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {
            String template = dataRuleDto.getTemplate();
            String result = template;
            for (String extractKey : extractKeys(template)) {
                result = result.replace("[" + extractKey + "]", doc.getAttr(extractKey));
            }
            doc.setNew_name(result);
        }
    }



    protected Set<String> extractKeys(String template) {
        return Arrays.stream(template.split("\\]")).map(x -> x.replaceAll(".{0,250}\\[{1}", "")).collect(Collectors.toSet());
    }



}
