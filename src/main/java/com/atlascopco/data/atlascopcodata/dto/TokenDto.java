/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import com.atlascopco.data.atlascopcodata.model.Token;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TokenDto {
    private int count;
    private int documentCount;
    private long id;
    private String code;
    private Token.TokenType type; // UNDEFINED, DEFINED, FIXED
    private List<String> synonyms = new ArrayList<>(); // Replacements
    private List<SynonymTokenGroupDto> synonymsTokenGroups = new ArrayList<>();
    private List<SynonymTokenGroupDto> synonymTokenParents = new ArrayList<>();

    public TokenDto() {
    }

    public TokenDto(long id, String code) {
        this.id = id;
        this.code = code;
    }
    public TokenDto( String code) {
        this.code = code;
    }

    public TokenDto(Token token) {
        this.id = token.getId();
        this.code = token.getCode();
        this.type = token.getType();
        this.count = token.getCount();
        this.synonyms = token.getSynonyms();
        this.documentCount = token.getDocumentCount();
        this.synonymTokenParents = token.getSynonymParents().stream().map(SynonymTokenGroupDto::new).collect(Collectors.toList());
        this.synonymsTokenGroups = token.getSynonymGroups().stream().map(SynonymTokenGroupDto::new).collect(Collectors.toList());
    }
}
