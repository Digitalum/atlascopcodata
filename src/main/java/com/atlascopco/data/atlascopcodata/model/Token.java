/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import lombok.Data;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Indexed
public class Token implements Comparable {
    private static final String SORT_PREFIX = "sort-";
    private static final String FACET_PREFIX = "facet-";

    @Id
    private String id;

    @Fields({
            @Field(name = "type", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "type",  analyze = Analyze.NO)
    })
    @Column
    private String type; // UNDEFINED, DEFINED, FIXED


    @Fields({
            @Field(name = "synonyms", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "synonyms",  analyze = Analyze.NO)
    })
    @FieldBridge(impl = RulesFieldBridge.class)
    @ElementCollection
    @CollectionTable(name = "TokenSynonyms", joinColumns = @JoinColumn(name = "synonymId"))
    @Column
    private List<String> synonyms = new ArrayList<>();

    @Fields({
            @Field(name = "tokens", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "tokens",  analyze = Analyze.NO)
    })
    @FieldBridge(impl = DocumentFieldBridge.class)
    @ManyToMany(mappedBy = "tokens")
    private List<TranslationDocument> documents = new ArrayList<>();


    public Token() {

    }

    public Token(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public Token(String id) {
        this.id = id;
    }


    public int getCount() {
        return documents.size();
    }

    @Override
    public int compareTo(Object o) {
        return ((KeywordDto) o).getCount() - this.getCount();
    }

}
