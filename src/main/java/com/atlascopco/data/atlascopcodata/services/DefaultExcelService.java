/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.services;

import com.atlascopco.data.atlascopcodata.dao.SynonymTokenRepository;
import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dao.TokenTranslationRepository;
import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.model.TokenTranslation;
import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DefaultExcelService {

    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private SynonymTokenRepository synonymTokenRepository;
    @Autowired
    private TokenTranslationRepository tokenTranslationRepository;


    public void exportTokens(List<Token> list) throws Exception {
        System.out.println("-------------RUN-------------------------------\n");
        Workbook workbook = new XSSFWorkbook();

        createSheet(list, workbook, Token.TokenType.WORD);
        createSheet(list, workbook, Token.TokenType.FIXED_NAME);

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "tokens.xlsx";

        System.out.println(path);
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    private void createSheet(List<Token> list, Workbook workbook, Token.TokenType tokenType) {
        Sheet sheet = workbook.createSheet(tokenType.toString());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell = createCell(headerCell, "Code", header, 1);
        headerCell = createCell(headerCell, "Count", header, 2);
        headerCell = createCell(headerCell, "Type", header, 3);
        headerCell = createCell(headerCell, "Replacements", header, 4);
        headerCell = createCell(headerCell, "Synonym Groups", header, 5);
        headerCell.setCellValue("Translation NL");

        int i = 1;
        for (Token token : list.stream().filter(tokenTypex -> tokenType.equals(tokenTypex.getType())).collect(Collectors.toList())) {
            Row dataRow = sheet.createRow(i);
            headerCell = dataRow.createCell(0);
            headerCell.setCellValue(token.getCode());

            headerCell = dataRow.createCell(1);
            headerCell.setCellValue(token.getCount());

            headerCell = dataRow.createCell(2);
            headerCell.setCellValue(token.getType().toString());

            headerCell = dataRow.createCell(3);
            headerCell.setCellValue(token.getSynonyms().stream().collect(Collectors.joining(",")));

            headerCell = dataRow.createCell(4);
            headerCell.setCellValue(token.getSynonymParents().stream()
                    .map(x -> x.getTokens().stream().map(Token::getCode).collect(Collectors.joining("::")))
                    .collect(Collectors.joining("##")));

            headerCell = dataRow.createCell(5);
            headerCell.setCellValue("TODO");

            i++;
        }
    }

    private Cell createCell(Cell headerCell, String Code, Row header, int i) {
        headerCell.setCellValue(Code);
        headerCell = header.createCell(i);
        return headerCell;
    }

    public void writeExcel(List<TranslationDocument> list) throws Exception {
        final Map<String, String> tokenTranslations = tokenTranslationRepository.findAll().stream()
                .filter(x -> "nl".equals(x.getKey().getLanguage()))
                .collect(Collectors.toMap(x -> x.getKey().getTokenCode(), TokenTranslation::getValue));

        System.out.println("-------------RUN-------------------------------\n");
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Data");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        //headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Partnumber");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Original Value");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Cleaned Value");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Category");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("NL");
        headerCell.setCellStyle(headerStyle);

        int i = 1;
        for (TranslationDocument translationDocument : list) {

            Row dataRow = sheet.createRow(i);
            headerCell = dataRow.createCell(0);
            headerCell.setCellValue(translationDocument.getCode());
            headerCell.setCellStyle(headerStyle);

            headerCell = dataRow.createCell(1);
            headerCell.setCellValue(translationDocument.getOriginal_name());
            headerCell.setCellStyle(headerStyle);

            headerCell = dataRow.createCell(2);
            headerCell.setCellValue(translationDocument.getNew_name());
            headerCell.setCellStyle(headerStyle);

            headerCell = dataRow.createCell(3);
            headerCell.setCellValue(translationDocument.getCategory());
            headerCell.setCellStyle(headerStyle);

            headerCell = dataRow.createCell(4);
            final String translated = translationDocument.getTokens().stream().map(x -> translate(x.getCode(), tokenTranslations)).collect(Collectors.joining(" "));
            headerCell.setCellValue(translated);
            headerCell.setCellStyle(headerStyle);

            i++;
        }


        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        System.out.println(path);
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

    private String translate(String value, Map<String, String> m) {
        if (m.containsKey(value)) {
            return m.get(value);
        }
        return value;
    }


    @Transactional
    public void importTokensTranslationsExcel(InputStream inputStream) throws IOException {

    }

    @Transactional
    public void importTokensExcel(InputStream inputStream) throws IOException {
        //synonymTokenRepository.deleteAll();
        //tokenRepository.deleteAll();
        Workbook workbook = new XSSFWorkbook(inputStream);

        importTokens(workbook);
        importSynonymTokens(workbook);
        importTranslations(workbook);

        System.out.println(translationDocumentRepository.findAll().size());
    }

    private void importTokens(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Token.TokenType tokenType = Token.TokenType.valueOf(sheet.getSheetName());
            int j = 0;
            for (Row row : sheet) {
                j++;
                if (j > 1) {
                    String code = format(row.getCell(0));
                    String count = format(row.getCell(1));
                    String type = format(row.getCell(2));
                    String replacements = format(row.getCell(3));
                    String synonyms = format(row.getCell(4));
                    String translationNl = format(row.getCell(5));
                    if (StringUtils.isNotEmpty(code)) {
                        final Token byCode = tokenRepository.findByCode(code).orElse(new Token(code));
                        byCode.setType(tokenType);
                        byCode.setSynonyms(new ArrayList<>());
                        if (!replacements.isEmpty()) {
                            byCode.getSynonyms().addAll(List.of(replacements.split(",")));
                        }
                        //byCode.setSynonymGroups(tokenType);
                        tokenRepository.save(byCode);
                        System.out.println(j);
                    }
                }
            }
        }
    }

    private void importTranslations(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            int j = 0;
            for (Row row : sheet) {
                j++;
                if (j > 1) {
                    String code = format(row.getCell(0));

                    String translationNl = format(row.getCell(5));
                    if (StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(translationNl)) {
                        TokenTranslation nl = tokenTranslationRepository.findByKey(new TokenTranslation.Key("nl", code)).orElse(new TokenTranslation(code, "nl"));
                        nl.setValue(translationNl);
                        tokenTranslationRepository.save(nl);
                    }
                }
            }
        }
    }

    private void importSynonymTokens(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Token.TokenType tokenType = Token.TokenType.valueOf(sheet.getSheetName());
            int j = 0;
            for (Row row : sheet) {
                j++;
                if (j > 1) {
                    String code = format(row.getCell(0));
                    String count = format(row.getCell(1));
                    String type = format(row.getCell(2));
                    String replacements = format(row.getCell(3));
                    String synonymTokens = format(row.getCell(4));
                    String translationNl = format(row.getCell(5));
                    if (StringUtils.isNotEmpty(synonymTokens) && StringUtils.isNotEmpty(code)) {
                        for (String tokenGroup : Arrays.stream(synonymTokens.split("##")).collect(Collectors.toList())) {
                            String tokenGroupCode = Arrays.stream(tokenGroup.split("::")).collect(Collectors.joining("-"));
                            final SynonymTokenGroup synonymTokenGroup = synonymTokenRepository.findById(tokenGroupCode).orElse(new SynonymTokenGroup(tokenGroupCode));
                            if (synonymTokenGroup.getParent() == null) {
                                final Token parent = tokenRepository.findById(code).orElse(new Token(code));
                                synonymTokenGroup.setParent(parent);
                            }
                            synonymTokenGroup.setTokens(new ArrayList<>());
                            for (String token : tokenGroup.split("::")) {
                                final Token byCode = tokenRepository.findById(token).orElse(new Token(token));
                                synonymTokenGroup.getTokens().add(byCode);
                            }
                            tokenRepository.saveAll(synonymTokenGroup.getTokens());
                            synonymTokenRepository.save(synonymTokenGroup);
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void importProductsExcel(InputStream inputStream) throws IOException {
        translationDocumentRepository.deleteAll();
        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());

            String code = format(row.getCell(1));
            String original_name = format(row.getCell(2));
            String category = format(row.getCell(3));
            String brand = format(row.getCell(4));
            i++;
            if (StringUtils.isNotEmpty(code)) {
                TranslationDocument translationDocument = new TranslationDocument(code, original_name, category, brand);
                translationDocumentRepository.save(translationDocument);
                System.out.println(i);
            }
        }
        System.out.println(translationDocumentRepository.findAll().size());
    }

    private static DecimalFormat df = new DecimalFormat("###");

    public static String format(Cell cell) {
        String result = null;
        if (cell == null) {
            result = "";
        } else if (CellType.STRING.equals(cell.getCellType())) {
            result = cell.getRichStringCellValue().getString().trim();
        } else if (CellType.FORMULA.equals(cell.getCellType())) {
            //CellType cellType = cell.getCellType();
            //if (cellType == CellType.FORMULA) {
            return cell.getCellFormula();
            //}
        } else if (CellType.BOOLEAN.equals(cell.getCellType())) {
            result = cell.getBooleanCellValue() ? "TRUE" : "FALSE";
        } else if (CellType.BLANK.equals(cell.getCellType())) {
            result = "";
        } else if (CellType.NUMERIC.equals(cell.getCellType())) {
            if (cell instanceof XSSFCell) {
                result = ((XSSFCell) cell).getRawValue().trim();
            } else {
                result = String.valueOf(df.format(cell.getNumericCellValue())).trim();
            }
        } else if (CellType.ERROR.equals(cell.getCellType())) {
            return FormulaError.forInt(cell.getErrorCellValue()).getString();
        }
        return result;
    }

}
