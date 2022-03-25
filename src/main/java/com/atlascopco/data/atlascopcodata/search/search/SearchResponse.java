/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse<T> {
    private final List<SearchFacet> facets;
    private final List<T> result;
    private final int resultSize;

}
