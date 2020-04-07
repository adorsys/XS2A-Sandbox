import { Pipe, PipeTransform } from '@angular/core';
import { JSON_SPACING } from '../components/common/constant/constants';

@Pipe({
  name: 'prettyJson',
})
export class PrettyJsonPipe implements PipeTransform {
  transform(value: any): any {
    return JSON.stringify(value, null, JSON_SPACING);
  }
}
