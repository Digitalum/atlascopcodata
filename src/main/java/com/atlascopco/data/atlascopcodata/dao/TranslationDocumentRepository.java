/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dao;

import com.atlascopco.data.atlascopcodata.model.TranslationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationDocumentRepository extends JpaRepository<TranslationDocument, String> {

    @Query(value = "SELECT c FROM TranslationDocument AS c")
    List<TranslationDocument> findAllWhereNewStatusIsNull();

    Optional<TranslationDocument> findByCode(String s);
}
