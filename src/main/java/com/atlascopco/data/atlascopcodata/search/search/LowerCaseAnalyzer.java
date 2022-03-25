/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;

public class LowerCaseAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer source = new KeywordTokenizer();
        TokenStream filter1 = new LowerCaseFilter(source);
        return new TokenStreamComponents(source, filter1);
    }
}