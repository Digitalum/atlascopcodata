/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import com.atlascopco.data.atlascopcodata.dto.TokenDto;
import com.atlascopco.data.atlascopcodata.dto.TranslationDocumentDto;
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

import static com.atlascopco.data.atlascopcodata.model.Token.TokenType;

@Controller
public class DocumentsController {
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


    @GetMapping("/documents")
    public String getKeywordDetail(Model model) {

        return "documents";
    }


    @GetMapping("/tokens/detail/{token}")
    public String getKeywordDetail(Model model, @PathVariable String token,
                                   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                   @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
        final List<TranslationDocument> documents = documentService.getDocumentsForKeyword(token);
        final Token orCreateToken = tokenService.getOrCreateToken(token);
        model.addAttribute("token", orCreateToken);

        return "tokendetail";
    }
}