/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import lombok.Data;
import org.hibernate.search.annotations.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Indexed
public class TranslationDocument {
    private static final String SORT_PREFIX = "sort-";
    private static final String FACET_PREFIX = "facet-";

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    // @Id
    // private String id;

    @Fields({
            @Field(name = "code", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "code",  analyze = Analyze.NO)
    })
    @Column
    private String code;


    @Fields({
            @Field(name = "originalName", analyze = Analyze.YES)
    })
    @Column
    private String original_name;

    @Fields({
            @Field(name = "newName", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "code",  analyze = Analyze.NO)
    })
    @Column
    private String new_name;

    @Transient
    private Map<String, String> mappedValues = new HashMap<>();

    @Fields({
            @Field(name = "category", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "category",  analyze = Analyze.NO)
    })
    @Column
    private String category;

    @Fields({
            @Field(name = "brand", analyze = Analyze.NO)
    })
    @Column
    private String brand;


    @Fields({
            @Field(name = "tokens", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "tokens",  analyze = Analyze.NO)
    })
    @FieldBridge(impl = TokenFieldBridge.class)
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Doc_Token",
            joinColumns = {@JoinColumn(name = "doc_id")},
            inverseJoinColumns = {@JoinColumn(name = "token_id")
            }
    )
    private List<Token> tokens = new ArrayList<>();


    @FieldBridge(impl = RulesFieldBridge.class)
    @ElementCollection
    @CollectionTable(name = "rules", joinColumns = @JoinColumn(name = "change_id"))
    @Column
    private List<String> changes = new ArrayList<>();


    public TranslationDocument() {
    }

    public TranslationDocument(String code, String original_name, String category, String brand) {
        this.code = code;
        this.original_name = original_name;
        this.category = category;
        this.brand = brand;
    }


    @Fields({
            @Field(name = "value", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "value",  analyze = Analyze.NO)
    })
    public String getValue() {
        if (new_name != null) {
            return new_name;
        }
        return original_name;
    }

    public String getAttr(String name) {
        if (StringUtils.isEmpty(mappedValues.get(name))) {
            return "";
        }
        return mappedValues.get(name);
    }
}