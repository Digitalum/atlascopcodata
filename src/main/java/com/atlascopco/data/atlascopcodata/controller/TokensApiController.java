/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Order;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TokensApiController {

    @Autowired
    private DefaultTokenService tokenService;
    @Autowired
    private DefaultCleansingService cleansingService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private DefaultDocumentService documentService;


    @RequestMapping(path = "/api2/tokens", method = RequestMethod.POST)
    public @ResponseBody
    Paged<TokenDto> getTokensPost(
            @RequestBody PagingRequest pagingRequest) {
        final int size = pagingRequest.getLength();
        final int pageNumber = pagingRequest.getStart() / size;

        Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, "documentCount");
        for (Order order : pagingRequest.getOrder()) {
            final String fieldName = pagingRequest.getColumns().get(order.getColumn()).getData();
            defaultOrder = new Sort.Order(Sort.Direction.fromString(order.getDir().toString()), fieldName);
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageable(PageRequest.of(pageNumber, size, Sort.by(defaultOrder)));
        searchRequest.setEntity(Token.class);

        if (StringUtils.isNotEmpty(pagingRequest.getSearch().getValue())) {
            searchRequest.setQuery(pagingRequest.getSearch().getValue());
        }

        return tokenService.getPagedTokens(searchRequest);
    }

    @RequestMapping(value = "/api/token/{id}", method = RequestMethod.GET)
    public @ResponseBody
    TokenDto getTokenById(@PathVariable("id") String id) {
        return new TokenDto(tokenService.getOrCreateToken(id));
    }


    @PostMapping("/keywords/token")
    public @ResponseBody
    void addSentence(Model model, @RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto.getId());
        token1.setType(tokenDto.getType());
        tokenRepository.save(token1);

        final String s = token1.getId().split(" ")[0];
        final List<TranslationDocument> documentsForKeyword = documentService.getDocumentsForKeyword(s);
        cleansingService.resetDocuments(documentsForKeyword);
        cleansingService.executeCleaningRules(documentsForKeyword);
    }

    @PostMapping("/keywords/synonym")
    public @ResponseBody
    void addSynonym(@RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto.getId().trim().toUpperCase());
        token1.getSynonyms().addAll(tokenDto.getSynonyms());
        tokenRepository.save(token1);

    }
}