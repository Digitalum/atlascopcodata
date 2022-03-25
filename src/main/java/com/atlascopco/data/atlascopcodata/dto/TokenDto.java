/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
public class TokenDto {
    private String id;
    private String type; // UNDEFINED, DEFINED, FIXED
    private List<String> synonyms = new ArrayList<>();
}
