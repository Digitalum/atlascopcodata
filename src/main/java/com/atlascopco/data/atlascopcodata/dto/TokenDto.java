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
    private String uuid;
    private String code;
    private Token.TokenType type; // UNDEFINED, DEFINED, FIXED
    private List<String> synonyms = new ArrayList<>(); // Replacements
    private List<SynonymTokenGroupDto> synonymParents = new ArrayList<>();
    private List<SynonymTokenGroupDto> synonymGroups = new ArrayList<>();

    public TokenDto() {
    }

    public TokenDto(String uuid, String code) {
        this.uuid = uuid;
        this.code = code;
    }
    public TokenDto( String code) {
        this.code = code;
    }

    public TokenDto(Token token) {
        this.uuid = token.getUuid();
        this.code = token.getCode();
        this.type = token.getType();
        this.count = token.getCount();
        this.synonyms = token.getSynonyms();
        this.documentCount = token.getDocumentCount();
        this.synonymParents = token.getSynonymParents().stream().map(SynonymTokenGroupDto::new).collect(Collectors.toList());
        this.synonymGroups = token.getSynonymGroups().stream().map(SynonymTokenGroupDto::new).collect(Collectors.toList());
    }
}
