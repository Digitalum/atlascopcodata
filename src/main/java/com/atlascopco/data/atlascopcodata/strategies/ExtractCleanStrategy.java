/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DataRuleDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExtractCleanStrategy extends CleaningStrategy {

    @Override
    public boolean isApplicable(DataRuleDto type) {
        return "EXTRACT_CLEAN".equals(type.getStrategy());
    }

    @Override
    public void clean(TranslationDocument doc, DataRuleDto ruleRule, Map<String, Object> ctx) {

        if (isRuleTriggered(doc, ruleRule.getTrigger(), ctx)) {

        }
    }

    private void extractDataClean(TranslationDocument document, DataRuleDto dataRuleDto, Map<String, Object> ctx) {

    }
}
