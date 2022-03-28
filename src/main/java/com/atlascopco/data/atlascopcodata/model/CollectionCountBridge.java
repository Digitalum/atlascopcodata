/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import java.util.Collection;

public class CollectionCountBridge implements TwoWayFieldBridge {

    @Override
    public void set(String name, Object tokens, Document document, LuceneOptions luceneOptions) {
        luceneOptions.addFieldToDocument(name, objectToString(tokens), document);
    }

    @Override
    public Object get(String s, Document document) {
        return null;
    }

    @Override
    public String objectToString(Object object) {
        if (object == null || (!(object instanceof Collection))) {
            return null;
        }
        Collection<?> coll = (Collection<?>) object;
        return String.valueOf(coll.size());
    }
}
