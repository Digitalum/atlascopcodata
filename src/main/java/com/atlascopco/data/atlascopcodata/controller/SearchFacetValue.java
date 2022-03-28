/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

public class SearchFacetValue {
    public final String value;
    public final Integer count;


    public SearchFacetValue(String value, Integer count) {
        this.value = value;
        this.count = count;
    }
}
