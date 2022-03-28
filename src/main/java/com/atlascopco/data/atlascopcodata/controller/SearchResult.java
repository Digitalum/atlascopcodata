/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class SearchResult<T> {
    private final Page<T> page;
    private final List<SearchFacet> facets;
}