/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.services.DefaultCleansingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TokensController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private DefaultCleansingService cleansingService;
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


}