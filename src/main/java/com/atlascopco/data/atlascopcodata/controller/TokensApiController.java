/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
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
import com.atlascopco.data.atlascopcodata.search.search.SearchFacet;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
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
        SearchRequest searchRequest = SearchHelper.createSearchRequest(pagingRequest, Token.class, "documentCount");

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
        searchRequest.setQuery(term + "*");

        return tokenService.getPagedTokens(searchRequest).getPage().getContent().stream().map(TokenDto::getCode).collect(Collectors.toList());
    }

    @PostMapping("/tokens/token/delete")
    public @ResponseBody
    void deleteToken(@RequestBody TokenDto tokenDto) throws Exception {
        final Token token1 = tokenService.getOrCreateToken(tokenDto);
        if (token1 != null) {
            final List<TranslationDocument> documentsForKeyword = documentService.getDocumentsTokenUUid(token1.getUuid());

            cleansingService.resetDocuments(documentsForKeyword);
            translationDocumentRepository.saveAll(documentsForKeyword);

            synonymTokenRepository.deleteAll(token1.getSynonymParents());
            synonymTokenRepository.deleteAll(token1.getSynonymGroups());


            cleansingService.executeCleaningRules(documentsForKeyword);
            if (token1.getDocuments().size() == 0) {
                tokenRepository.delete(token1);
            } else {
                token1.setType(Token.TokenType.UNDEFINED);
                tokenRepository.save(token1);
            }
        }
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
        if (Token.TokenType.UNDEFINED.equals(parentToken1.getType()) || Token.TokenType.UNDEFINED_ABBR.equals(parentToken1.getType())) {
            parentToken1.setType(tokenGroupDto.getParent().getType());
        }
        tokenRepository.save(parentToken1);


        synonymTokenGroup.setParent(parentToken1);
        synonymTokenGroup.setTokens(synTokens);
        tokenRepository.saveAll(synTokens);
        synonymTokenRepository.save(synonymTokenGroup);

        final String tokenUuid = synonymTokenGroup.getTokens().get(0).getUuid();
        regenerateForToken(tokenUuid);
    }

    private void regenerateForToken(String uuid) throws Exception {
        final List<TranslationDocument> documentsForKeyword = documentService.getDocumentsTokenUUid(uuid);
        cleansingService.resetDocuments(documentsForKeyword);
        cleansingService.executeCleaningRules(documentsForKeyword);
    }


    @GetMapping("/tokens/facets")
    public @ResponseBody
    SearchFacet getFacets(Model model) {
        return tokenService.getFacets();
    }
}