/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SynonymsTokenStrategy extends CleaningStrategy {

    @Autowired
    private TranslationDocumentRepository documentRepository;
    @Autowired
    private DefaultTokenService tokenService;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "SYNONYM_TOKEN".equals(type.getStrategy());
    }

    @Override
    public void clean(TranslationDocument doc, DataRuleDto ruleRule, Map<String, Object> ctx) {
        //if (isRuleTriggered(doc, ruleRule.getTrigger(), ctx)) {
        List<Token> newTokenList = new ArrayList<>();
        boolean hasReplacements = false;
        hasReplacements = replaceSynonymTokens(doc.getTokens(), newTokenList, hasReplacements, 10);
        if (hasReplacements) {
            doc.setTokens(new ArrayList<>());
            doc.getTokens().addAll(newTokenList);
            documentRepository.save(doc);
        }
    }

    private boolean replaceSynonymTokens(List<Token> oldTokenList, List<Token> newTokenList, boolean hasReplacements, int count) {
        boolean hasReplacementInCurrentIteration = false;
        if (count <= 0) {
            return hasReplacements;
        }
        count--;
        for (int i = 0; i < oldTokenList.size(); i++) {
            Token token = oldTokenList.get(i);

            token = tokenService.getOrCreateToken(token);
            final List<SynonymTokenGroup> allByTokensIn = token.getSynonymGroups();

            if (CollectionUtils.isNotEmpty(allByTokensIn)) {
                boolean isReplaced = false;
                for (SynonymTokenGroup synonymParent : allByTokensIn) {
                    final int tokenCount = synonymParent.getTokens().size();
                    if (oldTokenList.size() >= i + tokenCount) {
                        final String tokenkey = synonymParent.getTokens().stream().map(Token::getCode).collect(Collectors.joining(""));
                        StringBuilder builder = new StringBuilder();

                        for (int a = i; a < i + tokenCount; a++) {
                            builder.append(oldTokenList.get(a).getId());
                        }

                        if (tokenkey.equals(builder.toString())) {
                            newTokenList.add(synonymParent.getParent());
                            i += tokenCount - 1;
                            hasReplacements = true;
                            isReplaced = true;
                            hasReplacementInCurrentIteration = true;
                            break;
                        }
                    }
                }
                if (!isReplaced) {
                    newTokenList.add(token);
                }
            } else {
                newTokenList.add(token);
            }
        }
        if (hasReplacementInCurrentIteration) {
            oldTokenList = new ArrayList<>(newTokenList);
            newTokenList.clear();
            replaceSynonymTokens(oldTokenList, newTokenList, hasReplacements, count);
        }
        return hasReplacements;
    }
}