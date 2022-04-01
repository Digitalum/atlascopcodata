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
import java.util.UUID;

@Entity
@Data
//@Table(indexes = @javax.persistence.Index(name = "code_idx", columnList = "code", unique = true))
@Indexed
public class Token implements Comparable {
    private static final String SORT_PREFIX = "sort-";
    private static final String FACET_PREFIX = "facet-";

    public enum TokenType {
        WORD,
        WORD_NOT_TRANSLATABLE,
        FIXED_NAME,
        UNDEFINED,
        UNDEFINED_ABBR
    }

    @Fields({
            @Field(name = "uuid", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "uuid",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "uuid", index = Index.NO, analyze = Analyze.NO)
    })
    @Facet(forField = FACET_PREFIX + "uuid")
    @SortableField(forField = SORT_PREFIX + "uuid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="token_sequence")
    @SequenceGenerator(name="token_sequence", sequenceName = "token_sequence", allocationSize = 100)
    @Column
    private String uuid;

    @Fields({
            @Field(name = "code2", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "code",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "code", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "code")
    @Column
    @Id
    private String code;

    @FieldBridge(impl = TokenTypeFieldBridge.class)
    @Fields({
            @Field(name = "type", analyze = Analyze.YES),
            @Field(name = SORT_PREFIX + "type", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "type")
    @Column
    private TokenType type; // UNDEFINED, DEFINED, FIXED

    @Fields({
            @Field(name = FACET_PREFIX + "type",  analyze = Analyze.NO)
    })
    @Facet(forField = FACET_PREFIX + "type")
    private String getType2() {
        if (TokenType.UNDEFINED.equals(type) || TokenType.UNDEFINED_ABBR.equals(type)) {
            if (getDocumentCount() == 0) {
                return "OTHER";
            }
        }
        return type.toString();
    }


    @Fields({
            @Field(name = "synonyms", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "synonyms",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "synonyms", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "synonyms")
    @FieldBridge(impl = RulesFieldBridge.class)
    @ElementCollection
    @CollectionTable(name = "TokenSynonyms", joinColumns = @JoinColumn(name = "synonymId"))
    @Column
    private List<String> synonyms = new ArrayList<>();


    @ManyToMany(mappedBy = "tokens")
    private List<TranslationDocument> documents = new ArrayList<>();



    @Fields({
            @Field(name = "synonymParents", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "synonymParents",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "synonymParents", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "synonymParents")
    @FieldBridge(impl = SynonymTokenGroupFieldBridge.class)
    @OneToMany(mappedBy="parent" )
    private List<SynonymTokenGroup> synonymParents = new ArrayList<>();

    @Fields({
            @Field(name = "synonymGroups", analyze = Analyze.YES),
            @Field(name = FACET_PREFIX + "synonymGroups",  analyze = Analyze.NO),
            @Field(name = SORT_PREFIX + "synonymGroups", index = Index.NO, analyze = Analyze.NO)
    })
    @SortableField(forField = SORT_PREFIX + "synonymGroups")
    @FieldBridge(impl = SynonymTokenGroupFieldBridge.class)
    @ManyToMany(mappedBy = "tokens")
    private List<SynonymTokenGroup> synonymGroups = new ArrayList<>();


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
        this.uuid = UUID.randomUUID().toString();
    }

    public Token(String code, TokenType type) {
        this.uuid = UUID.randomUUID().toString();
        this.code = code;
        this.type = type;
    }

    public Token(String code) {
        this.uuid = UUID.randomUUID().toString();
        this.code = code;
        this.type = TokenType.UNDEFINED;
    }


    public int getCount() {
        return documents.size();
    }

    @Override
    public int compareTo(Object o) {
        return ((KeywordDto) o).getCount() - this.getCount();
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
