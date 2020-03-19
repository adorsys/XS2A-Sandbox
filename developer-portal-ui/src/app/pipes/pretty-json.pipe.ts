import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'prettyJson',
})
export class PrettyJsonPipe implements PipeTransform {
  transform(value: any): any {
    return JSON.stringify(value, null, 4);
  }
}
