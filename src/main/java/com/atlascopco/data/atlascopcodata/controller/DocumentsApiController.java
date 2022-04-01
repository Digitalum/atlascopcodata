/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Column;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Order;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.dto.TranslationDocumentDto;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacet;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class DocumentsApiController {

    @Autowired
    private DefaultDocumentService documentService;


    @RequestMapping(path = "/api2/documents/{token}", method = RequestMethod.POST)
    public @ResponseBody
    Paged<TranslationDocumentDto> getTokensPost(
            @PathVariable String token,
            @RequestBody PagingRequest pagingRequest) {

        SearchRequest searchRequest = SearchHelper.createSearchRequest(pagingRequest, TranslationDocument.class, "code");
        searchRequest.addFilter("tokens", token);

        return documentService.getPagedDocuments(searchRequest);
    }

    @RequestMapping(path = "/api2/documents", method = RequestMethod.POST)
    public @ResponseBody
    Paged<TranslationDocumentDto> getTokensAllPost(
            @RequestBody PagingRequest pagingRequest) {
        SearchRequest searchRequest = SearchHelper.createSearchRequest(pagingRequest, TranslationDocument.class, "code");

        return documentService.getPagedDocuments(searchRequest);
    }



    @GetMapping("/documents/facets")
    public @ResponseBody
    SearchFacet getFacets(Model model) {
        return documentService.getFacets();
    }
}