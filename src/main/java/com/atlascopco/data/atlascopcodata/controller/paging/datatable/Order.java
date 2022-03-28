/*
 * Copyright (c) 2019 Atlas Copco. All rights reserved.
 */
package com.atlascopco.data.atlascopcodata.controller.paging.datatable;

import com.atlascopco.data.atlascopcodata.controller.paging.datatable.Direction;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    private Integer column;
    private Direction dir;
}