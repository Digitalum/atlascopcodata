/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import com.atlascopco.data.atlascopcodata.dao.TokenRepository;
import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.search.DefaultTokenService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DefaultRulesService {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private DefaultTokenService tokenService;

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
        String str = synonymDtoList.stream()
                //     .filter(x -> !x.isGenerated())
                .map(x -> x.getId() + "=" + x.getSynonyms().stream().collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("config/" + fileName));
        writer.write(str);
        writer.close();
    }

    public void importTokens() {
        tokenRepository.saveAll(getTokensFromCsv("WORD.csv", "WORD"));
        tokenRepository.saveAll(getTokensFromCsv("FIXED_NAME.csv", "FIXED_NAME"));
        //tokenRepository.saveAll(getTokensFromCsv("SENTENCE.csv", "SENTENCE"));
    }

    public void exportTokens() {
        try {
            for (Token.TokenType value : Token.TokenType.values()) {
                saveSynonyms(value + ".csv", tokenService.getTokens(value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Token> getTokensFromCsv(String fileName, String type) {
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
                token.setType(Token.TokenType.valueOf(type));
                Map<String, List<String>> m = new HashMap<>();
                if (str.split("=").length == 2) {
                    for (String s : str.split("=")[1].split(",")) {
                        token.getSynonyms().add(s);
                    }
                } else {

                }
                if (StringUtils.isNotEmpty(token.getCode())) {
                    // TODO sort synonums by length synonymDto.getSynonyms().sor
                    synonymDtos.add(token);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return synonymDtos;
    }

}
