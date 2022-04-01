package com.atlascopco.data.atlascopcodata.rules;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.strategies.RegexStrategy;
import com.atlascopco.data.atlascopcodata.strategies.ValidateRulesStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */

class ValidateRulesStrategyTest {

    private RegexStrategy regexStrategy;


    @BeforeEach
    void setUp() {
        regexStrategy = new RegexStrategy();
    }

    //@Test
    void hasUserFactoryInsertTest() {
        String id = "1";
        String code = "1";
        String original_name = "Test";
        String category = "01.01.01";
        String brand = "AC";
        TranslationDocument translationDocument = new TranslationDocument( code, original_name, category, brand);
        DataRuleDto dataRuleDto = new DataRuleDto();
        dataRuleDto.setCode("R001");
        dataRuleDto.setTrigger("true");
        dataRuleDto.setRegex("value.toUpperCase()");
        regexStrategy.clean(translationDocument, dataRuleDto, null);
        assertThat(translationDocument.getNew_name()).isEqualTo("TEST");
    }


    void regexTest() {
        String id = "1";
        String code = "1";
        String original_name = "DIGGING CHISEL HEX 22 X 82.5 MM (US)";
        String category = "03.02.03.01";
        String brand = "AC";
        TranslationDocument translationDocument = new TranslationDocument( code, original_name, category, brand);
        DataRuleDto dataRuleDto = new DataRuleDto();
        dataRuleDto.setCode("R001");
        dataRuleDto.setTrigger("true");
        dataRuleDto.setRegex("value.toUpperCase()");


        final Pattern p = Pattern.compile("^([a-zA-Z\\s]+)([hH][eE][xX])[\\s]*([0-9\\.]+)[\\s]*[xX][\\s]*([0-9\\.]+)[\\s]*([mM]{2})(.*)");

        String mapping = "name,shank,shank_dia,shank_length,shank_unit,postfix";

        // create matcher for pattern p and given string
        Matcher m = p.matcher(original_name);

        Map<String, String> map = new HashMap<>();
        // if an occurrence if a pattern was found in a given string...
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                map.put(mapping.split(",")[i - 1], m.group(i).trim());
                System.out.println(mapping.split(",")[i - 1] + ": " + m.group(i).trim()); // whole matched expression
            }
        }

        String template = "[name] [shank] [shank_dia]x[shank_length][shank_unit] [postfix]";
        String result = template;
        for (String extractKey : extractKeys(template)) {
            result = result.replace("[" + extractKey + "]", map.get(extractKey));
        }

        System.out.println(original_name);
        System.out.println(result);
    }

    private String[] extractKeys(String template) {

        return null;
    }

}