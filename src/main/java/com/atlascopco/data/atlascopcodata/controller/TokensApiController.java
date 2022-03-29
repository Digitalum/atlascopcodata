/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Order;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.dao.SynonymTokenRepository;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.dto.SynonymTokenGroupDto;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TokensApiController {

    @Autowired
    private DefaultTokenService tokenService;
    @Autowired
    private DefaultCleansingService cleansingService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private SynonymTokenRepository synonymTokenRepository;
    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;
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

    @PostMapping("/tokens/token")
    public @ResponseBody
    void addSentence(Model model, @RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto);
        token1.setType(tokenDto.getType());
        tokenRepository.save(token1);
        regenerateForToken(tokenDto.getUuid());
    }

    @PostMapping("/keywords/synonym")
    public @ResponseBody
    void addSynonym(@RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto);
        token1.getSynonyms().addAll(tokenDto.getSynonyms());
        tokenRepository.save(token1);
    }

    @GetMapping("/tokens/suggest")
    public @ResponseBody
    List<String> addSynonym(@RequestParam String term) {
        final int size = 20;
        final int pageNumber = 0;
        Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, "documentCount");
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageable(PageRequest.of(pageNumber, size, Sort.by(defaultOrder)));
        searchRequest.setEntity(Token.class);
        searchRequest.setQuery(term);

        return tokenService.getPagedTokens(searchRequest).getPage().getContent().stream().map(TokenDto::getCode).collect(Collectors.toList());
    }

    @PostMapping("/tokens/token/delete")
    public @ResponseBody
    void deleteToken(@RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto);
        final List<TranslationDocument> documentsForKeyword = documentService.getDocumentsForKeyword(token1.getCode());

        cleansingService.resetDocuments(documentsForKeyword);
        translationDocumentRepository.saveAll(documentsForKeyword);

        synonymTokenRepository.deleteAll(token1.getSynonymParents());
        synonymTokenRepository.deleteAll(token1.getSynonymGroups());
        tokenRepository.delete(token1);


        cleansingService.executeCleaningRules(documentsForKeyword);
    }


    @PostMapping("/document/update")
    public @ResponseBody
    void updateDocument(@RequestBody TranslationDocument translationDocument) throws Exception {
        final TranslationDocument documentForCode = documentService.getDocumentForCode(translationDocument.getCode());
        documentForCode.setOriginal_name(translationDocument.getOriginal_name());
        translationDocumentRepository.save(documentForCode);

        cleansingService.resetDocuments(Collections.singletonList(documentForCode));
        cleansingService.executeCleaningRules(Collections.singletonList(documentForCode));
    }


    @PostMapping("/tokens/tokengroup/add")
    public @ResponseBody
    void addTokenGroup(@RequestBody SynonymTokenGroupDto tokenGroupDto) throws Exception {
        String tokenGroupCode = tokenGroupDto.getTokens().stream().map(TokenDto::getCode).collect(Collectors.joining("-"));
        tokenGroupCode = tokenGroupCode.toUpperCase();
        final SynonymTokenGroup synonymTokenGroup = synonymTokenRepository.findById(tokenGroupCode).orElse(new SynonymTokenGroup(tokenGroupCode));

        final Token parentToken1 = tokenService.getOrCreateToken(tokenGroupDto.getParent());
        final List<Token> synTokens = tokenGroupDto.getTokens().stream().map(x -> tokenService.getOrCreateToken(x)).collect(Collectors.toList());
        synonymTokenGroup.setParent(parentToken1);
        synonymTokenGroup.setTokens(synTokens);
        if (parentToken1.getType() == null) {
            parentToken1.setType(Token.TokenType.valueOf(tokenGroupDto.getParentType()));
        }
        tokenRepository.save(parentToken1);
        tokenRepository.saveAll(synTokens);
        synonymTokenRepository.save(synonymTokenGroup);

        final String tokenUuid = synonymTokenGroup.getTokens().get(0).getUuid();
        regenerateForToken(tokenUuid);
    }

    private void regenerateForToken(String uuid) throws Exception {
        final List<TranslationDocument> documentsForKeyword = documentService.getDocumentsForKeyword(uuid);
        cleansingService.resetDocuments(documentsForKeyword);
        cleansingService.executeCleaningRules(documentsForKeyword);
    }
}