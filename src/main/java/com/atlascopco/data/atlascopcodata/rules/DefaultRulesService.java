/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.dto.SynonymDto;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DefaultRulesService {

    @Autowired
    private TokenRepository tokenRepository;

    public List<RuleGroupDto> getRules() throws IOException {
        InputStreamReader isReader = new InputStreamReader(new FileInputStream("config/rules.json"));
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        String json = sb.toString();
        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<RuleGroupDto>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public void saveSynonyms(String fileName, List<Token> synonymDtoList) throws IOException {
        //String fileName = "keywords.csv";
        String str = synonymDtoList.stream()
           //     .filter(x -> !x.isGenerated())
                .map(x -> x.getId() + "=" + x.getSynonyms().stream().collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("config/" + fileName));
        writer.write(str);
        writer.close();
    }

    public List<Token> getKeywords() {
        return tokenRepository.findAllByType("WORD");
    }

    public List<Token> getSentences() {
        return tokenRepository.findAllByType("SENTENCE");
    }

    public List<Token> getFixedNames() {
        return tokenRepository.findAllByType("FIXED_NAME");
    }

    public void importTokens() {
        tokenRepository.saveAll(getTokensFromCsv("keywords.csv"));
        tokenRepository.saveAll(getTokensFromCsv("fixed_names.csv"));
        tokenRepository.saveAll(getTokensFromCsv("sentences.csv"));
        // TODO add reverse synonyms
    }

    public List<Token> getTokensFromCsv(String fileName) {
        InputStreamReader isReader = null;
        List<Token> synonymDtos = new ArrayList<>();
        try {
            isReader = new InputStreamReader(new FileInputStream("config/" + fileName));

            //Creating a BufferedReader object
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                Token token = new Token(str.split("=")[0]);
                Map<String, List<String>> m = new HashMap<>();
                if (str.split("=").length == 2) {
                    for (String s : str.split("=")[1].split(",")) {
                        token.getSynonyms().add(s);
                    }
                } else {

                }
                // TODO sort synonums by length synonymDto.getSynonyms().sor
                synonymDtos.add(token);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return synonymDtos;
    }

    public void saveFixedNames(List<Token> synonymDtoList) throws IOException {
        saveSynonyms("fixed_names.csv", synonymDtoList);
    }


    public void saveSentences(List<Token> synonymDtoList) throws IOException {
        saveSentences("sentences.csv", synonymDtoList);
    }

    private void saveSentences(String fileName, List<Token> synonymDtoList) throws IOException {
        String str = synonymDtoList.stream().map(Token::getId).collect(Collectors.joining("\n"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("config/" + fileName));
        writer.write(str);
        writer.close();

    }


}
