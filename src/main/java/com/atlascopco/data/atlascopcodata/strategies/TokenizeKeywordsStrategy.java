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
import java.util.stream.Collectors;

@Component
public class TokenizeKeywordsStrategy extends CleaningStrategy {

    @Autowired
    private DefaultRulesService rulesService;
    @Autowired
    private DefaultTokenService tokenService;

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "TOKENIZE".equals(type.getStrategy());
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
                orCreateToken.setType(file.getType());
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
    public void clean(TranslationDocument doc, DataRuleDto dataRuleDto, Map<String, Object> ctx) {
        if (isRuleTriggered(doc, dataRuleDto.getTrigger(), ctx)) {

            final List<Token> sentences = (List<Token>) ctx.get("sentences");
            final Map<String, Token> m = (Map<String, Token>) ctx.get("sentencesM");

            String value = doc.getValue().toUpperCase().trim();
            for (String s : extractWords(value, sentences)) {
                if (!m.containsKey(s)) {
                    String type = "UNDEFINED";
                    if (s.matches(".*[aA-zZ].*") && s.matches(".*[0-9].*")) {
                        type = "UNDEFINED_ABBR";
                    }

                    final Token orCreateToken = tokenService.getOrCreateToken(s);
                    orCreateToken.setType(type);
                    m.put(s, orCreateToken);
                }
                m.get(s).getDocuments().add(doc);
                doc.getTokens().add(m.get(s));
            }
        }
    }

    public List<String> extractWords(String value, List<Token> sentences) {
        value = value.replace(". ", ".");
        List<String> split = List.of(value.split("(?<=[ \\.])"));
        List<String> result = new ArrayList<>();
        String rejoinString = "";

        split = rejoinFixedSentencesKeyword(sentences, split);

        for (String s : split) {
            if (countCharacters(s) > 1) {
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


    private List<String> rejoinFixedSentencesKeyword(List<Token> sentences, List<String> split) {
        final String joined = String.join("", split);
        final List<String> usedSentences = sentences.stream().map(Token::getId)
                .filter(sentence -> joined.toUpperCase().contains(sentence.toUpperCase())).collect(Collectors.toList());
        if (!usedSentences.isEmpty()) {
            String joined2 = String.join("$SEP$", split);
            for (String usedSentence : usedSentences) {
                joined2 = joined2.replaceAll("(?i)" + usedSentence.replace(" ", "[\\s]?\\$SEP\\$"), usedSentence);
            }
            split = List.of(joined2.split("\\$SEP\\$"));
        }
        return split;
    }

    private int countCharacters(String value) {
        return value.replaceAll("^[aA0-zZ9]", "").length();
    }

    private void generateReverseSynonym(Token sentence) {
        final List<String> strings = new ArrayList<>(Arrays.asList(sentence.getId().split(" ")));
        Collections.reverse(strings);
        String reversed = String.join(" ", strings);
        //sentence.setGenerated(true);
        sentence.getSynonyms().add(reversed);
    }

}
