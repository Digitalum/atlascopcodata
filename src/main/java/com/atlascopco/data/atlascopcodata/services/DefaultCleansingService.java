/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.services;

import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.SynonymDto;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.rules.RuleGroupDto;
import com.atlascopco.data.atlascopcodata.strategies.CleaningStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class DefaultCleansingService {
    @Autowired
    private DefaultRulesService rulesService;
    @Autowired
    private List<CleaningStrategy> cleaningStrategies;
    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;

    @Transactional
    public void executeCleaningRules() throws Exception {
        executeCleaningRules(translationDocumentRepository.findAll());
    }

    public void executeCleaningRules(List<TranslationDocument> docs) throws Exception {

        System.out.println("Execute Cleaning Rules");
        final List<RuleGroupDto> rules = rulesService.getRules();
        Map<String, KeywordDto> m = null;
        List<SynonymDto> synonyms = null;
        //ctx.put()
        for (RuleGroupDto rule : rules) {
            for (DataRuleDto ruleRule : rule.getRules()) {
                int j = 0;
                System.out.println(ruleRule.getCode() + " : START");
                CleaningStrategy cleaningStrategy = null;
                for (CleaningStrategy cleaningStrategy1 : cleaningStrategies) {
                    if (cleaningStrategy1.isApplicable(ruleRule)) {
                        cleaningStrategy = cleaningStrategy1;
                    }
                }

                int i = 0;
                if (cleaningStrategy != null) {
                    final Map<String, Object> ctx = cleaningStrategy.createContext(ruleRule);
                    for (TranslationDocument doc : docs) {
                        j++;
                        if (j % 1000 == 0) {
                            System.out.print("X");
                        }
                        String value = doc.getValue();
                        cleaningStrategy.clean(doc, ruleRule, ctx);
                        if (!value.equals(doc.getValue())) {
                            doc.getChanges().add(ruleRule.getCode() + " : '" + value + "' -> '" + doc.getValue() + "'");
                            i++;
                            translationDocumentRepository.save(doc);
                        }
                    }
                    if (ctx.containsKey("sentencesM")) {
                        m = (Map<String, KeywordDto>) ctx.get("sentencesM");
                    }
                    if (ctx.containsKey("sentences")) {
                        synonyms = (List<SynonymDto>) ctx.get("sentences");
                    }
                }
                System.out.println();
                System.out.println(ruleRule.getCode() + " : " + i);
                System.out.println();
            }
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void resetDocuments() {

        TypedQuery<TranslationDocument> schoolQuery = entityManager.createQuery("SELECT s from TranslationDocument s", TranslationDocument.class);
        List<TranslationDocument> docs = schoolQuery.getResultList();

        System.out.println("Reset Documents " + docs.size());
        int j = 0;
        for (TranslationDocument doc : docs) {
            j++;
            doc.setNew_name("");
            doc.setTokens(new ArrayList<>());
            doc.setChanges(new ArrayList<>());
            doc.setMappedValues(new HashMap<>());
            if (j % 1000 == 0) {
                System.out.print("X");
            }
        }

        System.out.println();
        System.out.println();
    }

    /*
        public void cleanSynonyms(List<TranslationDocument> docs) throws Exception {
            final List<SynonymDto> sentences = rulesService.getSentences();
            final List<SynonymDto> keywords = rulesService.getKeywords();
            final List<SynonymDto> synonyms = new ArrayList<>();

            synonyms.addAll(keywords);
            for (SynonymDto sentence : sentences) {
                final SynonymDto synonymDto = generateReverseSynonym(sentence);
                synonyms.add(synonymDto);
            }

            for (SynonymDto synonym : synonyms) {
                for (String s : synonym.getSynonyms()) {
                    for (TranslationDocument doc : docs) {
                        String s2 = "(?i)" + s.replace(".", "\\.");
                        String newValue = synonym.getKey();
                        String oldValue = doc.getValue();
                        doc.setNew_name(replaceSynonym(oldValue, s2, newValue));
                    }
                }
            }
        }
    */
    public String replaceSynonym(String oldValue, String s2, String newValue) {
        oldValue = " " + oldValue + " ";
        return oldValue.replaceAll("[^aA0-zZ9]" + s2 + "[^aA0-zZ9]", " " + newValue + " ").trim();
    }
/*
    public List<KeywordDto> extractKeywords(List<TranslationDocument> docs) throws Exception {
        final List<SynonymDto> sentences = rulesService.getSentences();
        final List<SynonymDto> fixedNames = rulesService.getFixedNames();
        final List<SynonymDto> synonyms = rulesService.getKeywords();


        Map<String, KeywordDto> m = new HashMap<>();
        for (SynonymDto sentence : sentences) {
            m.put(sentence.getKey(), new KeywordDto(sentence.getKey(), "SENTENCE"));
            final SynonymDto synonymDto = generateReverseSynonym(sentence);
            synonyms.add(synonymDto);
        }
        for (SynonymDto name : fixedNames) {
            m.put(name.getKey(), new KeywordDto(name.getKey(), "FIXED_NAME"));
        }
        for (SynonymDto value : synonyms.stream().filter(x -> !x.isGenerated()).collect(Collectors.toList())) {
            m.put(value.getKey(), new KeywordDto(value.getKey(), "WORD"));
        }


        for (TranslationDocument doc : docs) {
            String value = doc.getValue().toUpperCase().trim();
            for (String s : extractWords(value, sentences)) {
                if (!m.containsKey(s)) {
                    String type = "UNDEFINED";
                    if (s.matches(".*[aA-zZ].*") && s.matches(".*[0-9].*")) {
                        type = "UNDEFINED_ABBR";
                    }
                    m.put(s, new KeywordDto(s, type));
                }
                m.get(s).getPartNumbers().add(doc.getCode());
                doc.getKeywords().add(m.get(s));
            }
        }

        for (KeywordDto value : m.values()) {
            for (SynonymDto synonym : synonyms) {
                if (synonym.getKey().equalsIgnoreCase(value.getKey().toUpperCase())) {
                    value.setSynonymDto(synonym);
                }
            }
        }

        return m.values().stream().sorted().collect(Collectors.toList());
    }

    private SynonymDto generateReverseSynonym(SynonymDto sentence) {
        final List<String> strings = new ArrayList<>(Arrays.asList(sentence.getKey().split(" ")));
        Collections.reverse(strings);
        String reversed = String.join(" ", strings);
        sentence.setGenerated(true);
        sentence.getSynonyms().add(reversed);
        return sentence;
    }

*/
}
