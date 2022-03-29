/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.dao;

import com.atlascopco.data.atlascopcodata.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findAllByType(Token.TokenType type);

    Optional<Token> findByUuid(String uuid);

    Optional<Token> findByCode(String code);

}
