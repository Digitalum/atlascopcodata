/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller.paging.datatable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Column {

    private String data;
    private String name;
    private Boolean searchable;
    private Boolean orderable;
    private Search search;

    public Column(String data) {
        this.data = data;
    }
}
