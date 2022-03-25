/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KeywordDto implements Comparable {
    private String key;
    private String type; // UNDEFINED, DEFINED, FIXED
    private List<String> synonyms = new ArrayList<>();
    private List<String> partNumbers = new ArrayList<>();
    private SynonymDto synonymDto;

    public KeywordDto(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public int getCount() {
        return partNumbers.size();
    }

    @Override
    public int compareTo(Object o) {
        return ((KeywordDto) o).getCount() - this.getCount();
    }
}
