/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

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
            final List<Token> synonyms = tokenService.getTokens(Token.TokenType.valueOf(file.getType()));
            System.out.println(file.getType() + " : " + synonyms.size());
            allSys.addAll(synonyms);

            for (Token name : synonyms) {
                final Token orCreateToken = tokenService.getOrCreateToken(name.getId());
                orCreateToken.setType(Token.TokenType.valueOf(file.getType()));
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
            final List<Token> synonymDtos = (List<Token>) ctx.get("sentences");

            String oldValuelower = doc.getValue().toLowerCase();
            for (Token synonym : synonymDtos) {
                for (String s : synonym.getSynonyms()) {
                    if (oldValuelower.contains(s.toLowerCase())) {
                        String oldValue = doc.getValue();

                        String s2 = "(?i)" + s.replace(".", "\\.");
                        String newValue = synonym.getId();
                        doc.setNew_name(replaceSynonym(oldValue, s2, newValue));
                    }
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
