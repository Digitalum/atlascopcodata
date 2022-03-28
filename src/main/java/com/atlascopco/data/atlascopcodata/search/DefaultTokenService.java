/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.Paging;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultTokenService {

    @Autowired
    private SearchService searchService;
    @Autowired
    private TokenRepository tokenRepository;

    public List<KeywordDto> getTokens() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(Token.class);
        searchRequest.setEntity(Token.class);

        searchRequest.setPageable(PageRequest.of(0, 5000, Sort.by(new Sort.Order(Sort.Direction.DESC, "documentCount"))));
        final SearchResponse<Token> search = searchService.search(searchRequest);
        final List<Token> tokens = search.getResult();

        List<KeywordDto> keywordDtos = new ArrayList<>();
        for (Token token : tokens) {
            KeywordDto keywordDto = new KeywordDto(token.getId(), String.valueOf(token.getType()));
            keywordDto.setDocumentCount(token.getDocuments().size());
            keywordDto.setSynonyms(token.getSynonyms());
            keywordDtos.add(keywordDto);
        }
        return keywordDtos;
    }

    public Paged<TokenDto> getPagedTokens(SearchRequest searchRequest ) {

        final SearchResponse<Token> search = searchService.search(searchRequest);
        final List<TokenDto> tokendDtos = search.getResult().stream().map(x -> new TokenDto(x)).collect(Collectors.toList());
        final PageImpl<TokenDto> page = new PageImpl<>(tokendDtos, searchRequest.getPageable(), search.getResultSize());

        return new Paged<>(page, Paging.of(page.getTotalPages(), searchRequest.getPageable().getPageNumber(), searchRequest.getPageable().getPageSize()));
    }


    public Token getOrCreateToken(String id) {
        return tokenRepository.findById(id).orElse(new Token(id));
    }


    public List<Token> getTokens(Token.TokenType type) {
        return tokenRepository.findAllByType(type);
    }
}
