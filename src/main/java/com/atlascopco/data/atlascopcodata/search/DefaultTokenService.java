/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search;

import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultTokenService {

    @Autowired
    private SearchService searchService;
    @Autowired
    private TokenRepository tokenRepository;

    public List<KeywordDto> getTokens() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(Token.class);
        searchRequest.setPageable(PageRequest.of(0, 5000));
        final SearchResponse<Token> search = searchService.search(searchRequest);
        final List<Token> tokens = search.getResult();

        List<KeywordDto> keywordDtos = new ArrayList<>();
        for (Token token : tokens) {
            KeywordDto keywordDto = new KeywordDto(token.getId(), token.getType());
            keywordDtos.add(keywordDto);
        }
        return keywordDtos;
    }


    public Token getOrCreateToken(String id) {
        return tokenRepository.findById(id).orElse(new Token(id));
    }


    public List<Token> getTokens(String type) {
        return tokenRepository.findAllByType(type);
    }
}
