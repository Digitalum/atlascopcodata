/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenizeKeywordsStrategy extends CleaningStrategy {

    @Autowired
    private DefaultTokenService tokenService;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "TOKENIZE".equals(type.getStrategy());
    }

    @Override
    public Map<String, Object> createContext(Map<String, Object> ctx, DataRuleDto ruleRule) {
        final Map<String, Object> context = super.createContext(ctx, ruleRule);
        if (!ctx.containsKey("tokenMap")) {
            final Map<String, Token> m = new HashMap<>();// tokenService.getAllTokens().stream().collect(Collectors.toMap(Token::getCode, y -> y));
            context.put("tokenMap", m);
        }


        System.out.println("createContext DONE");
        return context;
    }

    @Transactional
    @Override
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {
            final Map<String, Token> m = (Map<String, Token>) ctx.get("tokenMap");

            String value = doc.getValue().toUpperCase().trim();
            for (String s : extractWords(value)) {
                if (!m.containsKey(s)) {
                    final Token orCreateToken = tokenService.getOrCreateTokenByCode(s);
                    orCreateToken.setType(getDefaultTokenType(s));
                    m.put(s, orCreateToken);
                }
                Token token = m.get(s);
                if (token == null) {
                    System.out.println("                           " + value);
                    System.out.println("                           " + s);
                }
                token.getDocuments().add(doc);
                doc.getTokens().add(token);
            }
        }
    }

    private Token.TokenType getDefaultTokenType(String s) {
        String type = "UNDEFINED";
        if (s.matches(".*[aA-zZ].*") && s.matches(".*[0-9].*")) {
            type = "UNDEFINED_ABBR";
        }
        return Token.TokenType.valueOf(type);
    }

    public List<String> extractWords(String value) {
        List<String> split1 = List.of(value.split("(?<=[ \\.\\,:)])"));
        List<String> split = new ArrayList<>();
        for (String s : split1) {
            split.addAll(List.of(s.split("(?=[\\(])")));
        }
        split = split.stream().filter(x -> !x.isBlank()).collect(Collectors.toList());

        List<String> result = new ArrayList<>();
        String rejoinString = "";

        for (String s : split) {
            if (countCharacters(s) > 0) {
                if (!rejoinString.isEmpty()) {
                    result.add(rejoinString.trim());
                    rejoinString = "";
                }
                result.add(s.trim());
            } else {
                rejoinString += s;
            }
        }
        if (!rejoinString.isEmpty()) {
            result.add(rejoinString.trim());
        }

        return result;
    }

    private int countCharacters(String value) {
        return value.replaceAll("^[aA0-zZ9]", "").length();
    }
}
