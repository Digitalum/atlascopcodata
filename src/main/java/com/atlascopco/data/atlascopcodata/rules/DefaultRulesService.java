/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultRulesService {

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
}
