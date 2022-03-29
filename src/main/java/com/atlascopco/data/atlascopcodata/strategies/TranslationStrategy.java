/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dao.TokenTranslationRepository;
import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.model.TokenTranslation;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TranslationStrategy extends CleaningStrategy {

    @Autowired
    private TokenTranslationRepository tokenTranslationRepository;
    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "TRANSLATION".equals(type.getStrategy());
    }


    @Override
    public Map<String, Object> createContext(Map<String, Object> ctx, DataRuleDto ruleRule) {
        final Map<String, Object> context = super.createContext(ctx, ruleRule);

        final Map<String, String> tokenTranslations = tokenTranslationRepository.findAll().stream()
                .filter(x -> "nl".equals(x.getKey().getLanguage()))
                .collect(Collectors.toMap(x -> x.getKey().getTokenCode(), TokenTranslation::getValue));
        Map<String, Map<String, String>> tranMap = new HashMap<>();
        tranMap.put("nl", tokenTranslations);

        System.out.println("createContext DONE");
        context.put("tokenTranslationMap", tranMap);
        return context;
    }

    @Transactional
    @Override
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {
            Map<String, Map<String, String>> tranMap = (Map<String, Map<String, String>>) ctx.get("tokenTranslationMap");
            final Map<String, String> nlMap = tranMap.get("nl");

            final String newNameTranslated = doc.getNewNameTranslated();
            final String translated = doc.getTokens().stream().map(x -> translate(x.getCode(), nlMap)).collect(Collectors.joining(" "));
            if (!translated.equals(newNameTranslated)) {
                doc.setNewNameTranslated(translated);
                translationDocumentRepository.save(doc);
            }
        }
    }

    private String translate(String value, Map<String, String> m) {
        if (m.containsKey(value)) {
            return m.get(value);
        }
        return value;
    }
}
