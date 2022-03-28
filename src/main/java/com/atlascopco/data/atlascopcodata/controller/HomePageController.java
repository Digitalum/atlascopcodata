/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.Paged;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.atlascopco.data.atlascopcodata.model.Token.*;

@Controller
public class HomePageController {
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

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }

}