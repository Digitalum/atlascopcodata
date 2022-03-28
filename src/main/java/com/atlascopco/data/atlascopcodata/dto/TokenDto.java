/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import com.atlascopco.data.atlascopcodata.model.Token;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TokenDto {
    private int count;
    private int documentCount;
    private String id;
    private Token.TokenType type; // UNDEFINED, DEFINED, FIXED
    private List<String> synonyms = new ArrayList<>(); // Replacements
    private List<TokenGroupDto> synonymsTokenGroups = new ArrayList<>();
    private TokenDto parent = null;

    public TokenDto() {
        this.id = id;
    }

    public TokenDto(Token token) {
        this.id = token.getId();
        this.type = token.getType();
        this.count = token.getCount();
        this.synonyms = token.getSynonyms();
        this.documentCount = token.getDocumentCount();
    }
}
