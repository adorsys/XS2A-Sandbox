import { Injectable } from '@angular/core';
import {
  NgbDateAdapter,
  NgbDateStruct
} from '@ng-bootstrap/ng-bootstrap';
import { format as dateFormat, isValid, parse } from 'date-fns';

@Injectable()
export class CustomNgbDateAdapter extends NgbDateAdapter<Date> {
  fromModel(date: Date): NgbDateStruct {
    return date && date.getFullYear
      ? { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() }
      : null;
  }

  toModel(date: NgbDateStruct): Date {
    return date ? new Date(Date.UTC(date.year, date.month - 1, date.day)) : null;
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
  console.log({
    year: dateObject.getFullYear(),
    month: dateObject.getMonth() + 1,
    day: dateObject.getDate()
  });
  return {
    year: dateObject.getFullYear(),
    month: dateObject.getMonth() + 1,
    day: dateObject.getDate()
  };
}

export function ngbDateToString(date: NgbDateStruct, format: string = 'yyyy-MM-dd'): string {
  if (!date) {
    return '';
  }

  const dateObject = new Date(date.year, date.month - 1, date.day);
  if (!isValid(dateObject)) {
    return '';
  }

  console.log(dateFormat(dateObject, format));
  return dateFormat(dateObject, format);
}
