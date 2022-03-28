/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TokenGroupDto {

    private List<TokenDto> parent = new ArrayList<>();

}
