/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import lombok.Data;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Indexed
public class SynonymTokenGroup {
    private static final String SORT_PREFIX = "sort-";
    private static final String FACET_PREFIX = "facet-";

    public SynonymTokenGroup() {

    }
    public SynonymTokenGroup(String code) {
        this.code = code;
    }

    @Column
    @Id
    private String code;

    @ManyToOne
    @JoinColumn(name="id", nullable=false)
    private Token parent;


    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "syntokens",
            joinColumns = {@JoinColumn(name = "token_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")
            }
    )
    @Column
    private List<Token> tokens = new ArrayList<>();

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}