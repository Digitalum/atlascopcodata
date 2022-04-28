/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RuleGroupDto {
    private String code;
    private String name;
    private List<DataRuleDto> rules = new ArrayList<>();

}
