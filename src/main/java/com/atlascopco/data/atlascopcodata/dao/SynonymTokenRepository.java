/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dao;

import com.atlascopco.data.atlascopcodata.model.SynonymTokenGroup;
import com.atlascopco.data.atlascopcodata.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SynonymTokenRepository extends JpaRepository<SynonymTokenGroup, String> {

    List<SynonymTokenGroup> findAllByTokens(Token token);
}
