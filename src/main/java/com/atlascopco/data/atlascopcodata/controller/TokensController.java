/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TokensController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private DefaultCleansingService cleansingService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private DefaultDocumentService documentService;
    @Autowired
    private DefaultTokenService tokenService;
    @Autowired
    private DefaultRulesService rulesService;


    @GetMapping("/tokens")
    public String getTokens(Model model,
                            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                            @RequestParam(value = "size", required = false, defaultValue = "100") int size) {


        return "tokens";
    }


    private boolean inprogress = false;


    @PostMapping("/cleandata")
    public ResponseEntity<?> startCleaning() throws Exception {
        if (!inprogress) {
            inprogress = true;
            cleansingService.resetDocuments();
            cleansingService.executeCleaningRules();
        }
        return ResponseEntity.ok("File uploaded successfully.");
    }

    @PostMapping("/exportTokens")
    public ResponseEntity<?> exportTokens() {
        rulesService.exportTokens();
        return ResponseEntity.ok("File uploaded successfully.");
    }




    @GetMapping("/keywords/{token}")
    public @ResponseBody
    KeywordDto getKeywords(Model model, @PathVariable String token) {
        // TODO FIX
        return tokenService.getTokens().stream().filter(x -> x.getKey().equals(token)).findFirst().get();
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