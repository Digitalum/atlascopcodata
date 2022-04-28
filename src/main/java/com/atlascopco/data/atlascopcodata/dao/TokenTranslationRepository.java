/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dao;

import com.atlascopco.data.atlascopcodata.model.TokenTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenTranslationRepository extends JpaRepository<TokenTranslation, TokenTranslation.Key> {

    Optional<TokenTranslation> findByKey(TokenTranslation.Key key);

    List<TokenTranslation> findAllByKeyLanguage(String language);
}
