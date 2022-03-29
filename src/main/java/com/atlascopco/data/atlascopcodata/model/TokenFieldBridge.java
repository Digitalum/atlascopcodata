/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import java.util.Collection;

public class TokenFieldBridge implements TwoWayFieldBridge {

    @Override
    public void set(String name, Object tokens, Document document, LuceneOptions luceneOptions) {
        for (Token token : (Collection<Token>) tokens) {
            luceneOptions.addFieldToDocument(name, token.getUuid(), document);
        }
    }

    @Override
    public Object get(String s, Document document) {
        return null;
    }

    @Override
    public String objectToString(Object o) {
        return String.valueOf(o);
    }
}
