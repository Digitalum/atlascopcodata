/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller;

import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Column;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Order;
import com.atlascopco.data.atlascopcodata.controller.paging.datatable.PagingRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class SearchHelper {

    public static SearchRequest createSearchRequest(PagingRequest pagingRequest, Class entity, String defaultOrderCode) {
        Sort.Order defaultOrder = new Sort.Order(Sort.Direction.DESC, defaultOrderCode);
        for (Order order : pagingRequest.getOrder()) {
            final String fieldName = pagingRequest.getColumns().get(order.getColumn()).getData();
            defaultOrder = new Sort.Order(Sort.Direction.fromString(order.getDir().toString()), fieldName);
        }


        final int size = pagingRequest.getLength();
        final int pageNumber = pagingRequest.getStart() / size;
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageable(PageRequest.of(pageNumber, size, Sort.by(defaultOrder)));
        searchRequest.setEntity(entity);

        if (StringUtils.isNotEmpty(pagingRequest.getSearch().getValue())) {
            searchRequest.setQuery(pagingRequest.getSearch().getValue());
        }

        for (Column column : pagingRequest.getColumns()) {
            if (column.getSearch() != null && StringUtils.isNotEmpty(column.getSearch().getValue())) {
                final String value = column.getSearch().getValue();
                final String fieldId = column.getData();
                searchRequest.addSearchFilter(fieldId, value);
            }
        }
        return searchRequest;
    }
}
