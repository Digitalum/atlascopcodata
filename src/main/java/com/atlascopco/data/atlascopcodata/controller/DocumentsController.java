/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentsController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private DefaultTokenService tokenService;


    @GetMapping("/documents")
    public String getKeywordDetail(Model model) {

        return "documents";
    }


}