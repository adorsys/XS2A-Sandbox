import { TestBed, waitForAsync } from '@angular/core/testing';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ngbDateToString, stringToNgbDate } from './ngb-datepicker-utils';

describe('ngb-datepicker-utils', () => {
  const dateInNumber: NgbDateStruct = {
    year: 2010,
    month: 5,
    day: 12,
  };

  const dateInString = '2010-05-12';

  it('should convert ngbDate into String', () => {
    const date = ngbDateToString(dateInNumber, 'yyyy-MM-dd');
    expect(date).toEqual(dateInString);
  });

  it('should convert String into ngbDate', () => {
    const date = stringToNgbDate('2010-05-12', 'yyyy-MM-dd');
    expect(date).toEqual(dateInNumber);
  });
});
