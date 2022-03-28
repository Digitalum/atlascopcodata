/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchFacet {
    public final String code;
    public final String name;
    public List<SearchFacetValue> facetValues = new ArrayList<>();

}
