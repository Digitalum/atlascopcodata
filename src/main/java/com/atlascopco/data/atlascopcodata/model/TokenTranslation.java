/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
public class TokenTranslation {

    @EmbeddedId()
    private Key key;

    @Column
    private String value;


    public TokenTranslation() {

    }

    public TokenTranslation(String tokenCode, String language) {
        this.key = new Key(tokenCode, language);
    }

    @Data
    @Embeddable
    public static class Key implements Serializable {
        @Column(nullable = false)
        private String tokenCode;
        @Column(nullable = false)
        private String language;

        protected Key() {
            // for hibernate
        }

        public Key(String tokenCode, String language) {
            this.tokenCode = tokenCode;
            this.language = language;
        }
    }
}
