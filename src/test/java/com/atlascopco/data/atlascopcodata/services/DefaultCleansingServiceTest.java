package com.atlascopco.data.atlascopcodata.services;

import com.atlascopco.data.atlascopcodata.dto.SynonymDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.strategies.SynonymsStrategy;
import com.atlascopco.data.atlascopcodata.strategies.TokenizeKeywordsStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DefaultCleansingServiceTest {
    private SynonymsStrategy synonymsStrategy;
    private TokenizeKeywordsStrategy tokenizeKeywordsStrategy;

    @BeforeEach
    void setUp() {
        synonymsStrategy = new SynonymsStrategy();
        tokenizeKeywordsStrategy = new TokenizeKeywordsStrategy();
    }

    @Test
    void extractWords() {
        List<String> values = tokenizeKeywordsStrategy.extractWords("P.E. GENERATOR");
        assertEquals("P.", values.get(0));
        assertEquals("E.", values.get(1));
        assertEquals("GENERATOR", values.get(2));
        values = tokenizeKeywordsStrategy.extractWords("test 123.abc");
        assertEquals("test", values.get(0));
        assertEquals("123.", values.get(1));
        assertEquals("abc", values.get(2));
        values = tokenizeKeywordsStrategy.extractWords("o-ring test-test");
        assertEquals("o-ring", values.get(0));
        assertEquals("test-test", values.get(1));
        values = tokenizeKeywordsStrategy.extractWords("test BALL BEARING");
        assertEquals("test", values.get(0));
        assertEquals("BALL BEARING", values.get(1));
        values = tokenizeKeywordsStrategy.extractWords("BEARING(+)Test");
        assertEquals("BEARING", values.get(0));
        assertEquals("(+)", values.get(1));
        assertEquals("Test", values.get(2));
        values = tokenizeKeywordsStrategy.extractWords("FRAME BOX2.2 MB");
        assertEquals("FRAME", values.get(0));
        assertEquals("BOX2.", values.get(1));
        assertEquals("2", values.get(2));
        assertEquals("MB", values.get(3));
        values = tokenizeKeywordsStrategy.extractWords("BEARING(+)");
        assertEquals("BEARING", values.get(0));
        assertEquals("(+)", values.get(1));
    }

    @Test
    void extractWordsFixedSentence() {
        List<String> values = tokenizeKeywordsStrategy.extractWords("test BAll BEARING");
        assertEquals("test", values.get(0));
        assertEquals("BALL BEARing", values.get(1));
    }

    @Test
    void replaceSynonym() {
        assertEquals("ASSEMBLED DOOR", synonymsStrategy.replaceSynonym("ASSEMBLED DOOR", "ASSEMBL", "ASSEMBLY"));
        assertEquals("ASSEMBLY", synonymsStrategy.replaceSynonym("ASSEMBL", "ASSEMBL", "ASSEMBLY"));
        assertEquals("TESASSEMBL", synonymsStrategy.replaceSynonym("TESASSEMBL", "ASSEMBL", "ASSEMBLY"));
        assertEquals("(ASSEMBLY)", synonymsStrategy.replaceSynonym("(ASSEMBL)", "ASSEMBL", "ASSEMBLY"));
        assertEquals("TEST (ASSEMBLY)Test", synonymsStrategy.replaceSynonym("TEST (ASSEMBL)Test", "ASSEMBL", "ASSEMBLY"));
    }
}