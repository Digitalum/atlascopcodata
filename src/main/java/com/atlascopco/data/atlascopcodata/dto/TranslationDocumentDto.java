/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TranslationDocumentDto {
    private final String category;
    private final long id;
    private final String code;
    private final List<TokenDto> tokens;
    private final String original_name;
    private final String new_name;
    private final String newNameTranslated;
    private final String value;
    private final List<String> changes;
    private final String brand;
    private final boolean completelyTokenized;

    public TranslationDocumentDto(TranslationDocument translationDocument) {
        this.id = translationDocument.getId();
        this.code = translationDocument.getCode();
        this.tokens = translationDocument.getTokens().stream().map(TokenDto::new).collect(Collectors.toList());
        this.original_name = translationDocument.getOriginal_name();
        this.new_name = translationDocument.getNew_name();
        this.value = translationDocument.getValue();
        this.changes = translationDocument.getChanges();
        this.brand = translationDocument.getBrand();
        this.category = translationDocument.getCategory();
        this.newNameTranslated = translationDocument.getNewNameTranslated();
        this.completelyTokenized = translationDocument.isCompletelyTokenized();
    }
}
