/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.services;

import com.atlascopco.data.atlascopcodata.dao.TranslationDocumentRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DefaultExcelService {

    @Autowired
    private TranslationDocumentRepository translationDocumentRepository;

    public Map<String, String> getTokenTranslations() {
        InputStreamReader isReader = null;
        Map<String, String> m = new HashMap<>();
        try {
            isReader = new InputStreamReader(new FileInputStream("config/TRANSLATIONS_NL.csv"));
            //Creating a BufferedReader object
            BufferedReader reader = new BufferedReader(isReader);
            String str;
            while ((str = reader.readLine()) != null) {
                if (str.split("=").length == 2) {
                    m.put(str.split("=")[0], str.split("=")[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return m;
    }

    public void writeExcel(List<TranslationDocument> list) throws Exception {
        final Map<String, String> tokenTranslations = getTokenTranslations();
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
            final String translated = translationDocument.getTokens().stream().map(x -> translate(x.getId(), tokenTranslations)).collect(Collectors.joining(" "));
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
    public void loadExcel(InputStream inputStream) throws IOException {
        translationDocumentRepository.deleteAll();

        List<TranslationDocument> items = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());

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

    public void loadExcel() throws IOException {
        loadExcel(new FileInputStream(new File("/home/jeroen/projects/ac_data_cleaning/atlascopcodata/export_.xlsx")));
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
