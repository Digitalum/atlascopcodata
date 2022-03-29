/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.rules.DefaultRulesService;
import com.atlascopco.data.atlascopcodata.search.DefaultDocumentService;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.atlascopco.data.atlascopcodata.services.DefaultExcelService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

@Log4j2
@Controller
public class UploadController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private DefaultExcelService excelService;
    @Autowired
    private DefaultDocumentService documentService;
    @Autowired
    private DefaultTokenService tokenService;


    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            excelService.importProductsExcel(inputStream);
            file.transferTo(new File("C:\\upload\\" + fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("File uploaded successfully.");
    }

    @PostMapping("/download")
    public ResponseEntity<?> download() throws Exception {
        excelService.writeExcel(documentService.getAllDocuments());
        return ResponseEntity.ok("File uploaded successfully.");
    }


    @PostMapping("/tokens/export")
    public ResponseEntity<?> exportTokens() throws Exception {
        excelService.exportTokens(tokenService.getAllTokens());
        return ResponseEntity.ok("File uploaded successfully.");
    }

    @PostMapping("/tokens2/import")
    public ResponseEntity<?> importTokens() throws Exception {
        return ResponseEntity.ok("File uploaded successfully.");
    }


    @PostMapping("/tokens/import")
    public ResponseEntity<?> importTokens(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            excelService.importTokensExcel(inputStream);
            file.transferTo(new File("C:\\upload\\" + fileName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("File uploaded successfully.");
    }

    @PostMapping("/tokens/translations/import")
    public ResponseEntity<?> importTranslationsTokens(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            excelService.importTokensExcel(inputStream);
            file.transferTo(new File("C:\\upload\\" + fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("File uploaded successfully.");
    }
}