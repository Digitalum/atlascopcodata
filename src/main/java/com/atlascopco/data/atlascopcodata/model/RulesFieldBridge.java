/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import java.util.Collection;

public class RulesFieldBridge implements TwoWayFieldBridge {

    @Override
    public void set(String name, Object rules, Document document, LuceneOptions luceneOptions) {
        for (String rule : (Collection<String>) rules) {
            luceneOptions.addFieldToDocument(name, rule, document);
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
