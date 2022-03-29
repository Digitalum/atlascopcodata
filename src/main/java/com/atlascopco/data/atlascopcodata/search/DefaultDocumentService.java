/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.Paging;
import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.dto.TranslationDocumentDto;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacet;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacetValue;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchResponse;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultDocumentService {

    @Autowired
    private SearchService searchService;
    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;

    public TranslationDocument getDocumentForCode(String token) {
        return translationDocumentRepository.findByCode(token).get();
    }

    public SearchFacet getFacets() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(TranslationDocument.class);
        searchRequest.setPageable(PageRequest.of(0, 100));
        final FacetManager facetManager = searchService.searchFacets(searchRequest);
        return getFacet(facetManager, "completelyTokenized");
    }

    private SearchFacet getFacet(FacetManager facetManager, String facet) {
        List<SearchFacetValue> facetResult = facetManager.getFacets(facet)
                .stream().map(f -> new SearchFacetValue(f.getValue(), f.getCount())).collect(Collectors.toList());
        SearchFacet searchFacet = new SearchFacet(facet, facet);
        searchFacet.getFacetValues().addAll(facetResult);
        return searchFacet;
    }

    public List<TranslationDocument> getDocumentsTokenUUid(String tokenId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(TranslationDocument.class);
        searchRequest.setPageable(PageRequest.of(0, 5000));
        searchRequest.addFilter("tokens", tokenId);
        final SearchResponse<TranslationDocument> search = searchService.search(searchRequest);
        return search.getResult();
    }


    public Paged<TranslationDocumentDto> getPagedDocuments(SearchRequest searchRequest) {
        final SearchResponse<TranslationDocument> search = searchService.search(searchRequest);
        final List<TranslationDocumentDto> tokendDtos = search.getResult().stream().map(TranslationDocumentDto::new).collect(Collectors.toList());
        final PageImpl<TranslationDocumentDto> page = new PageImpl<>(tokendDtos, searchRequest.getPageable(), search.getResultSize());

        return new Paged<>(page, Paging.of(page.getTotalPages(), searchRequest.getPageable().getPageNumber(), searchRequest.getPageable().getPageSize()));
    }


    public List<TranslationDocument> getAllDocuments() {
        return translationDocumentRepository.findAll();
    }

}
