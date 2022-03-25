/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SynonymDto {
    private boolean generated;
    private String key;
    private List<String> synonyms = new ArrayList<>();

    public SynonymDto(String key) {
        this.key = key;
    }
}
