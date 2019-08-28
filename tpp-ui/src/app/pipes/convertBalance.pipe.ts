import { Pipe, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Pipe({ name: 'convertBalance' })
export class ConvertBalancePipe implements PipeTransform {
    transform(value: number):String {
        let pipe = new DecimalPipe("en-US");
        let balance = ""+pipe.transform(value, "1.2-2");
        let balances = balance.split(".");
        balance = balances[0].replace(/,/g,'.')+","+balances[1];
        console.log(balance);
        return balance;
    }
}