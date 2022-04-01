/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search.search;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SearchRequest {

    @Data
    public static class FacetFilter {
        public final String facetName;
        public final List<String> values;
    }

    private Class<?> entity = TranslationDocument.class;
    private List<FacetFilter> filters = new ArrayList<>();
    private List<FacetFilter> searchFilters = new ArrayList<>();
    private Map<String,FacetFilter> filterMap = new HashMap<>();
    private Map<String,FacetFilter> searchFilterMap = new HashMap<>();
    private Map<String,FacetFilter> permissionFilterMap = new HashMap<>();

    public Pageable pageable;
    public String query;

    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }

    public void addFilter(String field, String value) {
        if (!filterMap.containsKey(field)) {
            filterMap.put(field, new FacetFilter(field, new ArrayList<>()));
            filters.add(filterMap.get(field));
        }
        filterMap.get(field).getValues().add(value);
    }
    public void addSearchFilter(String field, String value) {
        if (!searchFilterMap.containsKey(field)) {
            searchFilterMap.put(field, new FacetFilter(field, new ArrayList<>()));
            searchFilters.add(searchFilterMap.get(field));
        }
        searchFilterMap.get(field).getValues().add(value);
    }

    public void addPermissionFilter(String field, String value) {
        if (!permissionFilterMap.containsKey(field)) {
            permissionFilterMap.put(field, new FacetFilter(field, new ArrayList<>()));
            filters.add(permissionFilterMap.get(field));
        }
        permissionFilterMap.get(field).getValues().add(value);
    }

    public static SearchRequest create() {
        return new SearchRequest();
    }
}
