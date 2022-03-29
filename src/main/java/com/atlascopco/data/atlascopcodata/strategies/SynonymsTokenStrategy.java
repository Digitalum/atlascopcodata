/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SynonymsTokenStrategy extends CleaningStrategy {

    @Autowired
    private TranslationDocumentRepository documentRepository;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "SYNONYM_TOKEN".equals(type.getStrategy());
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
    public void clean(TranslationDocument doc, DataRuleDto ruleRule, Map<String, Object> ctx) {
        final Map<String, Token> m = (Map<String, Token>) ctx.get("tokenMap");
        List<Token> newTokenList = new ArrayList<>();
        boolean hasReplacements = false;
        hasReplacements = replaceSynonymTokens(doc.getTokens(), newTokenList, hasReplacements, 10, m);
        if (hasReplacements) {
            doc.removeAllTokens();
            for (Token token : newTokenList) {
                doc.addToken(token);
            }

            //doc.getTokens().addAll(newTokenList);
            //System.out.println(doc.getTokens());
            documentRepository.save(doc);
        }
    }

    private boolean replaceSynonymTokens(List<Token> oldTokenList, List<Token> newTokenList, boolean hasReplacements, int count, Map<String, Token> m) {
        boolean hasReplacementInCurrentIteration = false;
        if (count <= 0) {
            return hasReplacements;
        }

        //System.out.println(oldTokenList);
        count--;
        for (int i = 0; i < oldTokenList.size(); i++) {
            Token token = oldTokenList.get(i);
            if (token == null || token.getCode() == null) {
                System.out.println(count);
                System.out.println(token);
                System.out.println(token.getUuid());
                System.out.println(token.getType());
                System.out.println(token.getCode());
            }
            token = getOrCreateToken(m, token);

            final List<SynonymTokenGroup> allByTokensIn = token.getSynonymGroups();

            if (CollectionUtils.isNotEmpty(allByTokensIn)) {
                boolean isReplaced = false;
                for (SynonymTokenGroup synonymParent : allByTokensIn) {
                    final int tokenCount = synonymParent.getTokens().size();
                    if (oldTokenList.size() >= i + tokenCount) {
                        final String tokenkey = synonymParent.getTokens().stream().map(Token::getCode).collect(Collectors.joining(""));
                        StringBuilder builder = new StringBuilder();

                        for (int a = i; a < i + tokenCount; a++) {
                            builder.append(oldTokenList.get(a).getCode());
                        }

                        if (tokenkey.equals(builder.toString())) {
                            newTokenList.add(getOrCreateToken(m, synonymParent.getParent()));
                            i += tokenCount - 1;
                            hasReplacements = true;
                            isReplaced = true;
                            hasReplacementInCurrentIteration = true;
                            break;
                        }
                    }
                }
                if (!isReplaced) {
                    newTokenList.add(getOrCreateToken(m, token));
                }
            } else {
                newTokenList.add(getOrCreateToken(m, token));
            }
        }
        if (hasReplacementInCurrentIteration) {
            oldTokenList = new ArrayList<>(newTokenList);
            newTokenList.clear();
            replaceSynonymTokens(oldTokenList, newTokenList, hasReplacements, count, m);
        }
        return hasReplacements;
    }


}