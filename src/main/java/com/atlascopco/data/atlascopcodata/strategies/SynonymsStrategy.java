/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.SynonymDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.rules.FileSynonymDto;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SynonymsStrategy extends CleaningStrategy {

    @Autowired
    private DefaultRulesService rulesService;
    @Autowired
    private DefaultTokenService tokenService;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "SYNONYMS".equals(type.getStrategy()) || "SYNONYMS_REVERSED".equals(type.getStrategy());
    }

    @Override
    public Map<String, Object> createContext(DataRuleDto ruleRule) {
        final Map<String, Object> context = super.createContext(ruleRule);
        List<Token> allSys = new ArrayList<>();
        Map<String, Token> m = new HashMap<>();
        for (FileSynonymDto file : ruleRule.getFiles()) {
            final List<Token> synonyms = tokenService.getTokens(file.getType());
            allSys.addAll(synonyms);

            for (Token name : synonyms) {
                final Token orCreateToken = tokenService.getOrCreateToken(name.getId());
                orCreateToken.setId(file.getType());
                m.put(name.getId(), orCreateToken);
                if (file.isRevert()) {
                    generateReverseSynonym(name);
                }
            }
        }

        context.put("sentences", allSys);
        context.put("sentencesM", m);
        return context;
    }

    @Override
    public void clean(TranslationDocument doc, DataRuleDto ruleRule, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, ruleRule.getTrigger(), ctx)) {
            final List<SynonymDto> synonymDtos = (List<SynonymDto>) ctx.get("sentences");


            for (SynonymDto synonym : synonymDtos) {
                for (String s : synonym.getSynonyms()) {
                    String s2 = "(?i)" + s.replace(".", "\\.");
                    String newValue = synonym.getKey();
                    String oldValue = doc.getValue();
                    doc.setNew_name(replaceSynonym(oldValue, s2, newValue));
                }
            }
        }
    }

    private void generateReverseSynonym(Token sentence) {
        final List<String> strings = new ArrayList<>(Arrays.asList(sentence.getId().split(" ")));
        Collections.reverse(strings);
        String reversed = String.join(" ", strings);
        //sentence.setGenerated(true);
        sentence.getSynonyms().add(reversed);
    }

    public String replaceSynonym(String oldValue, String s2, String newValue) {
        oldValue = " " + oldValue + " ";

        Pattern p = Pattern.compile("(.*[^aA0-zZ9])" + s2 + "([^aA0-zZ9].*)");
        Matcher m = p.matcher(oldValue);
        if (m.find()) {
            // replace first number with "number" and second number with the first
           return m.replaceFirst("$1" + newValue + "$2").trim();  // number 46
        }

        return oldValue.trim();
    }
}
