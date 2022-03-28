/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.search;


import com.atlascopco.data.atlascopcodata.model.Token;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacet;
import com.atlascopco.data.atlascopcodata.search.search.SearchFacetValue;
import com.atlascopco.data.atlascopcodata.search.search.SearchRequest;
import com.atlascopco.data.atlascopcodata.search.search.SearchResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.FacetingRequest;
import org.hibernate.search.query.hibernate.impl.FullTextQueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Transactional
@Service
public class SearchService implements ApplicationListener<ApplicationReadyEvent> {

    private final EntityManager entityManager;

    @Value("${ccp.lucene.index.on.startup:true}")
    private boolean luceneIndexOnstartUp;

    @Autowired
    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void initData() throws InterruptedException {
        Search.getFullTextEntityManager(entityManager).createIndexer().startAndWait();
    }

    public SearchResponse search(SearchRequest request) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(request.getEntity()).get();
        BooleanJunction bool = queryBuilder.bool().must(queryBuilder.all().createQuery());
        bool = addFreeTextSearch(request, queryBuilder, bool);

        for (SearchRequest.FacetFilter filter : request.getFilters()) {
            BooleanJunction bool2 = queryBuilder.bool().minimumShouldMatchNumber(1);
            for (String value : filter.getValues()) {
                Query categoryQuery = queryBuilder.keyword().wildcard().onField("facet-" + filter.getFacetName()).matching(value.toLowerCase()).createQuery();
                bool2 = bool2.should(categoryQuery);
            }
            bool = bool.must(bool2.createQuery());
        }

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(bool.createQuery(), request.getEntity());
        fullTextQuery.setFirstResult(request.getPageable().getPageNumber() * request.getPageable().getPageSize());
        fullTextQuery.setMaxResults(request.getPageable().getPageSize());

        List<SearchFacet> searchFacets = new ArrayList<>();//addFacets(queryBuilder, fullTextQuery);

        addSorts(request, fullTextQuery);

        log.info(((FullTextQueryImpl) fullTextQuery).getQueryString());
        final SearchResponse response = new SearchResponse(searchFacets, fullTextQuery.getResultList(), fullTextQuery.getResultSize());

        return response;
    }

    private BooleanJunction addFreeTextSearch(SearchRequest request, QueryBuilder queryBuilder, BooleanJunction bool) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(request.getQuery())) {
            return bool;
        }
        if (Token.class.equals(request.getEntity())) {
            if (!StringUtils.isEmpty(request.getQuery())) {
                Query query = queryBuilder
                        .simpleQueryString()
                        .onFields("code")
                        .boostedTo(2f).matching(request.getQuery().toLowerCase()).createQuery();
                bool = bool.must(query);
            }
            return bool;
        } else {
            Query query = queryBuilder
                    .simpleQueryString()
                    .onFields("code", "new_name", "old_name")
                    .boostedTo(2f).matching(request.getQuery().toLowerCase()).createQuery();
            bool = bool.must(query);
            return bool;
        }
    }

    public FacetManager searchFacetsForPgc(SearchRequest request, String pgc) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(request.getEntity()).get();
        BooleanJunction bool = queryBuilder.bool().must(queryBuilder.all().createQuery());

        if (!StringUtils.isEmpty(pgc)) {
            bool = bool.must(queryBuilder.keyword().onField("pgc").matching(pgc).createQuery());
        }

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(bool.createQuery(), request.getEntity());
        fullTextQuery.setFirstResult(0);
        fullTextQuery.setMaxResults(0);
        addFacets(queryBuilder, fullTextQuery, new String[]{"pgc", "gac", "brand", "factory",
                "division", "businessline", "range", "rg", "emissionregulation", "commercialfamily", "modelname"});

        log.info(((FullTextQueryImpl) fullTextQuery).getQueryString());

        return fullTextQuery.getFacetManager();
    }

    private void addSorts(SearchRequest request, FullTextQuery fullTextQuery) {
        final org.springframework.data.domain.Sort sorts = request.getPageable().getSort();
        for (org.springframework.data.domain.Sort.Order order : sorts) {
            if ("pk".equals(order.getProperty())) {
                Sort sortField = new Sort(new SortField("sort-" + order.getProperty(), SortField.Type.LONG, order.isAscending()));
                fullTextQuery.setSort(sortField);
            } else if (order.getProperty().contains("Count")) {
                Sort sortField = new Sort(new SortField("sort-" + order.getProperty(), SortField.Type.INT, order.isDescending()));
                fullTextQuery.setSort(sortField);
            } else {
                Sort sortField = new Sort(new SortField("sort-" + order.getProperty(), SortField.Type.STRING, order.isAscending()));
                fullTextQuery.setSort(sortField);
            }
        }
    }

    private List<SearchFacet> addFacets(QueryBuilder queryBuilder, FullTextQuery fullTextQuery, String[] facets) {
        List<SearchFacet> searchFacets = new ArrayList<>();
        for (String facet : facets) {
            FacetingRequest gacFacetRequest = queryBuilder.facet().name(facet).onField("facet-" + facet).discrete().maxFacetCount(1000).createFacetingRequest();
            fullTextQuery.getFacetManager().enableFaceting(gacFacetRequest);
            List<SearchFacetValue> facetResult = fullTextQuery.getFacetManager().getFacets(facet)
                    .stream().map(f -> new SearchFacetValue(f.getValue(), f.getCount())).collect(Collectors.toList());
            SearchFacet searchFacet = new SearchFacet(facet, facet);
            searchFacet.getFacetValues().addAll(facetResult);
            searchFacets.add(searchFacet);
        }
        return searchFacets;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (luceneIndexOnstartUp) {
            log.info("Lucene index on startup enabled.");
            try {
                initData();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.info("Lucene index on startup disabled.");
        }
    }
}