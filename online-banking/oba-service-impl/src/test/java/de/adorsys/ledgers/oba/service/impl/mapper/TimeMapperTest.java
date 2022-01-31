/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.service.impl.mapper;


import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class TimeMapperTest {

    @Test
    void mapTime() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(OffsetDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));
        assertEquals(LocalTime.of(1, 1, 1, 1), result);
    }

    @Test
    void mapTime_null_time() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(null);
        assertNull(result);
    }
}
