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

import { Injectable } from '@angular/core';
import { NgbDateAdapter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { format as dateFormat, isValid, parse } from 'date-fns';

@Injectable()
export class CustomNgbDateAdapter extends NgbDateAdapter<Date> {
  fromModel(date: Date): NgbDateStruct {
    return date && date.getFullYear
      ? {
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate(),
        }
      : null;
  }

  toModel(date: NgbDateStruct): Date {
    return date
      ? new Date(Date.UTC(date.year, date.month - 1, date.day))
      : null;
  }
}

export function stringToNgbDate(value: string, format: string = 'yyyy-MM-dd') {
  if (!value) {
    return null;
  }

  const dateObject = parse(value, format, new Date(value));

  if (!isValid(dateObject)) {
    return null;
  }

  return {
    year: dateObject.getFullYear(),
    month: dateObject.getMonth() + 1,
    day: dateObject.getDate(),
  };
}

export function ngbDateToString(
  date: NgbDateStruct,
  format: string = 'yyyy-MM-dd'
): string {
  if (!date) {
    return '';
  }

  const dateObject = new Date(date.year, date.month - 1, date.day);
  if (!isValid(dateObject)) {
    return '';
  }

  return dateFormat(dateObject, format);
}
