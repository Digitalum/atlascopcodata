package com.atlascopco.data.atlascopcodata.strategies;/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.dto.LineWrapper;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class CleaningStrategy {

    public abstract boolean isApplicable(DataRuleDto type);

    public Map<String, Object> createContext(Map<String, Object> ctx, DataRuleDto ruleRule) {
        return ctx;
    }

    ;

    public abstract void clean(TranslationDocument doc, DataRuleDto ruleRule, Map<String, Object> ctx);


    protected LineWrapper createLineWrapper(TranslationDocument doc, Map<String, Object> globalVariables) {
        LineWrapper lineWrapper = new LineWrapper();
        lineWrapper.setDoc(doc);
        if (StringUtils.isEmpty(doc.getNew_name())) {
            lineWrapper.setValue(doc.getOriginal_name());
        } else {
            lineWrapper.setValue(doc.getNew_name());
        }
        return lineWrapper;
    }

    public boolean isRuleTriggered(TranslationDocument document, String trigger, Map<String, Object> ctx) {
        ExpressionParser parser = new SpelExpressionParser();
        // REMARK! space added just after parseExpression     to avoid XSS issue
        Expression exp = parser.parseExpression(trigger);
        LineWrapper lineWrapper = createLineWrapper(document, ctx);
        lineWrapper.setDoc(document);
        StandardEvaluationContext context = new StandardEvaluationContext(lineWrapper);
        return (boolean) exp.getValue(context);
    }
}
