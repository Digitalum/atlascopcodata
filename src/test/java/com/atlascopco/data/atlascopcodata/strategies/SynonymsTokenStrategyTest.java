package com.atlascopco.data.atlascopcodata.strategies;

import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
public class SynonymsTokenStrategyTest {
    private SynonymsTokenStrategy synonymsTokenStrategy;

    @Mock
    private TranslationDocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        synonymsTokenStrategy = new SynonymsTokenStrategy();
    }

    @Test
    void extractWords() {
        TranslationDocument translationDocument = new TranslationDocument();

        final Token tokenXAS187 = new Token("XAS187");
        final Token tokenxas = new Token("XAS");
        final Token token187 = new Token("187");
        final Token token555 = new Token("555");
        final Token token666 = new Token("666");

        SynonymTokenGroup synonymTokenGroup = new SynonymTokenGroup();
        synonymTokenGroup.setParent(tokenXAS187);
        synonymTokenGroup.getTokens().add(tokenxas);
        synonymTokenGroup.getTokens().add(token187);

        tokenxas.getSynonymGroups().add(synonymTokenGroup);
        token187.getSynonymGroups().add(synonymTokenGroup);

        translationDocument.getTokens().add(tokenxas);
        translationDocument.getTokens().add(token187);
        translationDocument.getTokens().add(token555);
        translationDocument.getTokens().add(token666);
        synonymsTokenStrategy.clean(translationDocument, null, null);

        assertEquals(3, translationDocument.getTokens().size());
        assertEquals("XAS187", translationDocument.getTokens().get(0).getCode());
        assertEquals("555", translationDocument.getTokens().get(1).getCode());
        assertEquals("666", translationDocument.getTokens().get(2).getCode());
    }

}