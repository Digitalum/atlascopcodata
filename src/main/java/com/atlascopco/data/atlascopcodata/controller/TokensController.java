/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TokensController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private DefaultCleansingService cleansingService;
    @Autowired
    private DefaultTokenService tokenService;


    @GetMapping("/tokens")
    public String getTokens(Model model,
                            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                            @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
        return "tokens";
    }

    @GetMapping("/tokens/detail/{tokenId}")
    public String getKeywordDetail(Model model, @PathVariable String tokenId) {
        final Token orCreateToken = tokenService.getTokenByUuid(tokenId);
        model.addAttribute("token", orCreateToken);

        return "tokendetail";
    }

    private boolean inprogress = false;


    @PostMapping("/cleandata")
    public ResponseEntity<?> startCleaning() throws Exception {
        try {
            if (!inprogress) {
                inprogress = true;
                cleansingService.resetDocuments();
                cleansingService.executeCleaningRules();
            }
            return ResponseEntity.ok("File uploaded successfully.");
        } finally {
            inprogress = false;
        }
    }

    @PostMapping("/updatetranslations")
    public ResponseEntity<?> updateTranslations() throws Exception {
        try {
            if (!inprogress) {
                inprogress = true;
                cleansingService.executeTranslationRules();
            }
            return ResponseEntity.ok("File uploaded successfully.");
        } finally {
            inprogress = false;
        }
    }
}