/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules.dto;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LineWrapper {
    private int idx;
    private String value;
    private TranslationDocument doc;
    private Map<String,Object> global = new HashMap<>();

    public Map<String, Object> getGlobal() {
        return global;
    }
}
