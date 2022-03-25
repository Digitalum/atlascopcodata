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

@Component
public class RegexStrategy extends CleaningStrategy {

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "REGEX".equals(type.getStrategy());
    }

    @Override
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {
            String ruleRegex = dataRuleDto.getRegex();
            if (ruleRegex != null) {
                ExpressionParser parser2 = new SpelExpressionParser();
                LineWrapper lineWrapper = createLineWrapper(doc, ctx);
                // REMARK! space added just after parseExpression     to avoid XSS issue SAP Commerce
                Expression exp2 = parser2.parseExpression(ruleRegex);
                StandardEvaluationContext context2 = new StandardEvaluationContext(lineWrapper);
                String newValue = (String) exp2.getValue(context2);
                doc.setNew_name(newValue);
            }
        }
    }

}
