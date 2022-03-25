/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import lombok.Data;

import java.util.List;

@Data
public class DataRuleDto {
    private String code;
    private String name;
    private String trigger;
    private String strategy;
    private String regex;
    private String mapping;
    private String template;
    private String file;
    private List<FileSynonymDto> files;

}
