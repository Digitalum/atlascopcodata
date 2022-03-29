/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SynonymTokenGroupDto {

    private TokenDto parent;
    private String parentType;
    private List<TokenDto> tokens = new ArrayList<>();

    public SynonymTokenGroupDto() {
    }

    public SynonymTokenGroupDto(SynonymTokenGroup synonymTokenGroup) {
        this.parent = new TokenDto(synonymTokenGroup.getParent().getUuid(), synonymTokenGroup.getParent().getCode());
        this.parentType = synonymTokenGroup.getParent().getType().toString();
        this.tokens = synonymTokenGroup.getTokens().stream().map(x -> new TokenDto(x.getUuid(), x.getCode())).collect(Collectors.toList());
    }
}
