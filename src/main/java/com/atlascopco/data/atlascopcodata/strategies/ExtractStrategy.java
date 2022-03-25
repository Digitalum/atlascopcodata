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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractStrategy extends CleaningStrategy {

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "EXTRACT".equals(type.getStrategy());
    }



    @Override
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {
            final Pattern p = Pattern.compile(dataRuleDto.getRegex());
            String mapping = dataRuleDto.getMapping();

            // create matcher for pattern p and given string
            Matcher m = p.matcher(doc.getValue());
            // if an occurrence if a pattern was found in a given string...
            if (m.find()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    doc.getMappedValues().put(mapping.split(",")[i - 1], m.group(i).trim());
                }
            }
            System.out.println(doc.getMappedValues());
        }
    }

}
