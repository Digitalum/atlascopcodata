package com.atlascopco.data.atlascopcodata.strategies;/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */

import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.dto.LineWrapper;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class CleaningStrategy {

    @Autowired
    private DefaultTokenService tokenService;

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

    public Token getOrCreateToken(Map<String, Token> m, String tokenCode1) {
        Token token;
        String tokenCode = normalize(tokenCode1);
        if (m.containsKey(tokenCode)) {
            token = getToken(m, tokenCode);
        } else {
            token = tokenService.getOrCreateTokenByCode(tokenCode);
            m.put(tokenCode, token);
        }
        return token;
    }

    public Token getOrCreateToken(Map<String, Token> m, Token token) {
        String tokenCode = normalize(token.getCode());
        if (m.containsKey(tokenCode)) {
            token = getToken(m, tokenCode);
        } else {
            token = tokenService.getOrCreateTokenByCode(tokenCode);
            m.put(tokenCode, token);
        }
        return token;
    }

    protected Token getToken(Map<String, Token> m, String token) {
        if (m.get(normalize(token)) == null) {
            System.out.println(" Token not found " + token);
        }
        return m.get(normalize(token));
    }


    public static String normalize(String code) {
        return code.trim().toUpperCase();
    }

}
