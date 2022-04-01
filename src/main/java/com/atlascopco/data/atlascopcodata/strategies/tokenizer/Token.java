/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.strategies.tokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {

    /*
    TK_MINUS ("-"),
    TK_PLUS ("\\+"),
    TK_MUL ("\\*"),
    TK_DIV ("/"),
    TK_NOT ("~"),
    TK_AND ("&"),
    TK_OR ("\\|"),
    TK_LESS ("<"),
    TK_LEG ("<="),
    TK_GT (">"),
    TK_GEQ (">="),
    TK_EQ ("=="),
    TK_ASSIGN ("="),
    TK_OPEN ("\\("),
    TK_CLOSE ("\\)"),
    TK_SEMI (";"),
    TK_COMMA (","),
    TK_KEY_DEFINE ("define"),
    TK_KEY_AS ("as"),
    TK_KEY_IS ("is"),
    TK_KEY_IF ("if"),
    TK_KEY_THEN ("then"),
    TK_KEY_ELSE ("else"),
    TK_KEY_ENDIF ("endif"),
    OPEN_BRACKET ("\\{"),
    CLOSE_BRACKET ("\\}"),
    DIFFERENT ("<>"),
     */

    SEP ("\\,"),
    STRING ("\"[^\"]+\""),
    REAL ("(\\d+)\\.\\d+"),
    //INTEGER ("\\d"),
    IDENTIFIER ("[^\\s]+");

    private final Pattern pattern;

    Token(String regex) {
        pattern = Pattern.compile("^" + regex);
    }

    int endOfMatch(String s) {
        Matcher m = pattern.matcher(s);

        if (m.find()) {
            return m.end();
        }
        return -1;
    }
}