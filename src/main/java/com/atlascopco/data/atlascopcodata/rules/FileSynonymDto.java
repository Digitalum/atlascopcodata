/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.rules;

import lombok.Data;

import java.util.List;

@Data
public class FileSynonymDto {
    private String type;
    private boolean revert;
    private String file;
}
