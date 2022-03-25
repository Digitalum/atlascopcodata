/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultDocumentService {

    @Autowired
    private SearchService searchService;

    public List<TranslationDocument> getDocumentsForKeyword(String token) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(TranslationDocument.class);
        searchRequest.setPageable(PageRequest.of(0, 5000));
        searchRequest.addFilter("tokens", token);
        final SearchResponse<TranslationDocument> search = searchService.search(searchRequest);
        return search.getResult();
    }
}
