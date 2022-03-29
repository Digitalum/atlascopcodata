/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Order;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.dto.TranslationDocumentDto;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacet;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.query.facet.Facet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentsApiController {

    @Autowired
    private DefaultDocumentService documentService;


    @RequestMapping(path = "/api2/documents/{token}", method = RequestMethod.POST)
    public @ResponseBody
    Paged<TranslationDocumentDto> getTokensPost(
            @PathVariable String token,
            @RequestBody PagingRequest pagingRequest) {
        final int size = pagingRequest.getLength();
        final int pageNumber = pagingRequest.getStart() / size;

        Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, "code");
        for (Order order : pagingRequest.getOrder()) {
            final String fieldName = pagingRequest.getColumns().get(order.getColumn()).getData();
            defaultOrder = new Sort.Order(Sort.Direction.fromString(order.getDir().toString()), fieldName);
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageable(PageRequest.of(pageNumber, size, Sort.by(defaultOrder)));
        searchRequest.setEntity(TranslationDocument.class);

        if (StringUtils.isNotEmpty(pagingRequest.getSearch().getValue())) {
            searchRequest.setQuery(pagingRequest.getSearch().getValue());
        }
        searchRequest.addFilter("tokens", token);

        return documentService.getPagedDocuments(searchRequest);
    }

    @RequestMapping(path = "/api2/documents", method = RequestMethod.POST)
    public @ResponseBody
    Paged<TranslationDocumentDto> getTokensAllPost(
            @RequestBody PagingRequest pagingRequest) {
        final int size = pagingRequest.getLength();
        final int pageNumber = pagingRequest.getStart() / size;

        Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, "code");
        for (Order order : pagingRequest.getOrder()) {
            final String fieldName = pagingRequest.getColumns().get(order.getColumn()).getData();
            defaultOrder = new Sort.Order(Sort.Direction.fromString(order.getDir().toString()), fieldName);
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageable(PageRequest.of(pageNumber, size, Sort.by(defaultOrder)));
        searchRequest.setEntity(TranslationDocument.class);

        if (StringUtils.isNotEmpty(pagingRequest.getSearch().getValue())) {
            searchRequest.setQuery(pagingRequest.getSearch().getValue());
        }

        return documentService.getPagedDocuments(searchRequest);
    }

    @GetMapping("/documents/facets")
    public @ResponseBody
    SearchFacet getFacets(Model model) {
        return documentService.getFacets();
    }
}