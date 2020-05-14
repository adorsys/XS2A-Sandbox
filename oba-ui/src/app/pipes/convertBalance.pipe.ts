import { Pipe, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Pipe({ name: 'convertBalance' })
export class ConvertBalancePipe implements PipeTransform {
  transform(value: number): String {
    const pipe = new DecimalPipe('en-US');
    const balance = '' + pipe.transform(value, '1.2-2');
    const balances = balance.split('.');
    return balances[0].replace(/,/g, '.') + ',' + balances[1];
  }
}
