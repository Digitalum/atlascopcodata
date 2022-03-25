/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AnalysisController {
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

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "index2";
    }

    @GetMapping("/keywords")
    public String getKeywords(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("keywords", tokenService.getTokens());
        return "keywords";
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


    @GetMapping("/keywords/detail/{token}")
    public String getKeywordDetail(Model model, @PathVariable String token) {
        final List<TranslationDocument> documents = documentService.getDocumentsForKeyword(token);
        model.addAttribute("documents", documents);
        return "keyworddetail";
    }

    @GetMapping("/keywords/{token}")
    public @ResponseBody
    KeywordDto getKeywords(Model model, @PathVariable String token) {
        // TODO FIX
        return tokenService.getTokens().stream().filter(x -> x.getKey().equals(token)).findFirst().get();
    }


    @PostMapping("/keywords/token")
    public @ResponseBody
    void addSentence(Model model, @RequestBody TokenDto tokenDto) {
        final Token token1 = tokenService.getOrCreateToken(tokenDto.getId());
        token1.setType(tokenDto.getType());
        tokenRepository.save(token1);
    }
}