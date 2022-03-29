/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import lombok.Data;
import org.hibernate.search.annotations.Index;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_sequence")
    @SequenceGenerator(name = "doc_sequence", sequenceName = "doc_sequence", allocationSize = 100)
    public long id;


    @Fields({
            @Field(name = "code", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "code", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "code", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "code")
    @Column
    private String code;


    @Fields({
            @Field(name = "original_name", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "original_name", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "original_name", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "original_name")
    @Column
    private String original_name;

    @Fields({
            @Field(name = "new_name", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "new_name", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "new_name", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "new_name")
    @Column
    private String new_name;

    @Fields({
            @Field(name = "new_name", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "newNameTranslated", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "newNameTranslated", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "newNameTranslated")
    @Column(length = 1024)
    private String newNameTranslated;

    @Transient
    private Map<String, String> mappedValues = new HashMap<>();

    @Fields({
            @Field(name = "category", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "category", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "category", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "category")
    @Column
    private String category;

    @Fields({
            @Field(name = "brand", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "brand", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "brand", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "brand")
    @Column
    private String brand;


    @Fields({
            @Field(name = "tokens", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "tokens", analyze = Analyze.NO)
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
            @Field(name = FACET_PREFIX + "value", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "value", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "value")
    public String getValue() {
        if (new_name != null) {
            return new_name;
        }
        return original_name;
    }

    @Fields({
            @Field(name = "completelyTokenized", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "completelyTokenized", analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "completelyTokenized", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "completelyTokenized")
    public boolean isCompletelyTokenized() {
        return !tokens.stream().anyMatch(x -> !Token.TokenType.FIXED_NAME.equals(x.getType()) && !Token.TokenType.WORD.equals(x.getType()));
    }

    public String getAttr(String name) {
        if (StringUtils.isEmpty(mappedValues.get(name))) {
            return "";
        }
        return mappedValues.get(name);
    }
}