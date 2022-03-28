/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import com.atlascopco.data.atlascopcodata.dto.KeywordDto;
import lombok.Data;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Indexed
public class Token implements Comparable {
    private static final String SORT_PREFIX = "sort-";
    private static final String FACET_PREFIX = "facet-";

    public enum TokenType {
        WORD,
        WORD_NOT_TRANSLATABLE,
        SENTENCE,
        FIXED_NAME,
        UNDEFINED,
        UNDEFINED_ABBR
    }

    @Fields({
            @Field(name = "code", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "id",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "id", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "id")
    @Column
    @Id
    private String id;

    @Fields({
            @Field(name = "type", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "type",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "type", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "type")
    @Column
    private TokenType type; // UNDEFINED, DEFINED, FIXED


    @Fields({
            @Field(name = "synonyms", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "synonyms",  analyze = Analyze.NO)
    })
    @FieldBridge(impl = RulesFieldBridge.class)
    @ElementCollection
    @CollectionTable(name = "TokenSynonyms", joinColumns = @JoinColumn(name = "synonymId"))
    @Column
    private List<String> synonyms = new ArrayList<>();


    @ManyToMany(mappedBy = "tokens")
    private List<TranslationDocument> documents = new ArrayList<>();

    @Fields({
            @Field(name = "documentCount", analyze = Analyze.NO),
            @Field(name = FACET_PREFIX + "documentCount",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "documentCount", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "documentCount")
    public int getDocumentCount() {
        return  documents.size();
    }

    public Token() {

    }

    public Token(String id, TokenType type) {
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
